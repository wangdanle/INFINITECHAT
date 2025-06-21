package com.orion.offlinedatastoreservice.data.offlineMessage;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class OfflineMessageDetail implements Serializable {

    private String avatar;

    private OfflineMessageBody body;

    private Integer type;

    private String userName;

    private String sendUserId;

    private String messageId;
}
