package com.orion.momentservice.data.deleteComment;


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
public class DeleteCommentRequest {
    @NotEmpty(message = "朋友圈ID不能为空")
    private Long momentId;
    @NotEmpty(message = "评论ID不能为空")
    private Long commentId;
    @NotEmpty(message = "用户ID不能为空")
    private Long userId;
}
