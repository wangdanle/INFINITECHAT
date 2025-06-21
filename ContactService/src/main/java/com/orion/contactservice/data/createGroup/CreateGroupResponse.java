package com.orion.contactservice.data.createGroup;


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
public class CreateGroupResponse {
    private String sessionId;

    private String sessionName;

    private Integer sessionType;

    private String avatar;

    private String creatorId;

    private List<String> failedMemberIds;
}
