package com.orion.contactservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.contactservice.constants.ConfigEnum;
import com.orion.contactservice.constants.ErrorEnum;
import com.orion.contactservice.constants.FriendApplicationStatus;
import com.orion.contactservice.constants.FriendRequestConstants;
import com.orion.contactservice.data.addFriend.AddFriendRequest;
import com.orion.contactservice.data.addFriend.AddFriendResponse;
import com.orion.contactservice.data.getApplyList.ApplyFriendDTO;
import com.orion.contactservice.data.getApplyList.ApplyListRequest;
import com.orion.contactservice.data.getApplyList.ApplyListResponse;
import com.orion.contactservice.exception.DataBaseException;
import com.orion.contactservice.exception.ServiceException;
import com.orion.contactservice.mapper.ApplyFriendMapper;
import com.orion.contactservice.model.ApplyFriend;
import com.orion.contactservice.model.User;
import com.orion.contactservice.model.push.FriendApplicationNotification;
import com.orion.contactservice.service.ApplyFriendService;
import com.orion.contactservice.service.PushService;
import com.orion.contactservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 好友申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyFriendServiceImpl extends ServiceImpl<ApplyFriendMapper, ApplyFriend> implements ApplyFriendService {
    private final UserService userService;

    private final PushService pushService;

    private final StringRedisTemplate redisTemplate;

    private final ApplyFriendMapper applyFriendMapper;

    /**
     * 发送好友申请
     *
     * @param userUuid        发送者用户UUID
     * @param receiveUserUuid 接收者用户UUID
     * @param request         申请信息
     * @return 是否成功
     */
    @Override
    @Transactional
    public AddFriendResponse addFriend(String userUuid, String receiveUserUuid, AddFriendRequest request) {
        Long senderId = Long.valueOf(userUuid);
        Long receiverId = Long.valueOf(receiveUserUuid);

        User sender = getUserById(senderId, FriendRequestConstants.APPLICANT_NOT_FOUND);
        User receiver = getUserById(senderId, FriendRequestConstants.APPLICANT_NOT_FOUND);

        FriendApplicationNotification notification = createNotification(sender);
        ApplyFriend existingApplyFriend = findExistingApplyFriend(senderId, receiverId);

        if (existingApplyFriend == null) {
            handleNewFriendApplication(senderId, receiverId, request.getMsg(), notification);
        } else if (existingApplyFriend.getStatus() == FriendApplicationStatus.ACCEPTED.getCode()) {
            throw new ServiceException(FriendRequestConstants.FRIEND_ALREADY_ADDED);
        } else {
            handleExistingFriendApplication(existingApplyFriend, request.getMsg(), notification);
        }

        return new AddFriendResponse();
    }

    /**
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public ApplyListResponse getApplyList(ApplyListRequest request) {
        ApplyListResponse response = new ApplyListResponse();

        Page<ApplyFriend> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<ApplyFriend> queryWrapper = buildApplyListQuery(request.getUserUuid(), request.getKey());

        Page<ApplyFriend> applyFriendPage = applyFriendMapper.selectPage(page, queryWrapper.orderByDesc(FriendRequestConstants.CREATED_AT));
        List<ApplyFriendDTO> dtoList = mapApplyFriendsToDTO(applyFriendPage.getRecords(), request.getUserUuid());

        response.setTotal((int)applyFriendPage.getTotal());
        response.setData(dtoList);

        return response;
    }

    /**
     * 将好友申请记录映射为DTO对象列表
     *
     * @param applyFriends 好友申请记录
     * @param userUuid     当前用户UUID
     * @return DTO对象列表
     */
    private List<ApplyFriendDTO> mapApplyFriendsToDTO(List<ApplyFriend> applyFriends, Long userUuid) {
        List<ApplyFriendDTO> dtoList = new ArrayList<>();
        for (ApplyFriend applyFriend : applyFriends) {
            ApplyFriendDTO dto = new ApplyFriendDTO();
            dto.setMsg(applyFriend.getMsg());
            dto.setStatus(applyFriend.getStatus());
            dto.setTime(applyFriend.getCreatedAt());

            if (applyFriend.getUserId().equals(userUuid)) {
                User targetUser = getUserById(applyFriend.getTargetId(), "接收者用户不存在");
                dto.setUserUuid(String.valueOf(targetUser.getUserId()));
                dto.setNickname(targetUser.getUserName());
                dto.setAvatar(targetUser.getAvatar());
                dto.setIsReceiver(FriendRequestConstants.IS_RECEIVER_NO);
            } else {
                User senderUser = getUserById(applyFriend.getUserId(), "发送者用户不存在");
                dto.setUserUuid(String.valueOf(senderUser.getUserId()));
                dto.setNickname(senderUser.getUserName());
                dto.setAvatar(senderUser.getAvatar());
                dto.setIsReceiver(FriendRequestConstants.IS_RECEIVER_YES);
            }

            dtoList.add(dto);
        }
        return dtoList;
    }

    private QueryWrapper<ApplyFriend> buildApplyListQuery(Long userUuid, String key) {
        QueryWrapper<ApplyFriend> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                wrapper.eq(FriendRequestConstants.USER_ID, userUuid)
                        .or()
                        .eq(FriendRequestConstants.TARGET_ID, userUuid)
        );

        if (key != null && !key.trim().isEmpty()) {
            queryWrapper.like(FriendRequestConstants.MSG, key);
        }

        return queryWrapper;
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId   用户ID
     * @param errorMsg 错误信息
     * @return 用户信息
     */
    private User getUserById(Long userId, String errorMsg) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new ServiceException(errorMsg);
        }
        return user;
    }

    /**
     * 创建好友申请通知
     *
     * @param applicant 申请者
     * @return 通知对象
     */
    private FriendApplicationNotification createNotification(User applicant) {
        FriendApplicationNotification notification = new FriendApplicationNotification();
        notification.setApplyUserName(applicant.getUserName());
        return notification;
    }

    /**
     * 查找现有的好友申请
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @return 现有的好友申请
     */
    private ApplyFriend findExistingApplyFriend(Long senderId, Long receiverId) {
        QueryWrapper<ApplyFriend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(FriendRequestConstants.USER_ID, senderId)
                .eq(FriendRequestConstants.TARGET_ID, receiverId);
        return this.getOne(queryWrapper);
    }

    /**
     * 处理新的好友申请
     *
     * @param senderId     发送者ID
     * @param receiverId   接收者ID
     * @param msg          申请信息
     * @param notification 通知对象
     * @return 是否成功
     */
    private boolean handleNewFriendApplication(Long senderId, Long receiverId, String msg, FriendApplicationNotification notification) {
        ApplyFriend newApplyFriend = createApplyFriend(senderId, receiverId, msg);
        boolean isSaved = this.save(newApplyFriend);
        if (isSaved) {
            setFriendRequestExpiration(newApplyFriend.getId());
        } else {
            throw new DataBaseException(ErrorEnum.DATABASE_ERR);
        }
        pushNotification(receiverId, notification);

        return isSaved;
    }

    /**
     * 处理已有的好友申请
     *
     * @param existingApplyFriend 现有的好友申请
     * @param msg                 申请信息
     * @param notification        通知对象
     * @return 是否成功
     */
    private boolean handleExistingFriendApplication(ApplyFriend existingApplyFriend, String msg, FriendApplicationNotification notification) {
        existingApplyFriend.setStatus(FriendApplicationStatus.UNREAD.getCode());
        existingApplyFriend.setMsg(msg);
        existingApplyFriend.setUpdatedAt(LocalDateTime.now());
        boolean isUpdated = this.updateById(existingApplyFriend);
        if (isUpdated) {
            setFriendRequestExpiration(existingApplyFriend.getId());
        }

        pushNotification(existingApplyFriend.getTargetId(), notification);
        return isUpdated;
    }

    /**
     * 创建一个新的好友申请对象
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param msg        申请信息
     * @return ApplyFriend 对象
     */
    private ApplyFriend createApplyFriend(Long senderId, Long receiverId, String msg) {
        ApplyFriend applyFriend = new ApplyFriend();
        applyFriend.setId(getSnowflake().nextId());
        applyFriend.setUserId(senderId);
        applyFriend.setTargetId(receiverId);
        applyFriend.setMsg(msg);
        applyFriend.setStatus(FriendApplicationStatus.UNREAD.getCode());
        return applyFriend;
    }

    /**
     * 推送好友申请通知
     *
     * @param receiverId   接收者ID
     * @param notification 通知对象
     */
    private void pushNotification(Long receiverId, FriendApplicationNotification notification) {
        try {
            pushService.pushNewApply(receiverId, notification);
        } catch (Exception e) {
            log.warn(FriendRequestConstants.PUSH_FAILURE_LOG, e.getMessage());
        }
    }

    /**
     * 设置好友申请在Redis中的过期键
     *
     * @param applyFriendId 好友申请ID
     */
    private void setFriendRequestExpiration(Long applyFriendId) {
        String redisKey = FriendRequestConstants.FRIEND_REQUEST_KEY_PREFIX + applyFriendId;
        redisTemplate.opsForValue().set(redisKey, FriendRequestConstants.ACTIVE_STATUS,
                FriendRequestConstants.FRIEND_REQUEST_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }


    public Snowflake getSnowflake() {
        return IdUtil.getSnowflake(
                Integer.parseInt(ConfigEnum.WORKED_ID.getValue()),
                Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
    }

}