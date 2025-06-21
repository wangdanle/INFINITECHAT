package com.orion.momentservice.model.vo;

import com.orion.momentservice.data.createComment.MomentCommentVO;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MomentsVO implements Serializable {

    private Long momentId;

    private Long userId;

    private String userName;

    private String avatar;

    private String text;

    private List<String> mediaUrls;

    private List<LikeVO> likes;

    private List<MomentCommentVO> comments;

    private String createTime;
}
