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
import org.ayu.doyouknowback.global.cache.CacheConfig;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 캐시 사용 버전 - 성능 비교용
 * 인메모리 캐시에서 lastId를 조회하여 신규 여부 판단
 */
@Slf4j
@Service("newsCacheService")
@RequiredArgsConstructor
public class NewsCacheService implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMonitorHelper newsHelper;
    private final CacheConfig cacheService;

    @Transactional
    @Monitored("TOTAL_CACHE")
    @Override
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        // 1. 캐시에서 마지막 저장된 ID 조회 (DB 조회 없이 O(1))
        Long lastSavedId = cacheService.getNewsLastId();
        log.info("[CACHE] 캐시에서 조회한 lastNewsId: {}", lastSavedId);

        // 2. 크롤링된 뉴스 중 신규만 필터링 (캐시 비교)
        List<News> newNewsList = newsRequestDTOList.stream()
                .filter(dto -> dto.getId() > lastSavedId)
                .map(News::from)
                .toList();

        int count = newNewsList.size();
        log.info("[CACHE] 새로 등록될 뉴스 수 : {}", count);

        if (count == 0) {
            log.info("[CACHE] 신규 뉴스 없음 - DB 조회 스킵");
            return;
        }

        // 3. 신규 데이터만 저장
        newsHelper.saveNews(newNewsList);

        // 4. 캐시 업데이트
        Long maxId = newNewsList.stream()
                .mapToLong(News::getId)
                .max()
                .orElse(lastSavedId);
        cacheService.setNewsLastId(maxId);
        log.info("[CACHE] 캐시 업데이트 완료 - 새로운 lastNewsId: {}", maxId);

        // 5. 알림 전송
        newsHelper.sendNotification(newNewsList, count);
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

    // Pageable 객체 생성
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
