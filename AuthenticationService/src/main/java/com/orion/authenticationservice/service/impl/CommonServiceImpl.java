package com.orion.authenticationservice.service.impl;


import com.orion.authenticationservice.constant.config.OssConstant;
import com.orion.authenticationservice.constant.user.RegisterConstant;
import com.orion.authenticationservice.data.common.SmsRequest;
import com.orion.authenticationservice.data.common.SmsResponse;
import com.orion.authenticationservice.data.user.upload.UploadUrlRequest;
import com.orion.authenticationservice.data.user.upload.UploadUrlResponse;
import com.orion.authenticationservice.service.CommonService;
import com.orion.authenticationservice.utils.OssUtils;
import com.orion.authenticationservice.utils.RandomNumGenerator;
import com.orion.authenticationservice.utils.SendMailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SendMailUtils sendMailUtils;

    @Autowired
    private OssUtils ossUtils;

    @Value("${app.infinitechat.email.targetEmail}")
    private String targetEmail;

    @Override
    public SmsResponse sendSms(SmsRequest smsRequest) {
        String phone = smsRequest.getPhone();
        // 把验证码存到Redis里面
        if (stringRedisTemplate.hasKey(RegisterConstant.REGISTER_CODE + phone)) {
            return new SmsResponse().setPhone(phone);
        }

        String code = RandomNumGenerator.generateRandomNumber();
        stringRedisTemplate.opsForValue().set(RegisterConstant.REGISTER_CODE + phone, code, 1, TimeUnit.MINUTES);

        // 发送邮件验证码
        sendMailUtils.sendEmailCode(targetEmail, code);

        return new SmsResponse().setPhone(phone);
    }

    @Override
    public UploadUrlResponse uploadUrl(UploadUrlRequest uploadUrlRequest) {
        // 参数校验
        if (uploadUrlRequest == null) {
            throw new IllegalArgumentException("上传请求不能为空");
        }

        String fileName = uploadUrlRequest.getFileName();
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String uploadUrl = ossUtils.uploadUrl(OssConstant.BUCKET_NAME, fileName, OssConstant.PICTURE_EXPIRE_TIME);
        String downUrl = ossUtils.downUrl(OssConstant.BUCKET_NAME, fileName);

        UploadUrlResponse response = new UploadUrlResponse();
        response.setUploadUrl(uploadUrl)
                .setDownloadUrl(downUrl);

        return response;
    }
}
