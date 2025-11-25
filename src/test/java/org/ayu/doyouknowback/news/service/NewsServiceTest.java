package org.ayu.doyouknowback.news.service;

import org.ayu.doyouknowback.fcm.service.FcmService;
import org.ayu.doyouknowback.news.domain.News;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsService 단위 테스트")
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private FcmService fcmService;

    @InjectMocks
    private NewsService newsService;

    private News testNews;
    private NewsRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testNews = News.builder()
                .id(1L)
                .newsTitle("테스트 학교소식")
                .newsWriter("홍보팀")
                .newsDate("2025-01-15")
                .newsBody("테스트 뉴스 내용입니다.")
                .build();

        testRequestDTO = NewsRequestDTO.builder()
                .id(1L)
                .newsTitle("테스트 학교소식")
                .newsWriter("홍보팀")
                .newsDate("2025-01-15")
                .newsBody("테스트 뉴스 내용입니다.")
                .build();
    }

    @Test
    @DisplayName("학교소식 전체 조회 - 페이징")
    void getAll_ShouldReturnPagedNews() {
        List<News> newsList = Arrays.asList(testNews);
        Page<News> newsPage = new PageImpl<>(newsList);

        when(newsRepository.findAll(any(Pageable.class))).thenReturn(newsPage);

        Page<NewsResponseDTO> result = newsService.getAll(0, 10, "id,desc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(newsRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("학교소식 상세 조회 - 성공")
    void findById_ShouldReturnNewsDetail() {
        when(newsRepository.findById(1L)).thenReturn(Optional.of(testNews));

        NewsDetailResponseDTO result = newsService.findById(1L);

        assertThat(result).isNotNull();
        verify(newsRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("학교소식 상세 조회 - 존재하지 않는 ID는 null 반환")
    void findById_ShouldReturnNull_WhenNewsNotFound() {
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        NewsDetailResponseDTO result = newsService.findById(999L);

        assertThat(result).isNull();
        verify(newsRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("제목으로 학교소식 검색")
    void searchByTitle_ShouldReturnMatchingNews() {
        String keyword = "테스트";
        List<News> newsList = Arrays.asList(testNews);
        Page<News> newsPage = new PageImpl<>(newsList);

        when(newsRepository.findByNewsTitleContainingOrNewsBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class)))
                .thenReturn(newsPage);

        Page<NewsResponseDTO> result = newsService.searchByTitle(keyword, 0, 10, "id,desc");

        assertThat(result).isNotNull();
        verify(newsRepository, times(1))
                .findByNewsTitleContainingOrNewsBodyContaining(
                        eq(keyword), eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("최신 학교소식 상위 5개 조회")
    void getTop5LatestNews_ShouldReturnTop5() {
        List<News> top5News = Arrays.asList(testNews, testNews, testNews, testNews, testNews);

        when(newsRepository.findTop5ByOrderByIdDesc()).thenReturn(top5News);

        List<NewsResponseDTO> result = newsService.getTop5LatestNews();

        assertThat(result).hasSize(5);
        verify(newsRepository, times(1)).findTop5ByOrderByIdDesc();
    }

    @Test
    @DisplayName("학교소식 저장 - save 메서드")
    void save_ShouldSaveAllNews() {
        List<NewsRequestDTO> newsRequestDTOList = Arrays.asList(testRequestDTO);

        newsService.save(newsRequestDTOList);

        verify(newsRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("최신 학교소식 저장 - 새로운 뉴스 저장")
    void saveLatestNews_ShouldSaveAndSendNotification() {
        List<NewsRequestDTO> newNews = Arrays.asList(
                NewsRequestDTO.builder()
                        .id(2L)
                        .newsTitle("새 학교소식")
                        .newsWriter("홍보팀")
                        .newsDate("2025-01-16")
                        .newsBody("새 뉴스 내용")
                        .build());

        when(newsRepository.findTop5ByOrderByIdDesc())
                .thenReturn(Arrays.asList(testNews));

        newsService.saveLatestNews(newNews);

        verify(newsRepository, times(1)).saveAll(anyList());
        verify(fcmService, times(1))
                .sendNotificationToAllExpo(anyString(), anyString());
    }

    @Test
    @DisplayName("최신 학교소식 저장 - 중복된 뉴스는 저장 안 함")
    void saveLatestNews_ShouldNotSave_WhenAllNewsAreDuplicate() {
        List<NewsRequestDTO> duplicateNews = Arrays.asList(testRequestDTO);

        when(newsRepository.findTop5ByOrderByIdDesc())
                .thenReturn(Arrays.asList(testNews));

        newsService.saveLatestNews(duplicateNews);

        verify(newsRepository, never()).saveAll(anyList());
        verify(fcmService, never()).sendNotificationToAllExpo(anyString(), anyString());
    }
}
