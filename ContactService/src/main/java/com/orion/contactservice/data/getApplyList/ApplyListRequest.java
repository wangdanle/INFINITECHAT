package com.orion.contactservice.data.getApplyList;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/13
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ApplyListRequest {
    private Long userUuid;

    private int pageNum;

    private int pageSize;

    private String key;
}
