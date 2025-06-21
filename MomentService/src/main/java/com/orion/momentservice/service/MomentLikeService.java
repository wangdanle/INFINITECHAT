package com.orion.momentservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.momentservice.data.deleteLikeMoment.DeleteLikeMomentRequest;
import com.orion.momentservice.data.likeMoment.LikeMomentRequest;
import com.orion.momentservice.data.likeMoment.LikeMomentResponse;
import com.orion.momentservice.model.MomentLike;

/**
* @author Zzw
* @description 针对表【moment_like(朋友圈点赞)】的数据库操作Service
* @createDate 2024-10-08 15:50:26
*/
@SuppressWarnings({"all"})
public interface MomentLikeService extends IService<MomentLike> {
    public LikeMomentResponse createLikeWithNotification(Long momentId, LikeMomentRequest request);

    public boolean deleteLike(DeleteLikeMomentRequest request);
}
