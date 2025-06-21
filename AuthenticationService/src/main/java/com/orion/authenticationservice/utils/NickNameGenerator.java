package com.orion.authenticationservice.utils;

import java.util.Random;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:名称生成器
 */
public class NickNameGenerator {
    // 形容词库
    private static final String[] ADJECTIVES = {
            "可爱的", "聪明的", "勇敢的", "优雅的", "淘气的",
            "天选的", "神秘的", "傲娇的", "勤劳的", "智慧的",
            "暴躁的", "呆萌的", "机灵的", "高冷的", "幸运的",
            "辛苦的", "坚韧的", "顽强的", "幽默的", "沉稳的"
    };

    // 动物名库
    private static final String[] ANIMALS = {
            "猫咪", "柴犬", "考拉", "熊猫", "狐狸",
            "牛马", "企鹅", "仓鼠", "海豹", "树懒",
            "刺猬", "河马", "骆驼", "袋鼠", "雪豹",
            "孔雀", "浣熊", "水獭", "章鱼", "绵羊"
    };

    // 随机生成昵称
    public static String generate() {
        Random random = new Random();
        String adj = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String animal = ANIMALS[random.nextInt(ANIMALS.length)];
        return adj + animal;
    }
}