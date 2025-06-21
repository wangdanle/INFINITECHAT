package com.orion.authenticationservice.utils;


import cn.hutool.core.util.StrUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/23
 * @Description:
 */
@Service
public class OssUtils {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.url}")
    private String url;

    @SneakyThrows
    public String uploadUrl(String bucketName, String objectName, Integer expires) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName).
                        object(objectName)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build());
    }

    public String downUrl(String bucketName, String filename) {
        return url + StrUtil.SLASH + bucketName + StrUtil.SLASH +  filename;
    }
}
