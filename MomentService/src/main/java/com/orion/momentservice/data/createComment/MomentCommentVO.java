package com.orion.momentservice.data.createComment;

import lombok.Data;
import java.io.Serializable;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MomentCommentVO implements Serializable {
    private String parentUserName;

    private Long parentCommentId;

    private String userName;

    private String comment;

    private Long commentId;


}
