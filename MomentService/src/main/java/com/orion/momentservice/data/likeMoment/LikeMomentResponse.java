package com.orion.momentservice.data.likeMoment;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/10
 * @Description:
 */
@Data
@Accessors(chain = true)
public class LikeMomentResponse {
    private boolean success;

    private long likeId;
}
