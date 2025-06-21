package com.orion.momentservice.data.getMomentList;


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
public class GetMomentListRequest {
    @NotEmpty(message = "用户ID不能为空")
    private Long userId;
    @NotEmpty(message = "时间不能为空")
    private String time;
}
