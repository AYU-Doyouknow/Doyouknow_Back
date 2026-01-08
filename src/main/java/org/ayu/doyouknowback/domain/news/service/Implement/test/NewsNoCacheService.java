package org.ayu.doyouknowback.domain.news.service.Implement.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.domain.news.service.NewsMonitorHelper;
import org.ayu.doyouknowback.domain.news.service.NewsService;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 캐시 미사용 버전 - 성능 비교용
 * 매번 DB에서 최근 5개를 조회하여 신규 여부 판단
 */
@Slf4j
@Service("newsNoCacheService")
@RequiredArgsConstructor
public class NewsNoCacheService implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMonitorHelper newsHelper;

    @Override
    @Transactional
    @Monitored("TOTAL_NO_CACHE")
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        // 1. DB에서 최근 5개 뉴스 조회 (AOP로 시간 측정)
        List<News> latestNews = newsHelper.findTop5News();

        // 2. 크롤링된 뉴스를 Entity로 변환
        List<News> crawledNews = News.fromList(newsRequestDTOList);

        // 3. 새로운 뉴스만 필터링
        List<News> newNewsList = News.filterNewNews(crawledNews, latestNews);

        int count = newNewsList.size();
        log.info("[NO_CACHE] 새로 등록될 뉴스 수 : {}", count);

        if (count == 0) {
            log.info("[NO_CACHE] 신규 뉴스 없음");
            return;
        }

        // 4. 데이터 저장
        newsHelper.saveNews(newNewsList);

        // 5. 알림 전송
        newsHelper.sendNotification(newNewsList, count);
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

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
