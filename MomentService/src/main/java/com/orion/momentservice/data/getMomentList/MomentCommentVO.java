package com.orion.momentservice.data.getMomentList;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class MomentCommentVO implements Serializable {
    private Long commentId;

    private Long momentId;

    private Long userId;

    private String userName;

    private Long parentCommentId;

    private String comment;

    private Date createTime;

    private Date updateTime;
    }
