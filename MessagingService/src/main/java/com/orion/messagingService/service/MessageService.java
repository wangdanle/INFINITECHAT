package com.orion.messagingService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.messagingService.data.sendMsg.SendMsgRequest;
import com.orion.messagingService.data.sendMsg.SendMsgResponse;
import com.orion.messagingService.model.Message;

/**
 * @author Administrator
 * @description 针对表【message】的数据库操作Service
 * @createDate 2025-06-03 23:07:34
 */
public interface MessageService extends IService<Message> {
    SendMsgResponse sendMessage(SendMsgRequest request);
}
