package com.orion.realtimecommunicationservice.utils;


import com.orion.realtimecommunicationservice.constant.ConfigEnum;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
public class JwtUtils {
    private static final long EXPIRATION_TIME = 3600 * 1000;

    public static String generate(String id){
        Date expiredDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        // 使用UTF-8编码的字节数组作为密钥
        byte[] keyBytes = ConfigEnum.TOKEN_SECRET_KEY.getValue()
                .getBytes(StandardCharsets.UTF_8);

        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();
    }

    /**
     * 解析并验证 Token
     * @param token JWT Token
     * @return 解析后的 Claims
     */
    public static Claims parseToken(String token) {
        if (!StringUtils.hasLength(token)) {
            throw new JwtException("Token 为空");
        }
        try {
            // 清理可能存在的Bearer前缀
            if (token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            // 统一编码转换
            byte[] keyBytes = ConfigEnum.TOKEN_SECRET_KEY.getValue()
                    .getBytes(StandardCharsets.UTF_8);


            Claims claims =  Jwts.parser().setSigningKey(keyBytes).parseClaimsJws(token).getBody();

            // 验证 Token 是否过期
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Token 已过期");
            }

            return claims;
        } catch (JwtException e) {
            throw new JwtException("Token 验证失败", e);
        }
    }
}
