package com.orion.momentservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.momentservice.model.Friend;

import java.util.List;

/**
* @author Zzw
* @description 针对表【friend(联系人表)】的数据库操作Service
* @createDate 2024-10-08 15:09:48
*/
public interface FriendService extends IService<Friend> {
    List<Long> getFriendIds(Long userId);
}
