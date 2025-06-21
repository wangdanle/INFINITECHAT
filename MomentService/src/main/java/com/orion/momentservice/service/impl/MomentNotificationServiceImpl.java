package com.orion.momentservice.service.impl;


import com.orion.momentservice.constants.NoticeMomentEnum;
import com.orion.momentservice.model.vo.MomentRTCVO;
import com.orion.momentservice.service.MomentNotificationService;
import com.orion.momentservice.utlis.SendOkHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/9
 * @Description:
 */
@Service
@Slf4j
public class MomentNotificationServiceImpl implements MomentNotificationService {
    @Autowired
    private SendOkHttpRequest sendOkHttpRequest;

    /**
     * @param senderUserId
     * @param momentId
     * @param receiverUserIds
     * @param avatar
     */
    @Override
    public void sendMomentCreationNotification(Long senderUserId, String avatar, Long momentId, List<Long> receiverUserIds) {
        log.debug("开始发送朋友圈创建通知,用户ID={},朋友圈ID={}", senderUserId, momentId);

        MomentRTCVO momentRTCVO = new MomentRTCVO();
        momentRTCVO.setReceiveUserIds(receiverUserIds);
        momentRTCVO.setAvatar(avatar);
        momentRTCVO.setNoticeType(NoticeMomentEnum.CREATE_MOMENT_NOTICE.getValue());

        sendOkHttpRequest.sendNotification(momentRTCVO, senderUserId, NoticeMomentEnum.CREATE_MOMENT_NOTICE.getValue(), momentId);
        log.debug("朋友圈创建通知发送完成");
    }

    /**
     * @param senderUserId
     * @param momentId
     * @param receiverUserIds
     */
    @Override
    public void sendInteractionNotification(Long senderUserId, Long momentId, List<Long> receiverUserIds) {
        if (receiverUserIds == null ||  receiverUserIds.isEmpty()) {
            return;
        }

        MomentRTCVO momentRTCVO = new MomentRTCVO();
        momentRTCVO.setNoticeType(NoticeMomentEnum.CREATE_MOMENT_COMMENT_LIKE_NOTICE.getValue());
        momentRTCVO.setReceiveUserIds(receiverUserIds);

        sendOkHttpRequest.sendNotification(
                momentRTCVO,
                senderUserId,
                NoticeMomentEnum.CREATE_MOMENT_COMMENT_LIKE_NOTICE.getValue(),
                momentId
        );

        log.debug("朋友圈点赞或评论通知成功");
    }
}
