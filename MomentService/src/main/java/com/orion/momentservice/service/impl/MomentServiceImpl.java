package com.orion.momentservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.momentservice.constants.ConfigEnum;
import com.orion.momentservice.constants.ErrorEnum;
import com.orion.momentservice.data.createMoment.CreateMomentRequest;
import com.orion.momentservice.data.createMoment.CreateMomentResponse;
import com.orion.momentservice.data.deleteMoment.DeleteMomentRequest;
import com.orion.momentservice.data.deleteMoment.DeleteMomentResponse;
import com.orion.momentservice.data.getMomentList.*;
import com.orion.momentservice.exception.DataBaseException;
import com.orion.momentservice.exception.MomentException;
import com.orion.momentservice.mapper.MomentMapper;
import com.orion.momentservice.model.Moment;
import com.orion.momentservice.model.MomentComment;
import com.orion.momentservice.model.MomentLike;
import com.orion.momentservice.model.User;
import com.orion.momentservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Zzw
 * @description 针对表【moment(朋友圈)】的数据库操作Service实现
 * @createDate 2024-10-08 11:59:32
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    private final static Gson GSON = new Gson();

    private final FriendService friendService;

    private final UserService userService;

    private final MomentNotificationService momentNotificationService;

    private final @Lazy MomentLikeService momentLikeService;

    private final @Lazy MomentCommentService momentCommentService;


    /**
     * 创建朋友圈
     *
     * @param request
     * @return
     */
    @Override
    public CreateMomentResponse createMoment(CreateMomentRequest request) {
        CreateMomentResponse response = createMomentWithNotification(request.getUserId(), request.getText(), request.getMediaUrls());
        return response;
    }

    /**
     * 删除朋友圈
     *
     * @param request
     * @return
     */
    @Override
    public DeleteMomentResponse deleteMoment(DeleteMomentRequest request) {
        Moment moment = validateMomentOwnership(request.getUserId(), request.getMomentId());

        deleteAssociatedData(moment);

        moment.setDeleteTime(new Date());
        moment.setUpdateTime(new Date());
        boolean updated = this.updateById(moment);
        if (!updated) {
            throw new MomentException("更新朋友圈失败!");
        }

        return new DeleteMomentResponse().setMessage("删除朋友圈成功");
    }

    private void deleteAssociatedData(Moment moment) {
        deleteMomentLike(moment.getMomentId());
        deleteMomentComment(moment.getMomentId());
    }

    private void deleteMomentComment(Long momentId) {
        QueryWrapper<MomentComment> wrapper = new QueryWrapper<MomentComment>().eq("moment_id", momentId);
        momentCommentService.remove(wrapper);

    }

    private void deleteMomentLike(Long momentId) {
        QueryWrapper<MomentLike> wrapper = new QueryWrapper<MomentLike>().eq("moment_id", momentId);
        momentLikeService.remove(wrapper);
    }

    /**
     * @param momentId
     * @return
     */
    @Override
    public Long getMomentOwnerId(Long momentId) {
        QueryWrapper<Moment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("moment_id", momentId);
        Moment moment = this.getOne(queryWrapper);

        return moment.getUserId();
    }

    /**
     * 验证朋友圈是否是自己的
     *
     * @param momentId
     * @param userId
     * @return
     */
    private Moment validateMomentOwnership(Long momentId, Long userId) {
        QueryWrapper<Moment> queryWrapper = createMomentownerQuery(momentId, userId);
        Moment moment = this.getOne(queryWrapper);
        if (moment == null) {
            throw new MomentException(ErrorEnum.DELETE_MOMENT_FAILED_MSG);
        }
        return moment;
    }

    private QueryWrapper<Moment> createMomentownerQuery(Long momentId, Long userId) {
        return new QueryWrapper<Moment>().eq("user_id", userId).eq("moment_id", momentId);
    }

    private CreateMomentResponse createMomentWithNotification(long userId, String text, List<String> urls) {
        // 1.保存朋友圈
        CreateMomentResponse createMomentResponse = saveMoment(userId, text, urls);

        // 2.发现全部朋友圈好友
        User user = userService.getById(userId);
        List<Long> friendIds = friendService.getFriendIds(userId);

        // 3.发送朋友圈
        momentNotificationService.sendMomentCreationNotification(userId, user.getAvatar(), createMomentResponse.getMomentId(), friendIds);
        return createMomentResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    public CreateMomentResponse saveMoment(Long userId, String text, List<String> urls) {
        String mediaUrls = GSON.toJson(urls);
        Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setText(text);
        moment.setMediaUrl(mediaUrls);
        moment.setMomentId(snowflake.nextId());
        if (!this.save(moment)) {
            throw new DataBaseException(ErrorEnum.MOMENT_DATABASE_ERROR);
        }

        log.info("保存朋友圈成功，朋友圈信息为：{}", moment);
        CreateMomentResponse createMomentResponse = new CreateMomentResponse();
        BeanUtil.copyProperties(moment, createMomentResponse);
        createMomentResponse.setMediaUrls(urls);
        return createMomentResponse;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public GetMomentListResponse getMomentList(GetMomentListRequest request) {
        // 查询自己和自己的好友
        List<Long> friendIds = friendService.getFriendIds(request.getUserId());
        friendIds.add(request.getUserId());
        // 查询用户信息
        Map<Long, User> userInfoMap = getUserInfoMap(friendIds);
        // 查询朋友圈
        QueryWrapper<Moment> momentQueryWrapper = new QueryWrapper<>();
        momentQueryWrapper.in("user_id", friendIds).ge("update_time", request.getTime());
        List<Moment> momentList = this.list(momentQueryWrapper);
        List<Long> momentIds = new ArrayList<>();
        for (Moment moment : momentList) {
            momentIds.add(moment.getMomentId());
        }

        //查询点赞
        QueryWrapper<MomentLike> momentLikeQueryWrapper = new QueryWrapper<>();
        momentLikeQueryWrapper.in("moment_id", momentIds).ge("update_time", request.getTime());
        List<MomentLike> momentlikelist = momentLikeService.list(momentLikeQueryWrapper);

        //查询评论
        QueryWrapper<MomentComment> momentCommentQueryWrapper = new QueryWrapper<>();
        momentCommentQueryWrapper.in("moment_id", momentIds).ge("update_time", request.getTime());
        List<MomentComment> momentCommentlist = momentCommentService.list(momentCommentQueryWrapper);

        // 组装结果
        ArrayList<Long> deleteMoment = new ArrayList<>();
        ArrayList<Long> deleteLike = new ArrayList<>();
        ArrayList<Long> deleteComment = new ArrayList<>();

        ArrayList<MomentVO> createMoment = new ArrayList<>();
        ArrayList<MomentLikeVO> createLike = new ArrayList<>();
        ArrayList<MomentCommentVO> createComment = new ArrayList<>();

        for (Moment moment : momentList) {
            //不是自己的好友直接过滤掉
            if (!userInfoMap.containsKey(moment.getUserId())) {
                continue;
            }
            if (moment.getDeleteTime() != null) {
                deleteMoment.add(moment.getMomentId());
                continue;
            }
            User user = userInfoMap.get(moment.getUserId());
            MomentVO momentVO = new MomentVO();
            momentVO.setMomentId(moment.getMomentId());
            momentVO.setUserId(moment.getUserId());
            momentVO.setUserName(user.getUserName());
            momentVO.setUserAvatar(user.getAvatar());
            momentVO.setText(moment.getText());
            momentVO.setMediaUrl(GSON.fromJson(moment.getMediaUrl(), new TypeToken<List<String>>() {
            }.getType()));
            momentVO.setCreateTime(moment.getCreateTime());
            momentVO.setUpdateTime(moment.getCreateTime());
            momentVO.setDeleteTime(moment.getCreateTime());
            createMoment.add(momentVO);
        }

        for (MomentComment momentComment : momentCommentlist) {
            // 不是自己的好友直接过滤掉
            if (!userInfoMap.containsKey(momentComment.getUserId())) {
                continue;
            }
            if (momentComment.getIsDelete() != 0) {
                deleteComment.add(momentComment.getCommentId());
                continue;
            }
            User user = userInfoMap.get(momentComment.getUserId());
            MomentCommentVO momentCommentVO = new MomentCommentVO();
            momentCommentVO.setMomentId(momentComment.getCommentId());
            momentCommentVO.setMomentId(momentComment.getMomentId());
            momentCommentVO.setUserId(momentComment.getUserId());
            momentCommentVO.setUserName(user.getUserName());
            momentCommentVO.setParentCommentId(momentComment.getParentCommentId());
            momentCommentVO.setComment(momentComment.getComment());
            momentCommentVO.setCreateTime(momentComment.getCreateTime());
            momentCommentVO.setUpdateTime(momentComment.getCreateTime());
            createComment.add(momentCommentVO);
        }

        for (MomentLike momentLike : momentlikelist) {
            // 不是自己的好友直接过滤掉
            if (!userInfoMap.containsKey(momentLike.getUserId())) {
                continue;
            }
            if (momentLike.getIsDelete() != 0) {
                deleteLike.add(momentLike.getLikeId());
                continue;
            }
            User user = userInfoMap.get(momentLike.getUserId());
            MomentLikeVO like = new MomentLikeVO();
            like.setLikeId(momentLike.getLikeId());
            like.setMomentId(momentLike.getMomentId());
            like.setUserId(like.getUserId());
            like.setUserName(user.getUserName());
            like.setUserAvatar(user.getAvatar());
            createLike.add(like);
        }
        GetMomentListResponse response = new GetMomentListResponse();
        response.setCreateMoment(createMoment)
                .setCreateLike(createLike)
                .setCreateComment(createComment)
                .setDeleteMoment(deleteMoment)
                .setDeleteLike(deleteLike)
                .setDeleteComment(deleteComment);


        return response;
    }

    private List<Moment> queryMoments(List<Long> friendIds, String time) {
        return null;
    }


    private Map<Long, User> getUserInfoMap(List<Long> friendIds) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("user_id", friendIds);

        List<User> userList = userService.list(userQueryWrapper);
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getUserId(), user);
        }
        return userMap;
    }
}




