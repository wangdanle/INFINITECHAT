package com.orion.momentservice.data.getMomentList;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class MomentLikeVO {
    private Long likeId;

    private Long momentId;

    private Long userId;

    private String userName;

    private String userAvatar;
}
