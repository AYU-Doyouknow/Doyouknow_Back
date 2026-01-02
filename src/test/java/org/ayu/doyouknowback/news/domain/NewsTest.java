package org.ayu.doyouknowback.news.domain;

import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("News Entity 단위 테스트")
class NewsTest {

    private News existingNews1;
    private News existingNews2;
    private News newNews;
    private NewsRequestDTO newNewsDTO;

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

        newNews = News.builder()
                .id(3L)
                .newsTitle("새로운 학교소식")
                .newsWriter("홍보팀")
                .newsDate("2025-01-03")
                .newsBody("새 뉴스 내용")
                .newsUrl("https://example.com/3")
                .build();

        newNewsDTO = NewsRequestDTO.builder()
                .id(3L)
                .newsTitle("새로운 학교소식")
                .newsWriter("홍보팀")
                .newsDate("2025-01-03")
                .newsBody("새 뉴스 내용")
                .newsUrl("https://example.com/3")
                .build();
    }

    @Test
    @DisplayName("isNewComparedTo - 새로운 뉴스인 경우 true 반환")
    void isNewComparedTo_새로운_뉴스인_경우_true_반환() {
        // given
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);

        // when
        boolean result = newNews.isNewComparedTo(existingNews);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isNewComparedTo - 기존 뉴스인 경우 false 반환")
    void isNewComparedTo_기존_뉴스인_경우_false_반환() {
        // given
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);

        // when
        boolean result = existingNews1.isNewComparedTo(existingNews);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("filterNewNews - 새로운 뉴스만 필터링 성공")
    void filterNewNews_새로운_뉴스만_필터링_성공() {
        // given
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);
        List<News> candidates = Arrays.asList(existingNews1, newNews);

        // when
        List<News> result = News.filterNewNews(candidates, existingNews);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getNewsTitle()).isEqualTo("새로운 학교소식");
    }

    @Test
    @DisplayName("filterNewNews - 모두 기존 뉴스인 경우 빈 리스트 반환")
    void filterNewNews_모두_기존_뉴스인_경우_빈_리스트_반환() {
        // given
        List<News> existingNews = Arrays.asList(existingNews1, existingNews2);
        List<News> candidates = Arrays.asList(existingNews1, existingNews2);

        // when
        List<News> result = News.filterNewNews(candidates, existingNews);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createNotificationTitle - 알림 제목 생성 성공")
    void createNotificationTitle_알림_제목_생성_성공() {
        // when
        String result = newNews.createNotificationTitle();

        // then
        assertThat(result).isEqualTo("[학교소식] 새로운 학교소식");
    }

    @Test
    @DisplayName("createDetailUrl - 상세 페이지 URL 생성 성공")
    void createDetailUrl_상세_페이지_URL_생성_성공() {
        // when
        String result = newNews.createDetailUrl();

        // then
        assertThat(result).isEqualTo("https://doyouknowayu.netlify.app/news/detail/3");
    }

    @Test
    @DisplayName("createMultipleNewsNotificationBody - 여러 뉴스 알림 메시지 생성 성공")
    void createMultipleNewsNotificationBody_여러_뉴스_알림_메시지_생성_성공() {
        // given
        int totalCount = 5;

        // when
        String result = newNews.createMultipleNewsNotificationBody(totalCount);

        // then
        assertThat(result).isEqualTo("[학교소식] 새로운 학교소식 외 4개");
    }

    @Test
    @DisplayName("getNewsListUrl - 뉴스 목록 URL 반환 성공")
    void getNewsListUrl_뉴스_목록_URL_반환_성공() {
        // when
        String result = News.getNewsListUrl();

        // then
        assertThat(result).isEqualTo("https://doyouknowayu.netlify.app/news");
    }

    @Test
    @DisplayName("from - DTO로부터 Entity 생성 성공")
    void from_DTO로부터_Entity_생성_성공() {
        // when
        News result = News.from(newNewsDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getNewsTitle()).isEqualTo("새로운 학교소식");
        assertThat(result.getNewsWriter()).isEqualTo("홍보팀");
    }

    @Test
    @DisplayName("fromList - DTO 리스트를 Entity 리스트로 변환 성공")
    void fromList_DTO리스트를_Entity리스트로_변환_성공() {
        // given
        List<NewsRequestDTO> dtoList = Arrays.asList(newNewsDTO);

        // when
        List<News> result = News.fromList(dtoList);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getNewsTitle()).isEqualTo("새로운 학교소식");
    }
}
