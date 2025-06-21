package com.orion.realtimecommunicationservice.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/29
 * @Description:
 */
@Slf4j
@Data
@Accessors(chain = true)
@JsonPropertyOrder({"type","data"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {
    private Integer type;

    private Object data;
}
