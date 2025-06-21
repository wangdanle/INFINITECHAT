package com.orion.realtimecommunicationservice.data.receiveMessage;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ReceiveMessageResponse {
    private String dummy = "OK";
}
