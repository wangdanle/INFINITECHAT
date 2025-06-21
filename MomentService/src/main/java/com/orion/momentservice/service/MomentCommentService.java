package com.orion.momentservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.momentservice.data.createComment.CreateCommentRequest;
import com.orion.momentservice.data.createComment.CreateCommentResponse;
import com.orion.momentservice.data.createMoment.CreateMomentResponse;
import com.orion.momentservice.data.deleteComment.DeleteCommentRequest;
import com.orion.momentservice.model.MomentComment;

/**
* @author Zzw
* @description 针对表【moment_comment(朋友圈评论)】的数据库操作Service
* @createDate 2024-10-08 16:37:48
*/
@SuppressWarnings({"all"})
public interface MomentCommentService extends IService<MomentComment> {
    CreateCommentResponse createComment(CreateCommentRequest request);

    boolean deleteComment(DeleteCommentRequest request);

}
