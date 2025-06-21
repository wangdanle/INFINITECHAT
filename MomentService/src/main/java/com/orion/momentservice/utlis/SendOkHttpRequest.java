package com.orion.momentservice.utlis;

import com.alibaba.fastjson.JSON;
import com.orion.momentservice.constants.ConfigEnum;
import com.orion.momentservice.constants.ErrorEnum;
import com.orion.momentservice.exception.MessageSendFailureException;
import com.orion.momentservice.exception.ServiceException;
import com.orion.momentservice.model.vo.MomentRTCVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendOkHttpRequest {

    private final DiscoveryClient discoveryClient;

    public void sendNotification(MomentRTCVO notificationData, Long senderUserId, Integer notificationType, Long momentId) {
        List<ServiceInstance> serviceInstances = getServiceInstances();
        String requestBody = JSON.toJSONString(notificationData);

        sendRequestsToServices(serviceInstances, requestBody);
    }

    private void sendRequestsToServices(List<ServiceInstance> instances, String requesstBodyJson) {
        // 1.创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        // 2.创建HTTP客户端
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get(ConfigEnum.MEDIA_TYPE.getValue());
        RequestBody requestBody = RequestBody.create(mediaType, requesstBodyJson);
        // 3.向每个实例发送请求
        for (ServiceInstance instance : instances) {
            executorService.submit(() -> {
                sendRequestToInstance(instance, client, requestBody, requesstBodyJson);
            });
        }
        // 4.关闭线程池
        executorService.shutdown();
    }

    /**
     * 发送请求
     *
     * @param instance     实例
     * @param client       http客户端
     * @param requestBody  请求体
     * @param originalJson 原json
     */
    private void sendRequestToInstance(ServiceInstance instance, OkHttpClient client, RequestBody requestBody, String originalJson) {
        try {
            // 构建请求
            String url = instance.getUri().toString() + ConfigEnum.NOTICE_URL.getValue();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            // 执行请求
            client.newCall(request).execute();
            log.debug("成功向实例={}发送通知", instance.getUri());
        } catch (Exception e) {
            log.error("向实例={} 发送通知失败={}", instance.getUri(), e.getMessage());

            throw new MessageSendFailureException(
                    ErrorEnum.MESSAGE_SEND_FAILURE,
                    originalJson,
                    e);
        }
    }

    private List<ServiceInstance> getServiceInstances() {
        List<ServiceInstance> instances = discoveryClient.getInstances("RealTimeCommunicationService");
        if (instances.size() == 0) {
            throw new ServiceException("没有 netty 服务");
        }

        return instances;
    }
}