package org.ayu.doyouknowback.domain.news.service;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NewsMonitorHelper {

    private final NewsRepository newsRepository;
    private final NotificationPushService notificationPushService;

    public NewsMonitorHelper(
            NewsRepository newsRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService) {
        this.newsRepository = newsRepository;
        this.notificationPushService = notificationPushService;
    }

    @Monitored("DB_READ")
    public List<News> findTop5News() {
        List<News> latestNews = newsRepository.findTop5ByOrderByIdDesc();
        log.info("[DB_READ] DB에서 최근 5개 뉴스 조회 - {}개", latestNews.size());
        return latestNews;
    }

    @Monitored("DB_WRITE")
    public void saveNews(List<News> newNewsList) {
        newsRepository.saveAll(newNewsList);
        log.info("뉴스 {}개 저장 완료", newNewsList.size());
    }

    @Monitored("PUSH_NOTIFICATION")
    public void sendNotification(List<News> newNewsList, int count) {
        if (count == 1) {
            News singleNews = newNewsList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    singleNews.createNotificationTitle(),
                    singleNews.createDetailUrl());
        } else {
            News latestNews = newNewsList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    latestNews.createMultipleNewsNotificationBody(count),
                    News.getNewsListUrl());
        }

        log.info("푸시 알림 비동기 큐잉 완료 (백그라운드 실행 중)");
    }
}

