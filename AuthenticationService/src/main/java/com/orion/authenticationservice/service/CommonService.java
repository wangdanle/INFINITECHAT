package com.orion.authenticationservice.service;

import com.orion.authenticationservice.data.common.SmsRequest;
import com.orion.authenticationservice.data.common.SmsResponse;
import com.orion.authenticationservice.data.user.upload.UploadUrlRequest;
import com.orion.authenticationservice.data.user.upload.UploadUrlResponse;

public interface CommonService {
    SmsResponse sendSms(SmsRequest smsRequest);

    UploadUrlResponse uploadUrl(UploadUrlRequest uploadUrlRequest);
}
