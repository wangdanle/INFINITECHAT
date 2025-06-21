package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.model.Friend;
import com.orion.messagingService.service.FriendService;
import com.orion.messagingService.mapper.FriendMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【friend(联系人表)】的数据库操作Service实现
* @createDate 2025-06-03 23:17:51
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

}




