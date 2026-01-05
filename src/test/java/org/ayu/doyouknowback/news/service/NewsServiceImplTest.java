package org.ayu.doyouknowback.news.service;

import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.repository.NewsRepository;
import org.ayu.doyouknowback.domain.news.service.Implement.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsServiceImpl 단위 테스트")
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NotificationPushService fcmService;

    @InjectMocks
    private NewsServiceImpl newsService;

    private News existingNews1;
    private News existingNews2;
    private NewsRequestDTO newNewsDTO1;
    private NewsRequestDTO newNewsDTO2;
    private NewsRequestDTO newNewsDTO3;

    @BeforeEach
    void setUp() {
        existingNews1 = News.builder()
                .id(1L)
                .newsTitle("기존 학교소식 1")
                .newsWriter("홍보팀")
                .newsDate("2025-01-01")
                .newsBody("기존 뉴스 내용 1")
                .newsUrl("https://example.com/1")
                .build();

        existingNews2 = News.builder()
                .id(2L)
                .newsTitle("기존 학교소식 2")
                .newsWriter("홍보팀")
                .newsDate("2025-01-02")
                .newsBody("기존 뉴스 내용 2")
                .newsUrl("https://example.com/2")
                .build();

        newNewsDTO1 = NewsRequestDTO.builder()
                .id(3L)
                .newsTitle("새로운 학교소식 1")
                .newsWriter("홍보팀")
                .newsDate("2025-01-03")
                .newsBody("새 뉴스 내용 1")
                .newsUrl("https://example.com/3")
                .build();

        newNewsDTO2 = NewsRequestDTO.builder()
                .id(4L)
                .newsTitle("새로운 학교소식 2")
                .newsWriter("홍보팀")
                .newsDate("2025-01-04")
                .newsBody("새 뉴스 내용 2")
                .newsUrl("https://example.com/4")
                .build();

        newNewsDTO3 = NewsRequestDTO.builder()
                .id(5L)
                .newsTitle("새로운 학교소식 3")
                .newsWriter("홍보팀")
                .newsDate("2025-01-05")
                .newsBody("새 뉴스 내용 3")
                .newsUrl("https://example.com/5")
                .build();
    }

    @Test
    @DisplayName("saveLatestNews - 새로운 뉴스 1개 저장 및 단일 알림 전송 성공")
    void saveLatestNews_새로운_뉴스_1개_저장_및_단일_알림_전송_성공() {
        // given
        List<NewsRequestDTO> newNewsList = Arrays.asList(newNewsDTO1);
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);

        when(newsRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNews);

        // when
        newsService.saveLatestNews(newNewsList);

        // then
        // 1. DB 저장 검증
        ArgumentCaptor<List<News>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(newsRepository, times(1)).saveAll(saveCaptor.capture());
        List<News> savedNews = saveCaptor.getValue();
        assertThat(savedNews).hasSize(1);
        assertThat(savedNews.get(0).getId()).isEqualTo(3L);

        // 2. 단일 알림 전송 검증 (상세 페이지 URL)
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(fcmService, times(1)).sendNotificationAsync(
                titleCaptor.capture(),
                bodyCaptor.capture(),
                urlCaptor.capture());

        assertThat(titleCaptor.getValue()).isEqualTo("이거아냥?");
        assertThat(bodyCaptor.getValue()).isEqualTo("[학교소식] 새로운 학교소식 1");
        assertThat(urlCaptor.getValue()).isEqualTo("https://doyouknowayu.netlify.app/news/detail/3");
    }

    @Test
    @DisplayName("saveLatestNews - 새로운 뉴스 여러개 저장 및 복수 알림 전송 성공")
    void saveLatestNews_새로운_뉴스_여러개_저장_및_복수_알림_전송_성공() {
        // given
        List<NewsRequestDTO> newNewsList = Arrays.asList(newNewsDTO1, newNewsDTO2, newNewsDTO3);
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);

        when(newsRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNews);

        // when
        newsService.saveLatestNews(newNewsList);

        // then
        // 1. DB 저장 검증
        ArgumentCaptor<List<News>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(newsRepository, times(1)).saveAll(saveCaptor.capture());
        List<News> savedNews = saveCaptor.getValue();
        assertThat(savedNews).hasSize(3);

        // 2. 복수 알림 전송 검증 (목록 페이지 URL, "외 N개" 메시지)
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(fcmService, times(1)).sendNotificationAsync(
                titleCaptor.capture(),
                bodyCaptor.capture(),
                urlCaptor.capture());

        assertThat(titleCaptor.getValue()).isEqualTo("이거아냥?");
        assertThat(bodyCaptor.getValue()).contains("외 2개");
        assertThat(urlCaptor.getValue()).isEqualTo("https://doyouknowayu.netlify.app/news");
    }

    @Test
    @DisplayName("saveLatestNews - 새로운 뉴스 없는 경우 저장 및 알림 없음")
    void saveLatestNews_새로운_뉴스_없는_경우_저장_및_알림_없음() {
        // given
        NewsRequestDTO duplicateDTO = NewsRequestDTO.builder()
                .id(1L)
                .newsTitle("기존 학교소식 1")
                .newsWriter("홍보팀")
                .newsDate("2025-01-01")
                .newsBody("기존 뉴스 내용 1")
                .newsUrl("https://example.com/1")
                .build();

        List<NewsRequestDTO> duplicateNewsList = Arrays.asList(duplicateDTO);
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);

        when(newsRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNews);

        // when
        newsService.saveLatestNews(duplicateNewsList);

        // then
        // 1. DB 저장 호출 안 됨
        verify(newsRepository, never()).saveAll(anyList());

        // 2. 알림 전송 호출 안 됨
        verify(fcmService, never()).sendNotificationAsync(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("getAll - 페이징 조회 성공")
    void getAll_페이징_조회_성공() {
        // given
        List<News> newsList = Arrays.asList(existingNews1, existingNews2);
        Page<News> newsPage = new PageImpl<>(newsList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")), 2);

        when(newsRepository.findAll(any(Pageable.class))).thenReturn(newsPage);

        // when
        Page<NewsResponseDTO> result = newsService.getAll(0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(newsRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findById - 존재하는 뉴스 조회 성공")
    void findById_존재하는_뉴스_조회_성공() {
        // given
        when(newsRepository.findById(1L)).thenReturn(Optional.of(existingNews1));

        // when
        NewsDetailResponseDTO result = newsService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNewsTitle()).isEqualTo("기존 학교소식 1");
        verify(newsRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - 존재하지 않는 뉴스는 null 반환")
    void findById_존재하지_않는_뉴스는_null_반환() {
        // given
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        NewsDetailResponseDTO result = newsService.findById(999L);

        // then
        assertThat(result).isNull();
        verify(newsRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("searchByTitle - 제목으로 검색 성공")
    void searchByTitle_제목으로_검색_성공() {
        // given
        String keyword = "학교소식";
        List<News> newsList = Arrays.asList(existingNews1, existingNews2);
        Page<News> newsPage = new PageImpl<>(newsList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")), 2);

        when(newsRepository.findByNewsTitleContainingOrNewsBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class))).thenReturn(newsPage);

        // when
        Page<NewsResponseDTO> result = newsService.searchByTitle(keyword, 0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(newsRepository, times(1)).findByNewsTitleContainingOrNewsBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("searchByTitle - 검색 결과 없는 경우 빈 페이지 반환")
    void searchByTitle_검색_결과_없는_경우_빈_페이지_반환() {
        // given
        String keyword = "존재하지않는키워드";
        Page<News> emptyPage = new PageImpl<>(Collections.emptyList());

        when(newsRepository.findByNewsTitleContainingOrNewsBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class))).thenReturn(emptyPage);

        // when
        Page<NewsResponseDTO> result = newsService.searchByTitle(keyword, 0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(newsRepository, times(1)).findByNewsTitleContainingOrNewsBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class));
    }
}
