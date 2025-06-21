package com.orion.momentservice.service.impl;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.momentservice.constants.ConfigEnum;
import com.orion.momentservice.constants.MomentConstants;
import com.orion.momentservice.data.createComment.CreateCommentRequest;
import com.orion.momentservice.data.createComment.CreateCommentResponse;
import com.orion.momentservice.data.createComment.MomentCommentDTO;
import com.orion.momentservice.data.createComment.MomentCommentVO;
import com.orion.momentservice.data.deleteComment.DeleteCommentRequest;
import com.orion.momentservice.mapper.MomentCommentMapper;
import com.orion.momentservice.model.MomentComment;
import com.orion.momentservice.model.User;
import com.orion.momentservice.service.MomentCommentService;
import com.orion.momentservice.service.MomentNotificationService;
import com.orion.momentservice.service.MomentService;
import com.orion.momentservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zzw
 * @description 针对表【moment_comment(朋友圈评论)】的数据库操作Service实现
 * @createDate 2024-10-08 16:37:48
 */
@Service
@Slf4j
public class MomentCommentServiceImpl extends ServiceImpl<MomentCommentMapper, MomentComment>
        implements MomentCommentService {

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy //使用延迟加载避免循环依赖
    private MomentService momentService;

    @Autowired
    private MomentNotificationService momentNotificationService;

    @Override
    public CreateCommentResponse createComment(CreateCommentRequest request) {
        MomentCommentVO momentCommentVO = createCommentwithNotifiication(request.getMomentId(), request.getMomentCommentDTO());
        CreateCommentResponse response = new CreateCommentResponse();
        BeanUtils.copyProperties(momentCommentVO, response);
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public MomentCommentVO createCommentwithNotifiication(Long momentId, MomentCommentDTO momentCommentDTO) {
        // 1. 创建并保存评论
        MomentComment momentComment = createMomentComment(momentId, momentCommentDTO);
        boolean save = this.save(momentComment);
        if (!save) {
            log.error("评论保存失败:朋友圈ID:{},用户ID:{}", momentId, momentCommentDTO.getUserId());
            throw new DatabaseException("评论保存失败");
        }
        // 2. 构建返回视图对象
        MomentCommentVO momentCommentVO = buildMomentCommentVO(momentComment, momentCommentDTO);

        // 3. 发送通知给朋友圈所有者（如果不是自己评论自己）
        sendNotificationToMomentOwner(momentId, momentCommentDTO);


        return momentCommentVO;
    }

    /**
     * 发送通知给朋友圈所有者（如果不是自己评论自己）
     * @param momentId
     * @param momentCommentDTO
     */
    private void sendNotificationToMomentOwner(Long momentId, MomentCommentDTO momentCommentDTO) {
        Long momentOwnerId = momentService.getMomentOwnerId(momentId);
        List<Long> receiveIds = new ArrayList<>();
        if (momentOwnerId != null && !momentOwnerId.equals(momentCommentDTO.getUserId())) {
            receiveIds.add(momentOwnerId);
            momentNotificationService.sendInteractionNotification(momentCommentDTO.getUserId(), momentId, receiveIds);
        }
    }

    /**
     * 构建返回视图对象
     * @param momentComment
     * @param momentCommentDTO
     * @return
     */
    private MomentCommentVO buildMomentCommentVO(MomentComment momentComment, MomentCommentDTO momentCommentDTO) {
        MomentCommentVO momentCommentVO = new MomentCommentVO();
        BeanUtils.copyProperties(momentComment, momentCommentVO);

        User user = userService.getById(momentComment.getUserId());
        momentCommentVO.setUserName(user.getUserName());

        if (momentCommentDTO.getParentCommentId() != null) {
            setParentCommentInfo(momentCommentDTO.getParentCommentId(), momentCommentVO);
        }

        return momentCommentVO;
    }

    /**
     * 设置父评论ID
     * @param parentCommentId
     * @param commentVO
     */
    private void setParentCommentInfo(Long parentCommentId, MomentCommentVO commentVO) {
        MomentComment parentComment = this.getById(parentCommentId);
        if (parentComment != null) {
            Long parentUserid = parentComment.getUserId();
            User parentUser = userService.getById(parentUserid);
            commentVO.setParentUserName(parentUser.getUserName());
            commentVO.setParentCommentId(parentComment.getCommentId());
        }
    }


    public MomentComment createMomentComment(Long momentId, MomentCommentDTO momentCommentDTO) {
        MomentComment momentComment = new MomentComment();
        Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
        momentComment.setCommentId(snowflake.nextId());
        momentComment.setComment(momentCommentDTO.getComment());
        momentComment.setMomentId(momentId);
        momentComment.setUserId(momentCommentDTO.getUserId());
        momentComment.setIsDelete(MomentConstants.NOT_DELETED);

        if (momentCommentDTO.getParentCommentId() != null) {
            momentComment.setParentCommentId(momentCommentDTO.getParentCommentId());
        }
        return momentComment;
    }

    @Override
    public boolean deleteComment(DeleteCommentRequest request) {
        long momentId = request.getMomentId();
        long userId = request.getUserId();
        long commentId = request.getCommentId();
        return deleteCommentRecursively(momentId, commentId, userId);
    }

    private boolean deleteCommentRecursively(Long momentId,  Long commentId, Long userId) {
        // 查询当前评论的所有子评论
        QueryWrapper<MomentComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("moment_id", momentId);
        queryWrapper.eq("parent_comment_id", commentId); // 假设有 parent_comment_id 字段
        List<MomentComment> childComments = this.list(queryWrapper);
        // 递归删除所有子评论
        for (MomentComment childComment : childComments) {
            deleteCommentRecursively(momentId, childComment.getCommentId(), childComment.getUserId());
        }
        // 逻辑删除当前评论
        QueryWrapper<MomentComment> queryWrapperParent = new QueryWrapper<>();
        queryWrapperParent.eq("moment_id", momentId);
        queryWrapperParent.eq("comment_id", commentId);
        queryWrapperParent.eq("user_id", userId);
        MomentComment momentComment = this.getOne(queryWrapperParent);
        if (momentComment != null) {
            momentComment.setIsDelete(MomentConstants.DELETED);
            momentComment.setUpdateTime(new Date());
            log.info(momentComment.toString());
            return this.update(momentComment, queryWrapperParent);
        }
        return false;
    }
}




