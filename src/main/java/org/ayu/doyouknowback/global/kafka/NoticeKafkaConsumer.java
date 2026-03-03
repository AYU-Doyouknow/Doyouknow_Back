package org.ayu.doyouknowback.global.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeKafkaConsumer {

    @Qualifier("noticeCacheService")
    private final NoticeService noticeService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notice.crawled", groupId = "doyouknow-backend")
    public void consume(String message) {
        try {
            NoticeRequestDTO dto = objectMapper.readValue(message, NoticeRequestDTO.class);
            log.info("[Kafka] notice 수신: id={}, title={}", dto.getId(), dto.getNoticeTitle());
            noticeService.saveLatestNotice(List.of(dto));
        } catch (Exception e) {
            log.error("[Kafka] notice 처리 실패: {}", e.getMessage(), e);
        }
    }
}
