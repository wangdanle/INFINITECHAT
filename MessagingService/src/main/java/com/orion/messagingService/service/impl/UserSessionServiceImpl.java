package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.model.UserSession;
import com.orion.messagingService.service.UserSessionService;
import com.orion.messagingService.mapper.UserSessionMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
* @author Administrator
* @description 针对表【user_session】的数据库操作Service实现
* @createDate 2025-06-03 23:17:51
*/
@Service
public class UserSessionServiceImpl extends ServiceImpl<UserSessionMapper, UserSession>
    implements UserSessionService{

    @Override
    public List<Long> getUserIdsBySessionId(Long sessionId) {
        return Collections.emptyList();
    }
}




