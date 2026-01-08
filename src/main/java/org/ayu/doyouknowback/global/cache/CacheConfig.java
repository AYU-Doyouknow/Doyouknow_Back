package org.ayu.doyouknowback.global.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheConfig {

    private final NoticeRepository noticeRepository;
    private final NewsRepository newsRepository;

    private volatile Long lastNoticeId = 0L;
    private volatile Long lastNewsId = 0L;

    @PostConstruct
    public void init() {
        // 서버 시작 시 DB에서 마지막 ID 로드
        List<Notice> notices = noticeRepository.findTop5ByOrderByIdDesc();
        if (!notices.isEmpty()) {
            this.lastNoticeId = notices.get(0).getId();
        }

        List<News> newsList = newsRepository.findTop5ByOrderByIdDesc();
        if (!newsList.isEmpty()) {
            this.lastNewsId = newsList.get(0).getId();
        }

        log.info("캐시 초기화 완료 - lastNoticeId: {}, lastNewsId: {}", lastNoticeId, lastNewsId);
    }

    @Monitored("CACHE_READ")
    public Long getNoticeLastId() {
        return lastNoticeId;
    }

    public void setNoticeLastId(Long id) {
        this.lastNoticeId = id;
        log.debug("Notice lastId 업데이트: {}", id);
    }

    @Monitored("CACHE_READ")
    public Long getNewsLastId() {
        return lastNewsId;
    }

    public void setNewsLastId(Long id) {
        this.lastNewsId = id;
        log.debug("News lastId 업데이트: {}", id);
    }
}
