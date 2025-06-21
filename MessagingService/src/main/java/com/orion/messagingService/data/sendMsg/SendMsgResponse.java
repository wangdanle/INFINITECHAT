package com.orion.messagingService.data.sendMsg;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/3
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SendMsgResponse {
    private String sessionId;

    private List<Long> receiveUserIds;

    private Long senderId;

    private String userName;

    private String avatar;

    private Integer type;

    private Long messageId;

    private Integer sessionType;

    private String sessionName;

    private String sessionAvatar;

    private String createAt;

    private Object body;
}
