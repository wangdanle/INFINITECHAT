package com.orion.authenticationservice.data.user.register;


import lombok.Data;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
public class RegisterRequest {
    private String phone;
    private String password;
    private String code;
}
