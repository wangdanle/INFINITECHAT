package com.orion.contactservice.data.getApplyList;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/13
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ApplyFriendDTO {
    private String userUuid;

    private String nickname;

    private String avatar;

    private String msg;

    private Integer status;

    private LocalDateTime time;

    /**
     * 是否是接受者。1 是，0 否
     */
    private Integer isReceiver;
}
