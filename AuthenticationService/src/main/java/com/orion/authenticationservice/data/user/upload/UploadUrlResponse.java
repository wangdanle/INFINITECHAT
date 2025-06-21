package com.orion.authenticationservice.data.user.upload;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/20
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UploadUrlResponse {
    private String uploadUrl;
    private String downloadUrl;
}
