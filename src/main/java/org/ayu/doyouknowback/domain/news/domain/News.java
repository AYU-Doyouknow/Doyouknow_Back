package org.ayu.doyouknowback.domain.news.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class News {
    @Id
    private Long id; // 글 번호

    private String newsTitle;// 타이틀
    private String newsWriter; // 작성자
    private String newsDate; // 게시일

    @Column(columnDefinition = "LONGTEXT")
    private String newsBody; // news 글 내용

    @Column(columnDefinition = "LONGTEXT")
    private String newsUrl; // 게시글 원본 링크

    // DTO로부터 Entity 생성
    public static News from(NewsRequestDTO dto) {
        return new News(
                dto.getId(),
                dto.getNewsTitle(),
                dto.getNewsWriter(),
                dto.getNewsDate(),
                dto.getNewsBody(),
                dto.getNewsUrl());
    }

    // DTO 리스트를 Entity 리스트로 변환
    public static List<News> fromList(List<NewsRequestDTO> dtoList) {
        List<News> newsList = new ArrayList<>();
        for (NewsRequestDTO dto : dtoList) {
            newsList.add(News.from(dto));
        }
        return newsList;
    }

    // 이 뉴스가 기존 뉴스 목록에 없는 새로운 뉴스인지 비교
    public boolean isNewComparedTo(List<News> existingNews) {
        for (News existing : existingNews) {
            if (existing.id.equals(this.id)) {
                return false;
            }
        }
        return true;
    }

    // 기존 뉴스 목록과 비교하여 새로운 뉴스만 필터링
    public static List<News> filterNewNews(List<News> candidates, List<News> existingNews) {
        List<News> newNewsList = new ArrayList<>();

        for (News candidate : candidates) {
            if (candidate.isNewComparedTo(existingNews)) {
                newNewsList.add(candidate);
            }
        }

        return newNewsList;
    }

    // 단일 알림 제목 생성
    public String createNotificationTitle() {
        return "[학교소식] " + this.newsTitle;
    }

    // 여러 뉴스 알림 메시지 생성
    public String createMultipleNewsNotificationBody(int totalCount) {
        return this.createNotificationTitle() + " 외 " + (totalCount - 1) + "개";
    }

    // 상세 페이지 URL 생성
    public String createDetailUrl() {
        return "https://doyouknowayu.netlify.app/news/detail/" + this.id;
    }

    // 뉴스 목록 페이지 URL
    public static String getNewsListUrl() {
        return "https://doyouknowayu.netlify.app/news";
    }

}