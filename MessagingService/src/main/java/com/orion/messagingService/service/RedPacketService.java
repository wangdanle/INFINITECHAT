package com.orion.messagingService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketRequest;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketResponse;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketRequest;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketResponse;
import com.orion.messagingService.model.RedPacket;

public interface RedPacketService extends IService<RedPacket> {
    SendRedPacketResponse sendRedPacket(SendRedPacketRequest request);

    ReceiveRedPacketResponse receiveRedPacket(ReceiveRedPacketRequest request);

    void handleExpiredRedPacket(Long redPacketId);
}
