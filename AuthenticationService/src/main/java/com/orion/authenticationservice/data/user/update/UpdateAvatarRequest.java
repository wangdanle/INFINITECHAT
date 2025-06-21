package com.orion.authenticationservice.data.user.update;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/23
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UpdateAvatarRequest {

    @NotEmpty(message = "头像地址不能为空")
    private String avatarUrl;

}
