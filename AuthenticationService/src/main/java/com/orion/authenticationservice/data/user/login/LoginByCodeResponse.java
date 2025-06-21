package com.orion.authenticationservice.data.user.login;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
@Accessors(chain = true)
public class LoginByCodeResponse {
    private String userId;
    private String username;
    private String avatar;
    private String email;
    private String signature;
    private String gender;
    private String status;
    private String token;
}
