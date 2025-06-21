package com.orion.offlinedatastoreservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.orion.offlinedatastoreservice.mapper.SessionMapper;
import com.orion.offlinedatastoreservice.model.Session;
import com.orion.offlinedatastoreservice.service.SessionService;
import org.springframework.stereotype.Service;

/**
* @author 奇奇怪怪的沙小石
* @description 针对表【session(会话表)】的数据库操作Service实现
* @createDate 2024-11-04 17:54:30
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService {

}




