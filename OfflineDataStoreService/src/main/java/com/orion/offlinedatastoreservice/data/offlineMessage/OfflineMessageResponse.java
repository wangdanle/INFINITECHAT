package com.orion.offlinedatastoreservice.data.offlineMessage;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/8
 * @Description:
 */
@Data
@Accessors(chain = true)
public class OfflineMessageResponse {
    private List<OfflineMessage> offlineMessage;
}
