package com.orion.offlinedatastoreservice.consumer;


import com.orion.offlinedatastoreservice.constants.kafka.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {
    @KafkaListener(topics = KafkaConstants.TOPIC, groupId = KafkaConstants.CONSUMER_GROUP_ID)
    public void listen(String message) {
        log.info("Received Message: " + message);
    }
}
