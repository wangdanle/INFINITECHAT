package com.orion.momentservice.data.deleteMoment;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeleteMomentRequest {
    @NotEmpty(message = "朋友圈ID不能为空")
    private Long momentId;

    @NotEmpty(message = "操作者ID不能为空")
    private Long userId;
}
