package com.orion.realtimecommunicationservice.data.receiveMessage;


import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ReceiveMessageRequest {
//    @NotEmpty(message = "接受用户列表不能为空")
//    @Length(min = 1, message = "接受用户列表不能为空")
    private List<Long> receiveUserIds;

    private String sendUserId;

    private String sessionId;

    private String avatar;

    private String userName;

    private Integer type;

    private String messageId;

    private Integer sessionType;

    private String sessionName;

    private String sessionAvatar;

    private String createAt;

    private Object body;
}
