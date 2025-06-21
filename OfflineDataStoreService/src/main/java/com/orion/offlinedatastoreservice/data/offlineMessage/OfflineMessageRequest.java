package com.orion.offlinedatastoreservice.data.offlineMessage;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/8
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OfflineMessageRequest {
    @NotEmpty(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "时间不能为空")
    private String time;
}
