package com.orion.contactservice.service;

import com.orion.contactservice.data.createGroup.CreateGroupRequest;
import com.orion.contactservice.data.createGroup.CreateGroupResponse;
import com.orion.contactservice.model.Session;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【session(会话表)】的数据库操作Service
* @createDate 2025-06-13 11:46:17
*/
public interface SessionService extends IService<Session> {
    CreateGroupResponse createGroup(CreateGroupRequest request);
}
