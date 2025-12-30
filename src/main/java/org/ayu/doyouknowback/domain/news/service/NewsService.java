package org.ayu.doyouknowback.domain.news.service;

import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewsService {

    /**
     * 최신 학교소식 저장 및 알림 전송
     */
    void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList);

    /**
     * 학교소식 전체 조회 (페이징)
     */
    Page<NewsResponseDTO> getAll(int page, int size, String sort);

    /**
     * 학교소식 세부 조회
     */
    NewsDetailResponseDTO findById(Long id);

    /**
     * 뉴스 제목 또는 본문 검색
     */
    Page<NewsResponseDTO> searchByTitle(String keyword, int page, int size, String sort);

    /**
     * 최근 5개 뉴스 조회
     */
    List<NewsResponseDTO> getTop5LatestNews();
}
