package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.model.Session;
import com.orion.messagingService.service.SessionService;
import com.orion.messagingService.mapper.SessionMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【session(会话表)】的数据库操作Service实现
* @createDate 2025-06-03 23:17:51
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{

}




