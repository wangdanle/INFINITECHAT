package com.orion.realtimecommunicationservice.model;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Data
@Accessors(chain = true)
public class PictureMessageBody {
    private Integer size;

    private String url;

    private String replyId;
}
