package com.orion.momentservice.data.deleteMoment;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeleteMomentResponse {
    private String message;
}
