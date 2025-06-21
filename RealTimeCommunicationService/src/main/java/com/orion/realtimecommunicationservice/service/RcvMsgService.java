package com.orion.realtimecommunicationservice.service;

import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageRequest;
import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageResponse;

public interface RcvMsgService {
    ReceiveMessageResponse receiveMessage(ReceiveMessageRequest request);
}
