package com.orion.realtimecommunicationservice.controller;


import com.orion.realtimecommunicationservice.common.Result;
import com.orion.realtimecommunicationservice.data.pushMoment.FriendApplicationNotification;
import com.orion.realtimecommunicationservice.data.pushMoment.NewGroupApplicationNotification;
import com.orion.realtimecommunicationservice.data.pushMoment.PushMomentRequest;
import com.orion.realtimecommunicationservice.service.impl.NettyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/9
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/message/push")
public class PushController {
    @Autowired
    private NettyMessageService nettyMessageService;

    @PostMapping("/moment")
    public Result<?> receiveNoticeMoment(@RequestBody PushMomentRequest request) {
        nettyMessageService.sendNoticeMoment(request);

        return Result.OK(null);
    }

    /**
     * 发送好友申请通知
     *
     * @param notification 好友申请通知对象
     * @param userId       接收通知的用户ID
     */
    @PostMapping("/friendApplication/{userId}")
    public Result<?> pushFriendApplication(@PathVariable("userId") String userId, @RequestBody FriendApplicationNotification notification) {
        nettyMessageService.sendFriendApplicationNotification(notification, userId);

        return Result.OK(null);
    }

    /**
     * 群聊邀请通知
     *
     * @param notification    新群会话通知对象
     * @param userId 接收通知的用户ID
     */
    @PostMapping("/newGroupSession/{userId}")
    public Result pushNewGroupSession(@PathVariable("userId") String userId, @RequestBody NewGroupApplicationNotification notification
    ) {
        nettyMessageService.sendNewGroupSessionNotification(notification, userId);
        return Result.OK(null);
    }


}
