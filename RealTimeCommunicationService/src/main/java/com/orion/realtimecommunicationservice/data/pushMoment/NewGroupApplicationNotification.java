package com.orion.realtimecommunicationservice.data.pushMoment;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/12
 * @Description:
 */
@Data
@Accessors(chain = true)
public class NewGroupApplicationNotification {

    private String creatorId;

    private String sessionId;

    // 1 单聊，2 群聊
    private Integer sessionType;

    private String sessionName;

    private String avatar;
}
