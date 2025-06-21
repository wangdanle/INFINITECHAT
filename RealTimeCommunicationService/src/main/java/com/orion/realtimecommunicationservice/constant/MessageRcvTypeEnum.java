package com.orion.realtimecommunicationservice.constant;


/**
 * @Author: Orion
 * @CreateTime: 2025/6/1
 * @Description:
 */
public enum MessageRcvTypeEnum {
    TEXT_MESSAGE(1),

    PICTURE_MESSAGE(2),

    FILE_MESSAGE(3),

    VIDEO_MESSAGE(4),

    RED_PACKET_MESSAGE(5),

    EMOTICNN_MESSAGE(6);

    private final Integer code;

    MessageRcvTypeEnum(Integer code) {
        this.code = code;
    }

    public static MessageRcvTypeEnum fromCode(Integer code) {
        for (MessageRcvTypeEnum type : MessageRcvTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid Argument" + code);
    }

    public Integer getCode() {
        return this.code;
    }
}
