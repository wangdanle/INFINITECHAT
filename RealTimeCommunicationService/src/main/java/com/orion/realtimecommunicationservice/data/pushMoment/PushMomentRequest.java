package com.orion.realtimecommunicationservice.data.pushMoment;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/9
 * @Description:
 */
@Data
@Accessors(chain = true)
public class PushMomentRequest {
    private List<Long> receiveUserIds;

    private Integer noticeType;

    private String avatar;

    private Integer total;

}
