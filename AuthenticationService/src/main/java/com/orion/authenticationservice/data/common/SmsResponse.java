package com.orion.authenticationservice.data.common;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SmsResponse {
    private String phone;
}
