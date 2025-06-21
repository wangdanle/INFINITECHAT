package com.orion.momentservice.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NoticeMomentEnum {

    CREATE_MOMENT_NOTICE(1, "创建朋友圈通知"),
    CREATE_MOMENT_COMMENT_LIKE_NOTICE(2, "点赞评论朋友圈"),
    MOMENT_NOTICE(3, "朋友圈通知");

    private final String description;

    private final Integer value;

    NoticeMomentEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public static List<Integer> getValues() {
        return Arrays.stream(NoticeMomentEnum.values()).map(NoticeMomentEnum::getValue).collect(Collectors.toList());
    }


    public static NoticeMomentEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (NoticeMomentEnum anEnum : NoticeMomentEnum.values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }

        }
        return null;
    }

}
