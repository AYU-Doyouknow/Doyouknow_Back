package org.ayu.doyouknowback.domain.news.service.Implement;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.domain.news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("newsProduct")
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NotificationPushService notificationPushService;

    public NewsServiceImpl(
            NewsRepository newsRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService) {
        this.newsRepository = newsRepository;
        this.notificationPushService = notificationPushService;
    }

    @Override
    @Transactional
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        List<News> latestNews = newsRepository.findTop5ByOrderByIdDesc();

        List<News> crawledNews = News.fromList(newsRequestDTOList);

        List<News> newNewsList = News.filterNewNews(crawledNews, latestNews);

        int count = newNewsList.size();
        log.info("새로 등록될 뉴스 수 : {}", count);

        if (count == 0) {
            return;
        }

        newsRepository.saveAll(newNewsList);

        sendNotification(newNewsList, count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDTO> getAll(int page, int size, String sort) {

        Pageable pageable = createPageable(page, size, sort);

        Page<News> newsPage = newsRepository.findAll(pageable);

        List<NewsResponseDTO> dtoList = new ArrayList<>();
        for (News news : newsPage.getContent()) {
            dtoList.add(NewsResponseDTO.fromEntity(news));
        }

        return new PageImpl<>(dtoList, pageable, newsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDetailResponseDTO findById(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);

        if (optionalNews.isPresent()) {
            News news = optionalNews.get();
            return NewsDetailResponseDTO.fromEntity(news);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDTO> searchByTitle(String keyword, int page, int size, String sort) {

        Pageable pageable = createPageable(page, size, sort);

        Page<News> newsPage = newsRepository.findByNewsTitleContainingOrNewsBodyContaining(keyword, keyword, pageable);

        List<NewsResponseDTO> dtoList = new ArrayList<>();
        for (News news : newsPage.getContent()) {
            dtoList.add(NewsResponseDTO.fromEntity(news));
        }

        return new PageImpl<>(dtoList, pageable, newsPage.getTotalElements());
    }

    private void sendNotification(List<News> newNewsList, int count) {
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
    }

    // Pageable 객체 생성
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }

}
