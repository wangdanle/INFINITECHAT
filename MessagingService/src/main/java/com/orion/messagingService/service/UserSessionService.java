package com.orion.messagingService.service;

import com.orion.messagingService.model.UserSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【user_session】的数据库操作Service
* @createDate 2025-06-03 23:17:51
*/
public interface UserSessionService extends IService<UserSession> {
    List<Long> getUserIdsBySessionId(Long sessionId);
}
