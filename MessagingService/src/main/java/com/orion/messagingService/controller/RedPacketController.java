package com.orion.messagingService.controller;


import com.orion.messagingService.common.Result;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketRequest;
import com.orion.messagingService.data.receiveRedPacket.ReceiveRedPacketResponse;
import com.orion.messagingService.data.receiveRedPacket.RedPacketResponse;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketRequest;
import com.orion.messagingService.data.sendRedPacket.SendRedPacketResponse;
import com.orion.messagingService.service.RedPacketService;
import com.orion.messagingService.service.UserService;
import com.orion.messagingService.service.impl.GetRedPacketServiceImpl;
import com.orion.messagingService.utils.PreventDuplicateSubmit;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/5
 * @Description:
 */
@RestController
@RequestMapping("/api/v1/chat/redPacket")
public class RedPacketController {
    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private GetRedPacketServiceImpl getRedPacketService;

    @SneakyThrows
    @PreventDuplicateSubmit
    @PostMapping("/send")
    public Result<SendRedPacketResponse> sendRedPacket(@RequestBody SendRedPacketRequest request) {
        SendRedPacketResponse response = redPacketService.sendRedPacket(request);

        return Result.OK(response);
    }

    @SneakyThrows
    @PostMapping("/receive")
    public Result<ReceiveRedPacketResponse> receiveRedPacket(ReceiveRedPacketRequest request) {
        ReceiveRedPacketResponse response = redPacketService.receiveRedPacket(request);

        return Result.OK(response);
    }

    /**
     * 查询单个红包领取记录
     *
     * @param redPacketId 红包ID
     * @param pageNum     页码，默认为1
     * @param pageSize    每页大小，默认为10
     * @return 响应结果
     */
    @GetMapping("/{redPacketId}")
    public Result<RedPacketResponse> getRedPacket(@PathVariable Long redPacketId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        RedPacketResponse redPacketDetails = getRedPacketService.getRedPacketDetails(redPacketId, pageNum, pageSize);

        return Result.OK(redPacketDetails);
    }

}
