package com.orion.authenticationservice.data.user.update;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/23
 * @Description:
 */

@Data
@Accessors(chain = true)
public class UpdateAvatarResponse {
    private String userId;
    private String username;
    private String avatar;
    private String email;
    private String signature;
    private String gender;
    private String status;
    private String token;
}
