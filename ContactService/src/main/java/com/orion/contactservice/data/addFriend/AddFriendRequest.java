package com.orion.contactservice.data.addFriend;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AddFriendRequest {
    private String msg;
}
