package com.orion.offlinedatastoreservice.model;

import com.orion.offlinedatastoreservice.data.offlineMessage.OfflineMessageBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class RedPacketMessageBody extends OfflineMessageBody {

    private String redPacketWrapperText;
}
