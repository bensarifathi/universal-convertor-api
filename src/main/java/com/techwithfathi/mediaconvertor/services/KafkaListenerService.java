package com.techwithfathi.mediaconvertor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListenerService {
    private final AudioFileConvertor audioFileConvertor;

    public KafkaListenerService(AudioFileConvertor audioFileConvertor) {
        this.audioFileConvertor = audioFileConvertor;
    }

    @KafkaListener(topics = "t.media.conversion", groupId = "task-group")
    void listener(String taskId) {
        log.info("new conversion task: " + taskId);
        audioFileConvertor.convert(taskId);
    }
}
