package com.orion.messagingService.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.constants.SessionType;
import com.orion.messagingService.constants.UserConstants;
import com.orion.messagingService.data.sendMsg.AppMessage;
import com.orion.messagingService.data.sendMsg.SendMsgRequest;
import com.orion.messagingService.data.sendMsg.SendMsgResponse;
import com.orion.messagingService.exception.ServiceException;
import com.orion.messagingService.mapper.FriendMapper;
import com.orion.messagingService.mapper.MessageMapper;
import com.orion.messagingService.model.Friend;
import com.orion.messagingService.model.Message;
import com.orion.messagingService.model.Session;
import com.orion.messagingService.model.User;
import com.orion.messagingService.service.MessageService;
import com.orion.messagingService.service.SessionService;
import com.orion.messagingService.service.UserService;
import com.orion.messagingService.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Administrator
 * @description 针对表【message】的数据库操作Service实现
 * @createDate 2025-06-03 23:07:34
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {
    private static final int STATUS_ACTIVE = 1;

    private final UserService userService;

    private final UserSessionService userSessionService;

    private final SessionService sessionService;

    private final FriendMapper friendMapper;

    private final DiscoveryClient discoveryClient;

    private final OkHttpClient client = new OkHttpClient();

    private final ThreadPoolExecutor groupMessageExecutor;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public SendMsgResponse sendMessage(SendMsgRequest request) {
        // 1.判断用户是否真的存在
        validateSender(request.getSendUserId());

        // 2.判断是群聊还是单聊
        List<Long> receiveUserIds = getReceiveUserIds(request);

        validateReceiveUser(receiveUserIds);
        // 3.构建消息
        AppMessage appMessage = buildAppMessage(request, receiveUserIds);
        Long messageId = generatedMessageId();
        Date createdAt = new Date();

        appMessage.setMessageId(messageId);
        appMessage.setCreateAt(formatDate(createdAt));

        // todo: 发送到kafka

        // 4.通过Redis查询接收者的netty服务在哪
        sendRealTimeMessage(request, appMessage, createdAt);
        // 5.发消息

        return buildResponseMsgVo(appMessage);
    }

    private SendMsgResponse buildResponseMsgVo(AppMessage appMessage) {
        SendMsgResponse responseMsgVo = new SendMsgResponse();
        BeanUtils.copyProperties(appMessage, responseMsgVo);
//        responseMsgVo.setSessionId(appMessage.getSessionId().toString());
        responseMsgVo.setCreateAt(appMessage.getCreateAt());
        return responseMsgVo;
    }

    private void sendRealTimeMessage(SendMsgRequest request, AppMessage appMessage, Date createdAt) {
        String json = JSONUtil.toJsonStr(appMessage);
        String nettyServerIp = redisTemplate.opsForValue().get(UserConstants.USER_SESSION + request.getReceiveUserId());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        List<ServiceInstance> instances = discoveryClient.getInstances("RealTimeCommunicationService");

        if (CollectionUtils.isEmpty(instances)) {
            throw new ServiceException("没有可用的RealTimeCommunicationService服务实例!");
        }

        if (request.getSessionType() == SessionType.SINGLE.getValue()) {
            sendSingleMessage(request, requestBody, nettyServerIp);
        } else {
            sendGroupMessage(instances, requestBody, nettyServerIp);
        }

    }

    private void sendGroupMessage(List<ServiceInstance> instances, RequestBody requestBody, String token) {
        for (ServiceInstance instance : instances) {
            groupMessageExecutor.submit(() -> {
                String url = instance.getUri().toString();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    executeHttpRequest(request);
                    log.info("成功发送群聊消息到:{}", url);
                } catch (Exception e) {
                    log.error("发送群聊消息到 {} 失败:{}", url, e.getMessage());
                }
            });
        }
    }

    private void sendSingleMessage(SendMsgRequest sendMsgRequest, RequestBody requestBody, String nettyServerIp) {
        String receiveUserId = sendMsgRequest.getReceiveUserId().toString();
        try {
            if (nettyServerIp != null) {
                Request request = new Request.Builder()
                        .url("http://" + nettyServerIp + ":10010/api/v1/message/receive")
                        .post(requestBody)
                        .build();
                executeHttpRequest(request);
            } else {
                log.info("接收者已下线:{}", receiveUserId);
            }
        } catch (Exception e) {
            log.error("发送单聊消息失败:{}", e.getMessage());
            throw new ServiceException("发送单聊消息失败");
        }
    }


    private void executeHttpRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(String.format("HTTP请求失败: 状态码=%d, 消息=%s",
                        response.code(), response.message()));
            }
            ResponseBody body = response.body();
            if (body == null) {
                log.warn("HTTP响应体为空");
                return;
            }

            try {
                String responseBody = body.string(); // 注意：string()方法只能调用一次
                log.info("HTTP响应内容: {}", responseBody);
            } finally {
                body.close();
            }
        }
    }

    private Long generatedMessageId() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        return snowflake.nextId();
    }

    private AppMessage buildAppMessage(SendMsgRequest request, List<Long> receiveUserIds) {
        AppMessage appMessage = new AppMessage();
        BeanUtils.copyProperties(request, appMessage);
        appMessage.setBody(request.getBody());
        appMessage.setReceiveUserIds(receiveUserIds);

        User sendUser = userService.getById(request.getSendUserId());
        appMessage.setAvatar(sendUser.getAvatar());
        appMessage.setUserName(sendUser.getUserName());

        Session session = sessionService.getById(request.getSessionId());
        log.info("会话ID:{}", request.getSessionId());
        log.info("会话信息:{}", session);

        if (appMessage.getSessionType() == SessionType.SINGLE.getValue()) {
            appMessage.setSessionAvatar(null);
            appMessage.setSessionName(null);
        } else {
            appMessage.setSessionAvatar(sendUser.getAvatar());
            appMessage.setSessionName(session.getName());
        }
        log.info("AppMessage:{}", appMessage);

        return appMessage;
    }

    private String formatDate(Date now) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(now);
    }

    /**
     * 验证接收用户
     *
     * @param receiveUserIds 接收用户id
     */
    private void validateReceiveUser(List<Long> receiveUserIds) {
        if (receiveUserIds == null || receiveUserIds.isEmpty()) {
            throw new ServiceException("接收者列表不能为空!");
        }
    }

    /**
     * 验证发送人
     *
     * @param sendUserId 发送用户ID
     */
    private void validateSender(Long sendUserId) {
        User sendUser = userService.getById(sendUserId);
        if (sendUser == null || sendUser.getStatus() != STATUS_ACTIVE) {
            throw new ServiceException("发送者状态异常!");
        }
    }


    /**
     * 获取接收用户id
     *
     * @param request 要求
     * @return {@link List<Long>}
     */
    private List<Long> getReceiveUserIds(SendMsgRequest request) {
        List<Long> receiveUserIds = new ArrayList<>();
        Long sendUserId = request.getSendUserId();
        if (request.getSessionType() == SessionType.SINGLE.getValue()) {
            Long receiveUserId = request.getReceiveUserId();
            receiveUserIds.add(receiveUserId);
            validateSingleSession(sendUserId, receiveUserId);
        } else {
            receiveUserIds.addAll(userSessionService.getUserIdsBySessionId(request.getSessionId()));
            log.info("群聊接收者列表:{}", receiveUserIds);
            boolean removed = receiveUserIds.remove(sendUserId);
            if (removed) {
                log.info("移除发送者之后的群聊接收者列表:{}", receiveUserIds);
            } else {
                throw new ServiceException("发送者不在群聊内");
            }
        }

        return receiveUserIds;
    }

    /**
     * 验证单个会话
     *
     * @param sendUserId    发送用户ID
     * @param receiveUserId 接收用户ID
     */
    private void validateSingleSession(Long sendUserId, Long receiveUserId) {
        User receiveUser = userService.getById(receiveUserId);
        if (receiveUser == null || receiveUser.getStatus() != STATUS_ACTIVE) {
            throw new ServiceException("接收者:receiveUserId,状态异常");
        }

        Friend friend = friendMapper.selectFriendShip(sendUserId, receiveUserId);
        log.info("发送者ID:{}, 接收者ID{}", sendUserId, receiveUserId);
        if (friend == null || friend.getStatus() != STATUS_ACTIVE) {
            throw new ServiceException("发送者:" + sendUserId + "与接收者:" + receiveUserId + "之间不是好友关系");
        }
    }
}




