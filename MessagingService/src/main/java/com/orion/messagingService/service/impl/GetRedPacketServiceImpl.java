package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.data.receiveRedPacket.RedPacketResponse;
import com.orion.messagingService.data.receiveRedPacket.RedPacketUser;
import com.orion.messagingService.exception.ServiceException;
import com.orion.messagingService.mapper.RedPacketMapper;
import com.orion.messagingService.mapper.RedPacketReceiveMapper;
import com.orion.messagingService.mapper.UserMapper;
import com.orion.messagingService.model.RedPacket;
import com.orion.messagingService.model.RedPacketReceive;
import com.orion.messagingService.model.User;
import com.orion.messagingService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GetRedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket> {

    @Autowired
    private RedPacketReceiveMapper redPacketReceiveMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取红包详细信息，包括领取记录
     *
     * @param redPacketId 红包ID
     * @param pageNum     页码
     * @param pageSize    每页大小
     * @return 红包详情响应
     */
    public RedPacketResponse getRedPacketDetails(Long redPacketId, Integer pageNum, Integer pageSize) {
        RedPacket redPacket = this.getById(redPacketId);
        if (redPacket == null) {
            throw new ServiceException("红包不存在");
        }

        // 1. 并行查询接收者和发送者信息（性能优化）
        CompletableFuture<List<RedPacketReceive>> receivesFuture = CompletableFuture.supplyAsync(() ->
                redPacketReceiveMapper.selectByRedPacketId(redPacketId, (pageNum - 1) * pageSize, pageSize)
        );

        CompletableFuture<User> senderFuture = CompletableFuture.supplyAsync(() ->
                userService.getById(redPacket.getSenderId())
        );

        // 2. 组合结果并构建响应
        try {
            List<RedPacketReceive> receives = receivesFuture.get();
            User sender = senderFuture.get();

            return new RedPacketResponse()
                    .setList(convertToUserList(receives))
                    .setSenderName(sender.getUserName())
                    .setSenderAvatar(sender.getAvatar())
                    .setRedPacketWrapperText(redPacket.getRedPacketWrapperText())
                    .setRedPacketType(redPacket.getRedPacketType())
                    .setTotalAmount(redPacket.getTotalAmount())
                    .setTotalCount(redPacket.getTotalCount())
                    .setRemainingAmount(redPacket.getRemainingAmount())
                    .setRemainingCount(redPacket.getRemainingCount())
                    .setStatus(redPacket.getStatus());
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new ServiceException("查询红包详情失败", e);
        }
    }

    /**
     * 将领取记录转换为用户列表
     *
     * @param receives 红包领取记录
     * @return 用户列表
     */
    private List<RedPacketUser> convertToUserList(List<RedPacketReceive> receives) {
        List<RedPacketUser> userList = new ArrayList<>();
        for (RedPacketReceive receive : receives) {
            User user = userMapper.selectById(receive.getReceiverId());
            userList.add(new RedPacketUser(user.getUserName(), user.getAvatar(), String.valueOf(receive.getReceivedAt()), receive.getAmount()));
        }
        return userList;
    }
}
