package com.orion.messagingService.data.sendMsg;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/3
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SendMsgRequest {
    private Long sessionId;

    private Long sendUserId;

    private Long receiveUserId;

    private Integer sessionType;

    private Integer type;

    private Long messageId;

    private Object body;
}
