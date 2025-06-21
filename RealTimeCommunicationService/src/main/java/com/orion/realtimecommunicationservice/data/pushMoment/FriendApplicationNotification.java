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
public class FriendApplicationNotification {

    private String applyUserName;
}
