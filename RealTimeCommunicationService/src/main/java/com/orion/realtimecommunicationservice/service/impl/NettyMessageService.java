package com.orion.realtimecommunicationservice.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.orion.realtimecommunicationservice.constant.MessageRcvTypeEnum;
import com.orion.realtimecommunicationservice.constant.PushTypeEnum;
import com.orion.realtimecommunicationservice.data.pushMoment.FriendApplicationNotification;
import com.orion.realtimecommunicationservice.data.pushMoment.NewGroupApplicationNotification;
import com.orion.realtimecommunicationservice.data.pushMoment.PushMomentRequest;
import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageRequest;
import com.orion.realtimecommunicationservice.exception.ServiceException;
import com.orion.realtimecommunicationservice.model.*;
import com.orion.realtimecommunicationservice.websocket.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Slf4j
@Service
public class NettyMessageService {
    public void sendPush(PushTypeEnum type, Object data, String receiveUserUuid) {
        // 1.校验参数
        validateParameters(type, data, receiveUserUuid);
        // 2.构建消息体
        MessageDTO messageDTO = buildMessage(type, data);
        // 3.获取channel
        Channel userChannel = ChannelManager.getChannelByUserId(receiveUserUuid);
        log.info("channel:{}", userChannel);
        // 4、消息发送
        if (isChannelActive(userChannel)) {
            sendMessageThroughChannel(messageDTO, userChannel);
        } else {
            log.warn("用户 {} 的通道不可用或不活跃，消息推送失败: {}", receiveUserUuid, messageDTO);
        }
    }

    /**
     * 消息发送逻辑分离
     *
     * @param messageDTO 消息dto
     * @param channel    通道
     */
    private void sendMessageThroughChannel(MessageDTO messageDTO, Channel channel) {
        String jsonPayload = JSONUtil.toJsonStr(messageDTO);
        TextWebSocketFrame frame = new TextWebSocketFrame(jsonPayload);

        // 使用Lambda简化异步处理
        ChannelFuture future = channel.writeAndFlush(frame);

        // 分离日志处理逻辑
        future.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                if (log.isDebugEnabled()) {
                    log.debug("消息发送成功 [ID: {}]: {}", channel.id(), messageDTO);
                }
            } else {
                log.error("消息发送失败 [ID: {}]: {}", channel.id(), f.cause().getMessage());
                // 可在此添加重试逻辑或死信队列处理
            }
        });

        if (log.isDebugEnabled()) {
            log.debug("发送消息中 [ID: {}] - 类型: {}, 内容: {}",
                    channel.id(), messageDTO.getType(), jsonPayload);
        }
    }

    /**
     * 通道是活动的吗
     *
     * @param channel 通道
     * @return boolean
     */
    private boolean isChannelActive(Channel channel) {
        return channel != null && channel.isActive();
    }

    /**
     * 生成消息
     *
     * @param type 类型
     * @param data 数据
     * @return {@link MessageDTO}
     */
    private MessageDTO buildMessage(PushTypeEnum type, Object data) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(type.getCode());
        messageDTO.setData(data);
        return messageDTO;
    }

    /**
     * 检验参数
     *
     * @param type
     * @param data
     * @param receiveUserUuid
     */
    private static void validateParameters(PushTypeEnum type, Object data, String receiveUserUuid) {
        Objects.requireNonNull(type, "推送类型不能为空");
        Objects.requireNonNull(data, "推送数据不能为空");
        if (receiveUserUuid == null || receiveUserUuid.isEmpty()) {
            throw new ServiceException("接收用户ID不能为空");
        }
    }

    public void sendMessageToUser(ReceiveMessageRequest message) {
        switch (MessageRcvTypeEnum.fromCode(message.getType())) {
            case TEXT_MESSAGE:
                TextMessage textMessage = new TextMessage();
                BeanUtils.copyProperties(message, textMessage);
                TextMessageBody textBean = BeanUtil.toBean(message.getBody(), TextMessageBody.class);
                textMessage.setBody(textBean);
                log.info("textMessage:{} ", textMessage);
                List<Long> textReceiveUserIds = textMessage.getReceiveUserIds();
                textMessage.setReceiveUserIds(null);
                for (Long textReceiveUserId : textReceiveUserIds) {
                    log.info("textReceiveUser:{} ", textReceiveUserId);
                    log.info("是否存在管道:{} ", ChannelManager.getChannelByUserId(textReceiveUserId.toString()));
                    if (ChannelManager.getChannelByUserId(textReceiveUserId.toString()) != null) {
                        log.info("调用sendPush:{} ", textReceiveUserId);
                        sendPush(PushTypeEnum.fromCode(message.getType()), textBean, String.valueOf(textReceiveUserId));
                    }
                }
                break;
            case PICTURE_MESSAGE:
                PictureMessage pictureMessage = new PictureMessage();
                BeanUtils.copyProperties(message, pictureMessage);
                PictureMessageBody pictureBean = BeanUtil.toBean(message.getBody(), PictureMessageBody.class);
                pictureMessage.setBody(pictureBean);
                log.info("pictureMessage:{} ", pictureMessage);
                List<Long> pictureReceiveUserIds = pictureMessage.getReceiveUserIds();
                pictureMessage.setReceiveUserIds(null);
                for (Long pictureReceiveUser : pictureReceiveUserIds) {
                    log.info("pictureReceiveUser:{} ", pictureReceiveUser);
                    log.info("是否存在管道:{} ", ChannelManager.getChannelByUserId(pictureReceiveUser.toString()));
                    if (ChannelManager.getChannelByUserId(pictureReceiveUser.toString()) != null) {
                        log.info("调用sendPush:{} ", pictureReceiveUser);
                        sendPush(PushTypeEnum.fromCode(message.getType()), pictureBean, String.valueOf(pictureReceiveUser));
                    }
                }
                break;
        }

    }


    /**
     * 发送朋友圈通知
     *
     * @param request 要求
     */
    public void sendNoticeMoment(PushMomentRequest request) {
        List<Long> userIds = request.getReceiveUserIds();
        for (Long userId : userIds) {
            if (ChannelManager.getChannelByUserId(userId.toString()) != null) {
                request.setReceiveUserIds(null);
                sendPush(PushTypeEnum.MOMENT_NOTIFICATION, request, userId.toString());
            }
        }
    }

    /**
     * 发送好友申请通知
     *
     * @param notification 好友申请通知对象
     * @param userId       接收通知的用户ID
     */
    public void sendFriendApplicationNotification(FriendApplicationNotification notification, String userId) {
        sendPush(PushTypeEnum.FRIEND_APPLICATION_NOTIFICATION, notification, userId);
    }


    /**
     * 群聊邀请通知
     *
     * @param notification 通知
     * @param userId       用户ID
     */
    public void sendNewGroupSessionNotification(NewGroupApplicationNotification notification, String userId) {
        sendPush(PushTypeEnum.GROUP_APPLICATION_NOTIFICATION, notification, userId);
    }
}
