package com.orion.contactservice.data.searchUser;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SearchUserResponse {
    private String userUuid;

    private String nickName;

    private String avatar;

    private String email;

    private String phone;

    private String signature;

    private Integer gender;

    private Integer status;

    // 判断好友关系的一个ID
    private String sessionId;
}
