package com.orion.contactservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.contactservice.data.searchUser.SearchUserRequest;
import com.orion.contactservice.data.searchUser.SearchUserResponse;
import com.orion.contactservice.model.Friend;

public interface FriendService extends IService<Friend> {
    SearchUserResponse searchUser(SearchUserRequest request);
}
