package com.orion.contactservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.contactservice.data.addFriend.AddFriendRequest;
import com.orion.contactservice.data.addFriend.AddFriendResponse;
import com.orion.contactservice.data.getApplyList.ApplyListRequest;
import com.orion.contactservice.data.getApplyList.ApplyListResponse;
import com.orion.contactservice.model.ApplyFriend;


public interface ApplyFriendService extends IService<ApplyFriend> {
    /**
     * 添加好友
     */
    AddFriendResponse addFriend(String userUuid, String receiveUserUuid, AddFriendRequest request) throws Exception;

    ApplyListResponse getApplyList(ApplyListRequest request);
}