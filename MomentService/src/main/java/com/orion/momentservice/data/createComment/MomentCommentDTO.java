package com.orion.momentservice.data.createComment;

import lombok.Data;
import java.io.Serializable;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class MomentCommentDTO implements Serializable {
    @NotEmpty(message = "评论人ID不能为空")
    private Long userId;
    @NotEmpty(message = "评论内容不能为空")
    private String comment;

    private Long parentCommentId;
}
