package com.orion.offlinedatastoreservice.data.offlineMessage;

import lombok.Data;
import java.io.Serializable;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OfflineMessageBody implements Serializable {

    private String content;

    private String createdAt;

    private String replyId;
}
