package org.ayu.doyouknowback.domain.news.service.Implement.product;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.domain.news.service.NewsService;
import org.ayu.doyouknowback.global.cache.CacheConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("newsProduct")
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NotificationPushService notificationPushService;
    private final CacheConfig cacheService;

    public NewsServiceImpl(
            NewsRepository newsRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService,
            CacheConfig cacheService) {
        this.newsRepository = newsRepository;
        this.notificationPushService = notificationPushService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        // 캐시에서 마지막 저장된 ID 조회
        Long lastSavedId = cacheService.getNewsLastId();

        // 신규만 필터링
        List<News> newNewsList = newsRequestDTOList.stream()
                .filter(dto -> dto.getId() > lastSavedId)
                .map(News::from)
                .toList();

        int count = newNewsList.size();

        if (count == 0) {
            log.info("[NO_CACHE] 신규 소식 없음");
            return;
        }

        log.info("새로 등록될 뉴스 수 : {}", count);

        newsRepository.saveAll(newNewsList);

        // 캐시 업데이트
        Long maxId = newNewsList.stream()
                .mapToLong(News::getId)
                .max()
                .orElse(lastSavedId);
        cacheService.setNewsLastId(maxId);

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

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
