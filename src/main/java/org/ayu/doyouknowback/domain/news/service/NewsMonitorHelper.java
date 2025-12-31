package org.ayu.doyouknowback.domain.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
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
    private final FcmService fcmService;

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
            // 단일 뉴스: 상세 페이지로 이동
            News singleNews = newNewsList.get(0);
            fcmService.sendNotificationToAllExpoWithUrl(
                    "이거아냥?",
                    singleNews.createNotificationTitle(),
                    singleNews.createDetailUrl());
        } else {
            // 여러 뉴스: 목록 페이지로 이동
            News latestNews = newNewsList.get(0);
            fcmService.sendNotificationToAllExpoWithUrl(
                    "이거아냥?",
                    latestNews.createMultipleNewsNotificationBody(count),
                    News.getNewsListUrl());
        }
    }
}
