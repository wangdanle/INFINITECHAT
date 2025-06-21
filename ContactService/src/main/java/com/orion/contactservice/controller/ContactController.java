package com.orion.contactservice.controller;


import com.orion.contactservice.common.Result;
import com.orion.contactservice.data.addFriend.AddFriendRequest;
import com.orion.contactservice.data.addFriend.AddFriendResponse;
import com.orion.contactservice.data.createGroup.CreateGroupRequest;
import com.orion.contactservice.data.createGroup.CreateGroupResponse;
import com.orion.contactservice.data.getApplyList.ApplyListRequest;
import com.orion.contactservice.data.getApplyList.ApplyListResponse;
import com.orion.contactservice.data.searchUser.SearchUserRequest;
import com.orion.contactservice.data.searchUser.SearchUserResponse;
import com.orion.contactservice.service.ApplyFriendService;
import com.orion.contactservice.service.FriendService;
import com.orion.contactservice.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {
    @Autowired
    private FriendService friendService;

    @Autowired
    private ApplyFriendService applyFriendService;

    @Autowired
    private SessionService sessionService;


    @PostMapping("/{userUuid}/user")
    public Result<SearchUserResponse> searchUser(@Valid @ModelAttribute SearchUserRequest searchUserRequest) {
        SearchUserResponse response = friendService.searchUser(searchUserRequest);

        return Result.OK(response);
    }

    @PostMapping("/{userUuid}/friend/{receiveUserUuid}")
    public Result<AddFriendResponse> addFriend(
            @PathVariable("userUuid") String userUuid,
            @PathVariable("receiveUserUuid") String receiveUserUuid,
            @RequestBody AddFriendRequest request) throws Exception {
        AddFriendResponse response = applyFriendService.addFriend(userUuid, receiveUserUuid, request);

        return Result.OK(response);
    }

    @GetMapping( "/{userUuid}/apply")
    public Result<ApplyListResponse> getApplylist(@Valid @ModelAttribute ApplyListRequest request)  {
        ApplyListResponse response = applyFriendService.getApplyList(request);
        return Result.OK(response);
    }

    @PostMapping("/groups")
    public Result<CreateGroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request){
        CreateGroupResponse response = sessionService.createGroup(request);

        return Result.OK(response);
    }



}
