package org.ayu.doyouknowback.global.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.service.NewsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NewsKafkaConsumer {

    private final NewsService newsService;
    private final ObjectMapper objectMapper;

    public NewsKafkaConsumer(@Qualifier("newsCacheService") NewsService newsService, ObjectMapper objectMapper) {
        this.newsService = newsService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "news.crawled", groupId = "doyouknow-backend")
    public void consume(String message) {
        try {
            NewsRequestDTO dto = objectMapper.readValue(message, NewsRequestDTO.class);
            log.info("[Kafka] news 수신: id={}, title={}", dto.getId(), dto.getNewsTitle());
            newsService.saveLatestNews(List.of(dto));
        } catch (Exception e) {
            log.error("[Kafka] news 처리 실패: {}", e.getMessage(), e);
        }
    }
}
