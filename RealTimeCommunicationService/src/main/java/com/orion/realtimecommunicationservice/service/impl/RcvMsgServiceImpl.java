package com.orion.realtimecommunicationservice.service.impl;


import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageRequest;
import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageResponse;
import com.orion.realtimecommunicationservice.service.RcvMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Slf4j
@Service
public class RcvMsgServiceImpl implements RcvMsgService {
    @Autowired
    private NettyMessageService nettyMessageService;

    @Override
    public ReceiveMessageResponse receiveMessage(ReceiveMessageRequest request) {
        nettyMessageService.sendMessageToUser(request);
        return new ReceiveMessageResponse();
    }
}
