package com.orion.offlinedatastoreservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.offlinedatastoreservice.data.offlineMessage.*;
import com.orion.offlinedatastoreservice.exception.ServiceException;
import com.orion.offlinedatastoreservice.mapper.MessageMapper;
import com.orion.offlinedatastoreservice.mapper.RedPacketMapper;
import com.orion.offlinedatastoreservice.model.*;
import com.orion.offlinedatastoreservice.service.MessageService;
import com.orion.offlinedatastoreservice.service.SessionService;
import com.orion.offlinedatastoreservice.service.UserService;
import com.orion.offlinedatastoreservice.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Zzw
 * @description 针对表【message】的数据库操作Service实现
 * @createDate 2024-09-20 16:39:30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    private final UserService userService;

    private final RedPacketMapper redPacketMapper;

    private final SessionService sessionService;

    private final UserSessionService userSessionService;

    @Override
    public OfflineMessageResponse getOfflineMessage(OfflineMessageRequest request) {
        OfflineMessageResponse offlineMsgResponses = new OfflineMessageResponse();

        //  根据 userId 查找用户的会话
        Set<Long> sessionIds = userSessionService.findSessionIdByUserId(request.getUserId());
        if (CollectionUtils.isEmpty(sessionIds)) {
            return offlineMsgResponses;
        }

        // 根据所有的 sessionId 查找到所有的离线消息
        List<OfflineMessage> offlineMessages = new ArrayList<>();
        for (Long sessionId : sessionIds) {
            Session session = sessionService.getById(sessionId);
            OfflineMessage offlineMessage = new OfflineMessage();
            offlineMessage.setSessionId(sessionId.toString());
            offlineMessage.setSessionType(session.getType());
            if (session.getType() == 2) {
                offlineMessage.setSessionAvatar("http://47.115.130.44/img/avatar/IM_GROUP.jpg");
                offlineMessage.setSessionName(session.getName());
            }
            List<OfflineMessageDetail> offlineMsgDetails = findOfflineMsgBySessionId(sessionId, request.getTime());
            offlineMessage.setOfflineMsgDetails(offlineMsgDetails);
            offlineMessage.setTotal((long) offlineMsgDetails.size());
            if (offlineMsgDetails.size() > 0) {
                offlineMessages.add(offlineMessage);
            }
        }
        offlineMsgResponses.setOfflineMessage(offlineMessages);
        return offlineMsgResponses;
    }

    private List<OfflineMessageDetail> findOfflineMsgBySessionId(Long sessionId, String time) {
        LocalDateTime dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", sessionId).gt("created_at", dateTime);
        List<Message> messages = this.baseMapper.selectList(queryWrapper);
        log.info("messages:{}", messages);
        List<OfflineMessageDetail> offlineMsgDetails = new ArrayList<>();
        for (Message message : messages) {
            OfflineMessageDetail offlineMsgDetail = new OfflineMessageDetail();
            // 将消息中的内容 复制到body中期
            OfflineMessageBody body = buildMessageBody(message);
            offlineMsgDetail.setBody(body);

            User user = userService.getById(message.getSenderId());
            offlineMsgDetail.setUserName(user.getUserName());
            offlineMsgDetail.setAvatar(user.getAvatar());
            offlineMsgDetail.setMessageId(message.getMessageId().toString());
            offlineMsgDetail.setSendUserId(message.getSenderId().toString());
            offlineMsgDetail.setType(message.getType());
            offlineMsgDetails.add(offlineMsgDetail);
            log.info("offlineMsgDetail:{}", offlineMsgDetail);
        }

        return offlineMsgDetails;
    }

    private OfflineMessageBody buildMessageBody(Message message) {
        // 普通消息体
        OfflineMessageBody body = new OfflineMessageBody();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = message.getCreatedAt();
        body.setCreatedAt(formatter.format(date));
        body.setContent(message.getContent());
        Optional.ofNullable(message.getReplyId())
                .ifPresent(replyId -> body.setReplyId(replyId.toString()));

        // 红包消息特殊处理
        if (message.getType() == 5) {
            RedPacket redPacket = redPacketMapper.selectById(message.getContent());
            RedPacketMessageBody redPacketBody = new RedPacketMessageBody(redPacket.getRedPacketWrapperText());
            BeanUtils.copyProperties(body, redPacketBody);
            return redPacketBody;
        }

        return body;
    }

}




