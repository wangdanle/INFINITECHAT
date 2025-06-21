package com.orion.momentservice.data.createMoment;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/8
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CreateMomentResponse {
    private Long momentId;

    private Long userId;

    private String text;

    private List<String> mediaUrls;
}
