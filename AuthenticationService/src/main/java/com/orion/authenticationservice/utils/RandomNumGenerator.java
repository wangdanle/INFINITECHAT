package com.orion.authenticationservice.utils;


import java.util.Random;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class RandomNumGenerator {

    /**
     * 生成一个长度为六位的数字
     * @return
     */
    public static String generateRandomNumber() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.format("%06d", code);
    }
}
