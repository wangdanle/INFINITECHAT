package com.orion.realtimecommunicationservice.controller;


import com.orion.realtimecommunicationservice.common.Result;
import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageRequest;
import com.orion.realtimecommunicationservice.data.receiveMessage.ReceiveMessageResponse;
import com.orion.realtimecommunicationservice.service.RcvMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("api/v1/message")
public class RcvMsgController {
    @Autowired
    private RcvMsgService rcvMsgService;

    @PostMapping("/receive")
    public Result<ReceiveMessageResponse> receiveMessage(@Validated @RequestBody ReceiveMessageRequest request){
        ReceiveMessageResponse response = rcvMsgService.receiveMessage(request);
        return Result.OK(response);
    }
}
