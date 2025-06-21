package com.orion.authenticationservice.data.user.login;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
@Data
public class LoginRequest {
    @NotEmpty(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号应为11位")
    private String phone;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码应为6-16位")
    private String password;
}
