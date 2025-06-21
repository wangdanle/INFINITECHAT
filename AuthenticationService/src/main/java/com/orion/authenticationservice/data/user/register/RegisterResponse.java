package com.orion.authenticationservice.data.user.register;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
@Accessors(chain = true)
public class RegisterResponse {
    private String phone;
}
