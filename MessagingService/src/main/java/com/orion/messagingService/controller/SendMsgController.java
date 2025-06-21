package com.orion.messagingService.controller;


import com.orion.messagingService.common.Result;
import com.orion.messagingService.data.sendMsg.SendMsgRequest;
import com.orion.messagingService.data.sendMsg.SendMsgResponse;
import com.orion.messagingService.feign.ContactSerivceFeign;
import com.orion.messagingService.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/3
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
public class SendMsgController {
    @Autowired
    private ContactSerivceFeign contactSerivceFeign;

    @Autowired
    private MessageService messageService;

    @PostMapping("/session")
    public Result<SendMsgResponse> sendMsg(@RequestBody SendMsgRequest request){
        SendMsgResponse response = messageService.sendMessage(request);
        return Result.OK(response);
    }


}
