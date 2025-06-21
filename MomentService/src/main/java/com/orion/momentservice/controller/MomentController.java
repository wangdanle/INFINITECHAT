package com.orion.momentservice.controller;

import com.orion.momentservice.common.Result;
import com.orion.momentservice.data.createComment.CreateCommentRequest;
import com.orion.momentservice.data.createComment.CreateCommentResponse;
import com.orion.momentservice.data.createComment.MomentCommentDTO;
import com.orion.momentservice.data.createMoment.CreateMomentRequest;
import com.orion.momentservice.data.createMoment.CreateMomentResponse;
import com.orion.momentservice.data.deleteComment.DeleteCommentRequest;
import com.orion.momentservice.data.deleteComment.DeleteCommentResponse;
import com.orion.momentservice.data.deleteLikeMoment.DeleteLikeMomentRequest;
import com.orion.momentservice.data.deleteLikeMoment.DeleteLikeMomentResponse;
import com.orion.momentservice.data.deleteMoment.DeleteMomentRequest;
import com.orion.momentservice.data.deleteMoment.DeleteMomentResponse;
import com.orion.momentservice.data.getMomentList.GetMomentListRequest;
import com.orion.momentservice.data.getMomentList.GetMomentListResponse;
import com.orion.momentservice.data.likeMoment.LikeMomentRequest;
import com.orion.momentservice.data.likeMoment.LikeMomentResponse;
import com.orion.momentservice.service.MomentCommentService;
import com.orion.momentservice.service.MomentLikeService;
import com.orion.momentservice.service.MomentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Slf4j
@RestController
@RequestMapping("/api/v1/moment")
public class MomentController {
    @Autowired
    private MomentService momentService;

    @Autowired
    private MomentLikeService momentLikeService;

    @Autowired
    private MomentCommentService momentCommentService;


    // 创建朋友圈
    @PostMapping("")
    public Result<CreateMomentResponse> createMoment(@Validated @RequestBody CreateMomentRequest request) throws Exception {
        CreateMomentResponse response = momentService.createMoment(request);
        return Result.OK(response);
    }

    // 删除朋友圈
    @DeleteMapping("/{momentId}")
    public Result<DeleteMomentResponse> deleteMoment(@Validated @ModelAttribute DeleteMomentRequest request) throws Exception {
        DeleteMomentResponse response = momentService.deleteMoment(request);

        return Result.OK(response);
    }

    // 获取朋友朋友圈列表
    @GetMapping("/list/{userId}")
    public Result<GetMomentListResponse> getMomentList(@Validated @ModelAttribute GetMomentListRequest request) throws Exception {
        GetMomentListResponse response = momentService.getMomentList(request);

        return Result.OK(response);
    }


    // 点赞
    @GetMapping("/like/{momentId}")
    public Result<LikeMomentResponse> likeMoment(@PathVariable Long momentId, @Validated @RequestBody LikeMomentRequest request) throws Exception {
        LikeMomentResponse response = momentLikeService.createLikeWithNotification(momentId, request);

        return Result.OK(response);
    }

    // 取消点赞
    @DeleteMapping("/like/{momentId}")
    public Result<DeleteLikeMomentResponse> deleteLikeMoment(@Validated @ModelAttribute DeleteLikeMomentRequest request) throws Exception {
        boolean result = momentLikeService.deleteLike(request);

        DeleteLikeMomentResponse response = new DeleteLikeMomentResponse();
        String message = result ? "取消点赞成功！" : "取消点赞失败！";
        response.setMessage(message);
        return Result.OK(response);
    }

    // 评论
    @PostMapping("/comment/{momentId}")
    public Result<CreateCommentResponse> createMoment(
            @NotNull(message = "朋友圈ID不能为空") @PathVariable("momentid") Long momentId,
            @Valid @RequestBody MomentCommentDTO momentCommentDTO) {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setMomentId(momentId);
        createCommentRequest.setMomentCommentDTO(momentCommentDTO);

        CreateCommentResponse response = momentCommentService.createComment(createCommentRequest);

        return Result.OK(response);
    }

    // 删除评论
    @DeleteMapping("/comment/{momentId}")
    public Result<DeleteCommentResponse> deleteComment(@Validated @ModelAttribute DeleteCommentRequest request) throws Exception {
        boolean result = momentCommentService.deleteComment(request);

        DeleteCommentResponse response = new DeleteCommentResponse();
        String message = result ? "删除失败, 评论不存在！" : "评论删除成功！！";
        response.setMessage(message);
        return Result.OK(response);
    }
}
