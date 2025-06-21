package com.orion.offlinedatastoreservice.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TextMessageBody {

    private String content;

    private Long replyId;
}
