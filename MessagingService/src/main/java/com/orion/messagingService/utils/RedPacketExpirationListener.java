package com.orion.messagingService.utils;


import com.orion.messagingService.constants.RedPacketConstants;
import com.orion.messagingService.service.RedPacketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/7
 * @Description:
 */
@Service
@Slf4j
public class RedPacketExpirationListener implements MessageListener {
    @Autowired
    private RedPacketService redPacketService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith(RedPacketConstants.RED_PACKET_KEY_PREFIX.getValue())) {
            String redPacketIdStr = expiredKey.substring(RedPacketConstants.RED_PACKET_KEY_PREFIX.getValue().length());
            Long redPacketId = Long.parseLong(redPacketIdStr);

            log.info("得到过期的红包Id: " + redPacketId);
            try {
                redPacketService.handleExpiredRedPacket(redPacketId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
