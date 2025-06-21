package com.orion.contactservice.data.searchUser;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SearchUserRequest {
    @NotNull(message = "用户ID不能为空")
    private String userUuid;

    @NotNull(message = "查找的手机号不能为空")
    private String phone;
}
