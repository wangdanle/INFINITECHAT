package com.orion.realtimecommunicationservice.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Data
@Accessors(chain = true)
public class Message {
    private List<Long> receiveUserIds;
    private String sendUserId;
    private String sessionId;
    private String avatar;
    private String userName;
    private Integer type;
    private String messageId;
    private Integer sessionType;
    private String sessionName;
    private String sessionAvatar;
    private String createAt;
    private Object body;
}
