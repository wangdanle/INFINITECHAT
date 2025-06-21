package com.orion.momentservice.data.getMomentList;


import lombok.Data;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.Acceleration;

import java.util.Date;
import java.util.List;

/**
* @Author: Orion
* @CreateTime: 2025/6/11
* @Description: 
*/
@Data
@Accessors(chain = true)
public class MomentVO {
    private Long momentId;

    private Long userId;

    private String userName;

    private String userAvatar;

    private String text;

    private List<String> mediaUrl;

    private Date createTime;

    private Date updateTime;

    private Date deleteTime;
}
