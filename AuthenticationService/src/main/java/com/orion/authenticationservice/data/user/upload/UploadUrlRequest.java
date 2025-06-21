package com.orion.authenticationservice.data.user.upload;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
@Data
public class UploadUrlRequest {
    @NotEmpty(message = "文件名不能为空")
    private String fileName;
}
