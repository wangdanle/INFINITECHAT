package com.orion.offlinedatastoreservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.offlinedatastoreservice.data.offlineMessage.OfflineMessageRequest;
import com.orion.offlinedatastoreservice.data.offlineMessage.OfflineMessageResponse;
import com.orion.offlinedatastoreservice.model.Message;

import java.util.List;

/**
* @author Zzw
* @description 针对表【message】的数据库操作Service
* @createDate 2024-09-20 16:39:30
*/
public interface MessageService extends IService<Message> {
    OfflineMessageResponse getOfflineMessage(OfflineMessageRequest request);
}
