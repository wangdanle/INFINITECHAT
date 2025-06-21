package com.orion.momentservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MomentNotificationService {
    void sendMomentCreationNotification(Long senderUserId, String avatar, Long momentId, List<Long> receiverUserIds);

    void sendInteractionNotification(Long senderUserId, Long momentId, List<Long> receiverUserIds);
}
