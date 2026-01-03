package org.ayu.doyouknowback.domain.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsMonitorHelper {

    private final NewsRepository newsRepository;
    private final NotificationPushService notificationPushService;

    @Monitored("DB_READ")
    public List<News> findTop5News() {
        List<News> latestNews = newsRepository.findTop5ByOrderByIdDesc();

        log.info("========DB에서 불러온 최근 5개의 학교소식========");
        for (News news : latestNews) {
            log.info("id : {}, title : {}", news.getId(), news.getNewsTitle());
        }

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
