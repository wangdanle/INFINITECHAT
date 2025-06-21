package com.orion.momentservice.data.getMomentList;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class GetMomentListResponse {
    private List<Long> deleteLike;

    private List<Long> deleteComment;

    private List<Long> deleteMoment;

    private List<MomentLikeVO> createLike;

    private List<MomentCommentVO> createComment;

    private List<MomentVO> createMoment;
}
