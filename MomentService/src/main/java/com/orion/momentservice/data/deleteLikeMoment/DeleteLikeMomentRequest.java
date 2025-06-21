package com.orion.momentservice.data.deleteLikeMoment;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/10
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeleteLikeMomentRequest {
    @NotEmpty(message = "朋友圈ID不能为空")
    private Long momentId;
    @NotEmpty(message = "点赞ID不能为空")
    private Long likeId;
    @NotEmpty(message = "用户ID不能为空")
    private Long userId;
}
