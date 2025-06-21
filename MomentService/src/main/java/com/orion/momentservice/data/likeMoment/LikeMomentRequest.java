package com.orion.momentservice.data.likeMoment;


import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/10
 * @Description:
 */
@Data
@Accessors(chain = true)
public class LikeMomentRequest {
    @NotNull("用户ID不能为空")
    private Long userId;
}
