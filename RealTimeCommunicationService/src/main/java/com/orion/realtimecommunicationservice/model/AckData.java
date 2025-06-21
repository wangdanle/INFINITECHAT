package com.orion.realtimecommunicationservice.model;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/30
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AckData {
    private Long sessionId;

    private Long receiveUserUuid;

    private String msgUuid;
}
