package org.ayu.doyouknowback.domain.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
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
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final FcmService fcmService;

    @Override
    @Transactional
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        // 1. DB에서 최근 5개 뉴스 조회
        List<News> latestNews = newsRepository.findTop5ByOrderByIdDesc();

        log.info("========DB에서 불러온 최근 5개의 학교소식========");
        for (News news : latestNews) {
            log.info("id : {}, title : {}", news.getId(), news.getNewsTitle());
        }

        log.info("========크롤링으로 불러온 최근 5개의 학교소식========");
        for (NewsRequestDTO news : newsRequestDTOList) {
            log.info("id : {}, title : {}", news.getId(), news.getNewsTitle());
        }

        // 2. 크롤링된 뉴스를 Entity로 변환
        List<News> crawledNews = News.fromList(newsRequestDTOList);

        // 3. 새로운 뉴스만 필터링
        List<News> newNewsList = News.filterNewNews(crawledNews, latestNews);

        int count = newNewsList.size();
        log.info("새로 등록될 뉴스 수 : {}", count);

        if (count == 0) {
            return;
        }

        // 4. 데이터 저장
        newsRepository.saveAll(newNewsList);

        // 5. 알림 전송
        sendNotification(newNewsList, count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDTO> getAll(int page, int size, String sort) {
        // Pageable 생성
        Pageable pageable = createPageable(page, size, sort);

        // Repository 조회
        Page<News> newsPage = newsRepository.findAll(pageable);

        // Entity -> DTO 변환
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
        // Pageable 생성
        Pageable pageable = createPageable(page, size, sort);

        // Repository 조회
        Page<News> newsPage = newsRepository.findByNewsTitleContainingOrNewsBodyContaining(keyword, keyword, pageable);

        // Entity -> DTO 변환
        List<NewsResponseDTO> dtoList = new ArrayList<>();
        for (News news : newsPage.getContent()) {
            dtoList.add(NewsResponseDTO.fromEntity(news));
        }

        return new PageImpl<>(dtoList, pageable, newsPage.getTotalElements());
    }

    private void sendNotification(List<News> newNewsList, int count) {
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

    // Pageable 객체 생성
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }

}
