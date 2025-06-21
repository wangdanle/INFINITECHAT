package com.orion.contactservice.data.getApplyList;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/13
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ApplyListResponse {
    private long total;

    private List<ApplyFriendDTO> data;
}
