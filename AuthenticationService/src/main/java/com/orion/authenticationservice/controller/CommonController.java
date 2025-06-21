package com.orion.authenticationservice.controller;


import com.orion.authenticationservice.common.Result;
import com.orion.authenticationservice.data.common.SmsRequest;
import com.orion.authenticationservice.data.common.SmsResponse;
import com.orion.authenticationservice.data.user.upload.UploadUrlRequest;
import com.orion.authenticationservice.data.user.upload.UploadUrlResponse;
import com.orion.authenticationservice.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user/common")
public class CommonController {
    @Autowired
    private CommonService commonService;

    @GetMapping("/sms")
    public Result<SmsResponse> sendSms(@Validated SmsRequest request) {
        SmsResponse smsResponse = commonService.sendSms(request);

        return Result.OK(smsResponse);
    }

    @GetMapping("/uploadUrl")
    public Result<UploadUrlResponse> getUploadUrl(@Validated UploadUrlRequest request) {
        UploadUrlResponse uploadUrlResponse = commonService.uploadUrl(request);

        return Result.OK(uploadUrlResponse);
    }
}
