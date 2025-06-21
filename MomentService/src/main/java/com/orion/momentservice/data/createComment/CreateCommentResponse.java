package com.orion.momentservice.data.createComment;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/10
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CreateCommentResponse {
    private String parentUserName;

    private Long parentCommentId;

    private String userName;

    private String comment;

    private Long commentId;


}
