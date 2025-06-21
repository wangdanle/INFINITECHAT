package com.orion.momentservice.service.impl;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.momentservice.constants.ConfigEnum;
import com.orion.momentservice.constants.MomentConstants;
import com.orion.momentservice.data.deleteLikeMoment.DeleteLikeMomentRequest;
import com.orion.momentservice.data.likeMoment.LikeMomentRequest;
import com.orion.momentservice.data.likeMoment.LikeMomentResponse;
import com.orion.momentservice.exception.DataBaseException;
import com.orion.momentservice.mapper.MomentLikeMapper;
import com.orion.momentservice.model.MomentLike;
import com.orion.momentservice.service.MomentLikeService;
import com.orion.momentservice.service.MomentNotificationService;
import com.orion.momentservice.service.MomentService;
import com.orion.momentservice.service.UserService;
import com.orion.momentservice.utlis.SendOkHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zzw
 * @description 针对表【moment_like(朋友圈点赞)】的数据库操作Service实现
 * @createDate 2024-10-08 15:50:26
 */
@Service
@Slf4j
public class MomentLikeServiceImpl extends ServiceImpl<MomentLikeMapper, MomentLike> implements MomentLikeService {
    @Autowired
    @Lazy
    private MomentService momentService;

    @Autowired
    private MomentNotificationService momentNotificationService;


    /**
     * @param request
     * @return
     */

    @Override
    public LikeMomentResponse createLikeWithNotification(Long momentId, LikeMomentRequest request) {
        // 1.创建点赞记录
        Long likeId = getMomentLike(momentId, request);
        // 2.拿到该条朋友圈的userId
        Long momentOwnerId = momentService.getMomentOwnerId(momentId);
        List<Long> receiverIds = new ArrayList<>();

        // 3.只有他人点赞才会发送通知
        if (momentOwnerId != null && !momentOwnerId.equals(request.getUserId())) {
            receiverIds.add(momentOwnerId);
            // 发送消息通知
            momentNotificationService.sendInteractionNotification(request.getUserId(), momentId, receiverIds);
        }

        // 返回响应
        LikeMomentResponse response = new LikeMomentResponse();
        response.setSuccess(true);
        response.setLikeId(likeId);
        return response;

    }

    @Transactional(rollbackFor = Exception.class)
    public Long getMomentLike(Long momentId, LikeMomentRequest request) {
        MomentLike like = new MomentLike();
        Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
        like.setLikeId(snowflake.nextId());
        like.setMomentId(momentId);
        like.setUserId(request.getUserId());
        like.setIsDelete(0);

        // 保存到数据库
        if (!this.save(like)) {
            log.error("朋友圈点赞保存失败,momentId: {}, userID: {}", momentId, request.getUserId());
            throw new DataBaseException("朋友圈点赞保存失败");
        }
        return like.getLikeId();
    }

    @Override
    public boolean deleteLike(DeleteLikeMomentRequest request) {
        QueryWrapper<MomentLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("moment_id", request.getMomentId());
        queryWrapper.eq("like_id", request.getLikeId());
        queryWrapper.eq("user_id", request.getUserId());
        MomentLike like = this.getOne(queryWrapper);
        like.setIsDelete(MomentConstants.DELETED);
        like.setUpdateTime(new Date());
        return this.update(like, queryWrapper);
    }

}




