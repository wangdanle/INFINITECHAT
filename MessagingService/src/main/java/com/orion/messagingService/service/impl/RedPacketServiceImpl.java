package com.orion.messagingService.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.constants.BalanceLogType;
import com.orion.messagingService.constants.RedPacketConstants;
import com.orion.messagingService.constants.RedPacketStatus;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketRequest;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketResponse;
import com.orion.messagingService.data.sendMsg.SendMsgRequest;
import com.orion.messagingService.data.sendMsg.SendMsgResponse;
import com.orion.messagingService.data.sendRedPacket.RedPacketMessageBody;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketRequest;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketResponse;
import com.orion.messagingService.exception.RedPacketValidationException;
import com.orion.messagingService.exception.ServiceException;
import com.orion.messagingService.mapper.BalanceLogMapper;
import com.orion.messagingService.mapper.RedPacketMapper;
import com.orion.messagingService.mapper.RedPacketReceiveMapper;
import com.orion.messagingService.mapper.UserBalanceMapper;
import com.orion.messagingService.model.BalanceLog;
import com.orion.messagingService.model.RedPacket;
import com.orion.messagingService.model.RedPacketReceive;
import com.orion.messagingService.model.UserBalance;
import com.orion.messagingService.service.MessageService;
import com.orion.messagingService.service.RedPacketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.orion.messagingService.constants.RedPacketConstants.RED_PACKET_KEY_PREFIX;

/**
 * @author Administrator
 * @description 针对表【red_packet(红包主表)】的数据库操作Service实现
 * @createDate 2025-06-05 16:53:21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket>
        implements RedPacketService {
    // 使用final关键字配合注解@RequiredArgsConstructor能够进行自动注入
    private final UserBalanceMapper userBalanceMapper;

    private final BalanceLogMapper balanceLogMapper;

    private final RedPacketReceiveMapper redPacketReceiveMapper;

    private final MessageService messageService;

    private final StringRedisTemplate redisTemplate;


    @Override
    @Transactional
    public SendRedPacketResponse sendRedPacket(SendRedPacketRequest request) {
        // 验证参数
        SendRedPacketRequest.Body body = request.getBody();
        validateRedPacketBody(body);

        Long sendUserId = Objects.requireNonNull(request.getSendUserId(), "发送人不能为空");
        BigDecimal totalAmount = body.getTotalAmount();
        int totalCount = body.getTotalCount();
        int redPacketType = body.getRedPacketType();

        // 检查发送者的金额
        duductUserBalance(getUserBalance(sendUserId), totalAmount);

        //创建红包记录
        RedPacket redPacket = createRedPacket(sendUserId, request, body, totalAmount, totalCount, redPacketType);

        // 记录余额变更日志
        createBalanceLog(sendUserId, totalAmount.negate(), BalanceLogType.SEND_RED_PACKET, redPacket.getRedPacketId());

        // 发送红包消息
        SendRedPacketResponse response = sendRedPacketMessage(request, redPacket);

        // 设置红包剩余个数到Reids
        setRedPacketCountToRedis(redPacket.getRedPacketId(), totalCount);

        return response;
    }

    @Override
    public ReceiveRedPacketResponse receiveRedPacket(ReceiveRedPacketRequest request) {
        // 检查用户是否已领取过红包，如果已领取则返回红包详情页
        BigDecimal amount = verifyUserHasNotReceived(request);
        if (amount != null) {
            return new ReceiveRedPacketResponse(amount, 0);
        }

        // 尝试抢红包
        Integer result = grabRedPacket(request);
        if (result.equals(RedPacketStatus.CLAIMED.getStatus())) {
            return new ReceiveRedPacketResponse(null, RedPacketStatus.CLAIMED.getStatus());
        }

        // 获取红包信息
        RedPacket redPacket = getRedPacketById(request);

        // 检查红包状态
        Integer status = validateRedPacketStatus(redPacket);
        if (status != 0) {
            return new ReceiveRedPacketResponse(null, status);
        }

        // 计算领取金额
        BigDecimal receivedAmount = computeReceivedAmount(redPacket);

        // 更新红包信息
        updateRedPacketInfo(redPacket, receivedAmount);

        // 插入领取记录
        LocalDateTime receiveTime = logRedPacketReceive(request, receivedAmount);

        // 更新用户余额
        adjustUserBalance(request, receivedAmount);

        // 记录余额变动日志
        logBalanceChange(request, receivedAmount);

        // 构建响应对象
        return new ReceiveRedPacketResponse(receivedAmount, status);
    }

    /**
     * 处理红包过期
     *
     * @param redPacketId 红包ID
     */
    @Override
    @Transactional
    public void handleExpiredRedPacket(Long redPacketId) {
        // 1. 获取红包信息，通过ID查询红包
        ReceiveRedPacketRequest request = new ReceiveRedPacketRequest();
        request.setRedPacketId(redPacketId);
        RedPacket redPacket = getRedPacketById(request);

        // 2. 判断红包的状态
        if (isNotRefundable(redPacket)) {
            log.info("红包不存在、已被领取完或已过期，红包ID: {}", redPacketId);
            return;
        }

        // 3. 退还余额（原子操作）
        refundToSender(redPacket);

        // 4. 更新红包状态
        markAsExpired(redPacket);
    }

    /**
     * 更新红包状态
     *
     * @param redPacket 红包
     */
    private void markAsExpired(RedPacket redPacket) {
        redPacket.setStatus(RedPacketStatus.EXPIRED.getStatus());
        if (!updateById(redPacket)) {
            throw new ServiceException("更新红包状态失败");
        }
        log.info("红包标记为已过期: ID={}", redPacket.getRedPacketId());
    }

    /**
     * 不需要退还红包
     *
     * @param redPacket
     * @return
     */
    private static boolean isNotRefundable(RedPacket redPacket) {
        return redPacket == null
                || !RedPacketStatus.UNCLAIMED.equals(redPacket.getStatus())
                || redPacket.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * 退还红包余额
     *
     * @param redPacket 红包
     */
    private void refundToSender(RedPacket redPacket) {
        Long senderId = redPacket.getSenderId();
        BigDecimal remainingAmount = redPacket.getRemainingAmount();

        // 使用乐观锁更新余额
        UserBalance userBalance = Optional.ofNullable(userBalanceMapper.selectById(senderId))
                .orElseThrow(() -> new ServiceException("用户账户不存在: ID=" + senderId));

        BigDecimal newBalance = userBalance.getBalance().add(remainingAmount);
        userBalance.setBalance(newBalance);

        int updatedRows = userBalanceMapper.updateById(userBalance);
        if (updatedRows != 1) {
            throw new ServiceException("退还余额失败: 并发冲突或记录不存在");
        }

        // 记录审计日志
        createBalanceLog(
                senderId,
                remainingAmount,
                BalanceLogType.REFUND_RED_PACKET,
                redPacket.getRedPacketId()
        );
    }

    /**
     * 记录用户余额变动日志。
     *
     * @param request        用户ID
     * @param receivedAmount 变动金额
     * @throws ServiceException 如果插入余额变动日志失败
     */
    private void logBalanceChange(ReceiveRedPacketRequest request, BigDecimal receivedAmount) {
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setBalanceLogId(generateId());
        balanceLog.setUserId(request.getUserId());
        balanceLog.setAmount(receivedAmount);
        balanceLog.setType(BalanceLogType.RECEIVE_RED_PACKET.getType());
        balanceLog.setRelatedId(request.getUserId());
        balanceLog.setCreatedAt(LocalDateTime.now());

        int insertResult = balanceLogMapper.insert(balanceLog);
        if (insertResult != 1) {
            throw new ServiceException("记录余额变动日志失败");
        }
    }

    /**
     * 更新用户余额。
     *
     * @param request        用户ID
     * @param receivedAmount 领取金额
     * @throws ServiceException 如果更新用户余额失败
     */
    private void adjustUserBalance(ReceiveRedPacketRequest request, BigDecimal receivedAmount) {
        UserBalance userBalance = userBalanceMapper.selectById(request.getUserId());
        if (userBalance == null) {
            throw new ServiceException("用户余额信息不存在");
        }

        userBalance.setBalance(userBalance.getBalance().add(receivedAmount));
        userBalance.setUpdatedAt(LocalDateTime.now());

        int updateResult = userBalanceMapper.updateById(userBalance);
        if (updateResult != 1) {
            throw new ServiceException("更新用户余额失败");
        }
    }

    private LocalDateTime logRedPacketReceive(ReceiveRedPacketRequest request, BigDecimal receivedAmount) {
        RedPacketReceive receive = new RedPacketReceive();
        receive.setRedPacketReceiveId(generateId());
        receive.setRedPacketId(request.getRedPacketId());
        receive.setReceiverId(request.getUserId());
        receive.setAmount(receivedAmount);
        receive.setReceivedAt(LocalDateTime.now());

        int insertResult = redPacketReceiveMapper.insert(receive);
        if (insertResult != 1) {
            throw new ServiceException("红包领取记录插入失败");
        }
        return receive.getReceivedAt();
    }

    /**
     * 更新红包的剩余金额和数量，必要时更新红包状态为已领取完毕。
     *
     * @param redPacket      红包对象
     * @param receivedAmount 领取金额
     * @throws ServiceException 如果更新红包失败
     */
    private void updateRedPacketInfo(RedPacket redPacket, BigDecimal receivedAmount) throws ServiceException {
        redPacket.setRemainingAmount(redPacket.getRemainingAmount().subtract(receivedAmount));
        redPacket.setRemainingCount(redPacket.getRemainingCount() - 1);

        if (redPacket.getRemainingCount() == 0) {
            redPacket.setStatus(RedPacketStatus.CLAIMED.getStatus());
            redisTemplate.delete("red_packet:count:" + redPacket.getRedPacketId());
        }

        boolean updateSuccess = this.updateById(redPacket);
        if (!updateSuccess) {
            throw new ServiceException("更新红包信息失败");
        }
    }

    /**
     * 计算用户领取的红包金额，基于红包类型和剩余金额。
     *
     * @param redPacket 红包对象
     * @return BigDecimal 领取金额
     * @throws ServiceException 如果红包类型未知
     */
    private BigDecimal computeReceivedAmount(RedPacket redPacket) {
        if (Objects.equals(redPacket.getRedPacketType(), RedPacketConstants.RED_PACKET_TYPE_NORMAL.getIntValue())) {
            return calculateNormalRedPacket(redPacket);
        } else if (Objects.equals(redPacket.getRedPacketType(), RedPacketConstants.RED_PACKET_TYPE_RANDOM.getIntValue())) {
            return calculateRandomRedPacket(redPacket);
        } else {
            throw new ServiceException("未知的红包类型");
        }
    }

    /**
     * 生成指定范围内的随机金额。
     *
     * @param min 最小金额
     * @param max 最大金额
     * @return BigDecimal 随机金额
     */
    private BigDecimal generateRandomAmount(BigDecimal min, BigDecimal max) {
        BigDecimal range = max.subtract(min);
        BigDecimal randomInRange = range.multiply(BigDecimal.valueOf(Math.random()));
        BigDecimal randomAmount = min.add(randomInRange).setScale(RedPacketConstants.AMOUNT_SCALE.getDivideScale(), RoundingMode.DOWN);
        return randomAmount.compareTo(min) < 0 ? min : randomAmount;
    }


    /**
     * 计算拼手气红包的领取金额，随机分配。
     *
     * @param redPacket 红包对象
     * @return BigDecimal 领取金额
     */
    private BigDecimal calculateRandomRedPacket(RedPacket redPacket) {
        if (redPacket.getRemainingCount() == 1) {
            // 最后一个红包，领取剩余所有金额
            return redPacket.getRemainingAmount();
        } else {
            // 计算最大可领取金额
            BigDecimal maxAmount = redPacket.getRemainingAmount()
                    .divide(new BigDecimal(redPacket.getRemainingCount()), RedPacketConstants.DIVIDE_SCALE.getDivideScale(), RoundingMode.DOWN)
                    .multiply(RedPacketConstants.RANDOM_MULTIPLIER.getBigDecimalValue());
            return generateRandomAmount(RedPacketConstants.MIN_AMOUNT.getBigDecimalValue(), maxAmount);
        }
    }

    /**
     * 计算普通红包的领取金额，平均分配。
     *
     * @param redPacket 红包对象
     * @return BigDecimal 领取金额
     */
    private BigDecimal calculateNormalRedPacket(RedPacket redPacket) {
        return redPacket.getTotalAmount()
                .divide(new BigDecimal(redPacket.getTotalCount()), RedPacketConstants.DIVIDE_SCALE.getDivideScale(), RoundingMode.DOWN);
    }

    private Integer validateRedPacketStatus(RedPacket redPacket) {
        if (Objects.equals(redPacket.getStatus(), RedPacketStatus.EXPIRED.getStatus())) {
            return RedPacketStatus.EXPIRED.getStatus();
        }
        if (redPacket.getRemainingCount() <= 0) {
            return RedPacketStatus.CLAIMED.getStatus();
        }
        return 0;
    }

    /**
     * 获取红包信息，通过ID查询红包。
     */
    private RedPacket getRedPacketById(ReceiveRedPacketRequest request) throws ServiceException {
        RedPacket redPacket = this.getById(request.getRedPacketId());
        if (redPacket == null) {
            throw new ServiceException("红包不存在");
        }
        return redPacket;
    }

    private Integer grabRedPacket(ReceiveRedPacketRequest request) {
        String redPacketCountKey = RedPacketConstants.RED_PACKET_KEY_PREFIX.getValue() + request.getRedPacketId();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RedPacketConstants.RED_PACKET_LUA_SCRIPT.getValue());
        redisScript.setResultType(Long.class);
        try {
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(redPacketCountKey));
            if (result == null) {
                throw new IllegalStateException("Redis 脚本执行返回 null");
            }
            return result.intValue();
        } catch (Exception e) {
            throw new RuntimeException("执行 Redis Lua 脚本时出错", e);
        }
    }

    /**
     * 校验用户是否已领取过指定红包
     *
     * @param request 领取请求参数
     * @return 若未领取返回null，已领取则返回领取金额
     */
    private BigDecimal verifyUserHasNotReceived(ReceiveRedPacketRequest request) {
        QueryWrapper<RedPacketReceive> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("red_packet_id", request.getRedPacketId()).eq("receiver_id", request.getUserId());
        RedPacketReceive redPacketReceive = redPacketReceiveMapper.selectOne(queryWrapper);
        if (redPacketReceive == null) {
            return null;
        }
        return redPacketReceive.getAmount();
    }

    private void duductUserBalance(UserBalance userBalance, BigDecimal amount) {
        BigDecimal newBalance = userBalance.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("用户余额不足");
        }

        userBalance.setBalance(newBalance);
        int updateCount = userBalanceMapper.updateById(userBalance);
        if (updateCount != 1) {
            throw new ServiceException("余额扣减失败");
        }
    }

    private UserBalance getUserBalance(Long sendUserId) {
        UserBalance userBalance = userBalanceMapper.selectById(sendUserId);
        if (userBalance == null) {
            throw new ServiceException("用户金额信息不存在");
        }
        return userBalance;
    }

    private void createBalanceLog(Long sendUserId, BigDecimal amount, BalanceLogType balanceLogType, Long relatedId) {
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setBalanceLogId(generateId());
        balanceLog.setUserId(sendUserId);
        balanceLog.setAmount(amount);
        balanceLog.setType(balanceLogType.getType());
        balanceLog.setRelatedId(relatedId);
        balanceLog.setCreatedAt(LocalDateTime.now());

        balanceLogMapper.insert(balanceLog);
    }

    /**
     * 将红包计数设置为redis
     *
     * @param redPacketId 红包ID
     * @param totalCount  红包总数
     */
    private void setRedPacketCountToRedis(Long redPacketId, int totalCount) {
        String redisKey = RED_PACKET_KEY_PREFIX.getValue() + redPacketId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(totalCount), Duration.ofHours(RedPacketConstants.RED_PACKET_EXPIRE_HOURS.getIntValue()));
    }

    /**
     * 发送红包消息
     *
     * @param request   要求
     * @param redPacket 红包
     * @return {@link SendRedPacketResponse}
     */
    private SendRedPacketResponse sendRedPacketMessage(SendRedPacketRequest request, RedPacket redPacket) {
        SendMsgRequest sendMsgRequest = new SendMsgRequest();
        BeanUtils.copyProperties(request, sendMsgRequest);

        RedPacketMessageBody redPacketMessageBody = RedPacketMessageBody.builder().build()
                .setRedPacketWrapperText(redPacket.getRedPacketWrapperText())
                .setContent(String.valueOf(redPacket.getRedPacketId()));

        sendMsgRequest.setBody(redPacketMessageBody);

        try {
            SendMsgResponse sendMsgResponse = messageService.sendMessage(sendMsgRequest);
            SendRedPacketResponse sendRedPacketResponse = new SendRedPacketResponse();
            BeanUtils.copyProperties(sendMsgResponse, sendRedPacketResponse);
            return sendRedPacketResponse;
        } catch (Exception e) {
            log.error("发送红包消息失败,红包ID:" + redPacket.getRedPacketId(), e);
            throw new ServiceException("发送红包消息失败");
        }
    }

    private RedPacket createRedPacket(Long sendUserId, SendRedPacketRequest request, SendRedPacketRequest.Body body, BigDecimal totalAmount, int totalCount, int redPacketType) {
        RedPacket redPacket = new RedPacket();

        redPacket.setRedPacketId(generateId());
        redPacket.setSenderId(sendUserId);
        redPacket.setSessionId(request.getSessionId());
        String redPacketWrapperText = body.getRedPacketWrapperText();
        if (redPacketWrapperText == null || redPacketWrapperText.trim().isEmpty()) {
            redPacket.setRedPacketWrapperText("恭喜发财, 大吉大利!");
        } else {
            redPacket.setRedPacketWrapperText(redPacketWrapperText);
        }

        redPacket.setRedPacketWrapperText(redPacketWrapperText);
        redPacket.setRedPacketType(redPacketType);
        redPacket.setTotalAmount(totalAmount);
        redPacket.setTotalCount(totalCount);
        redPacket.setRemainingAmount(totalAmount);
        redPacket.setRemainingCount(totalCount);
        redPacket.setStatus(RedPacketStatus.UNCLAIMED.getStatus());
        redPacket.setCreatedAt(DateUtil.date());

        return redPacket;
    }

    private Long generateId() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return snowflake.nextId();
    }

    /**
     * 验证红包参数
     *
     * @param body 身体
     */
    private void validateRedPacketBody(SendRedPacketRequest.Body body) {
        if (body == null) {
            throw new RedPacketValidationException("请求体不能为空");
        }

        BigDecimal totalAmount = Objects.requireNonNull(body.getTotalAmount(), "红包总金额不能为空");
        int totalCount = body.getTotalCount();
        int redPacketType = body.getRedPacketType();

        //  金额范围校验
        validateAmountRange(totalAmount);

        // 红包类型校验
        if (isValidRedPacketType(redPacketType)) {
            throw new RedPacketValidationException("红包类型无效，必须是普通红包或拼手气红包");
        }

        // 单红包最小金额校验
        BigDecimal minAmountPerPacket = totalAmount.divide(
                BigDecimal.valueOf(totalCount),
                RedPacketConstants.DIVIDE_SCALE.getIntValue(),
                RoundingMode.DOWN);

        validateAmountRange(minAmountPerPacket);
    }

    // 红包类型校验
    private boolean isValidRedPacketType(int type) {
        return type == RedPacketConstants.RED_PACKET_TYPE_NORMAL.getIntValue()
                || type == RedPacketConstants.RED_PACKET_TYPE_RANDOM.getIntValue();
    }

    // 复用金额校验逻辑
    private void validateAmountRange(BigDecimal amount) {
        if (amount.compareTo(RedPacketConstants.MIN_AMOUNT.getBigDecimalValue()) < 0) {
            throw new IllegalArgumentException("金额不能低于 " + RedPacketConstants.MIN_AMOUNT.getBigDecimalValue() + " 元");
        }
        if (amount.compareTo(RedPacketConstants.MAX_AMOUNT_PER_PACKET.getBigDecimalValue()) > 0) {
            throw new IllegalArgumentException("金额不能超过 " + RedPacketConstants.MAX_AMOUNT_PER_PACKET.getBigDecimalValue() + " 元");
        }
    }
}




