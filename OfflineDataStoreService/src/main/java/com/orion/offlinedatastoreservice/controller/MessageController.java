package com.orion.offlinedatastoreservice.controller;


import com.orion.offlinedatastoreservice.common.Result;
import com.orion.offlinedatastoreservice.data.offlineMessage.OfflineMessageRequest;
import com.orion.offlinedatastoreservice.data.offlineMessage.OfflineMessageResponse;
import com.orion.offlinedatastoreservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/8
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/offline")
@RequiredArgsConstructor
public class MessageController {

    private MessageService messageService;

    @GetMapping("/message")
    public Result<OfflineMessageResponse> getOfflineMessage(@RequestBody OfflineMessageRequest request) {
        OfflineMessageResponse response = new OfflineMessageResponse();

        return Result.OK(response);
    }
}
