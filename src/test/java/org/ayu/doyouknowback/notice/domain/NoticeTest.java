package org.ayu.doyouknowback.notice.domain;

import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Notice Entity 단위 테스트")
class NoticeTest {

    private Notice existingNotice1;
    private Notice existingNotice2;
    private Notice newNotice;
    private NoticeRequestDTO newNoticeDTO;

    @BeforeEach
    void setUp() {
        existingNotice1 = Notice.builder()
                .id(1L)
                .noticeTitle("기존 공지사항 1")
                .noticeWriter("관리자")
                .noticeDate("2025-01-01")
                .noticeCategory("학사")
                .noticeBody("기존 공지 내용 1")
                .noticeUrl("https://example.com/1")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();

        existingNotice2 = Notice.builder()
                .id(2L)
                .noticeTitle("기존 공지사항 2")
                .noticeWriter("관리자")
                .noticeDate("2025-01-02")
                .noticeCategory("일반")
                .noticeBody("기존 공지 내용 2")
                .noticeUrl("https://example.com/2")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();

        newNotice = Notice.builder()
                .id(3L)
                .noticeTitle("새로운 공지사항")
                .noticeWriter("관리자")
                .noticeDate("2025-01-03")
                .noticeCategory("학사")
                .noticeBody("새 공지 내용")
                .noticeUrl("https://example.com/3")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();

        newNoticeDTO = NoticeRequestDTO.builder()
                .id(3L)
                .noticeTitle("새로운 공지사항")
                .noticeWriter("관리자")
                .noticeDate("2025-01-03")
                .noticeCategory("학사")
                .noticeBody("새 공지 내용")
                .noticeUrl("https://example.com/3")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();
    }

    @Test
    @DisplayName("isNewComparedTo - 새로운 공지인 경우 true 반환")
    void isNewComparedTo_새로운_공지인_경우_true_반환() {
        // given
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);

        // when
        boolean result = newNotice.isNewComparedTo(existingNotices);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isNewComparedTo - 기존 공지인 경우 false 반환")
    void isNewComparedTo_기존_공지인_경우_false_반환() {
        // given
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);

        // when
        boolean result = existingNotice1.isNewComparedTo(existingNotices);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("filterNewNotices - 새로운 공지만 필터링 성공")
    void filterNewNotices_새로운_공지만_필터링_성공() {
        // given
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);
        List<Notice> candidates = Arrays.asList(existingNotice1, newNotice);

        // when
        List<Notice> result = Notice.filterNewNotices(candidates, existingNotices);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getNoticeTitle()).isEqualTo("새로운 공지사항");
    }

    @Test
    @DisplayName("filterNewNotices - 모두 기존 공지인 경우 빈 리스트 반환")
    void filterNewNotices_모두_기존_공지인_경우_빈_리스트_반환() {
        // given
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);
        List<Notice> candidates = Arrays.asList(existingNotice1, existingNotice2);

        // when
        List<Notice> result = Notice.filterNewNotices(candidates, existingNotices);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createNotificationTitle - 알림 제목 생성 성공")
    void createNotificationTitle_알림_제목_생성_성공() {
        // when
        String result = newNotice.createNotificationTitle();

        // then
        assertThat(result).isEqualTo("[공지사항] 새로운 공지사항");
    }

    @Test
    @DisplayName("createDetailUrl - 상세 페이지 URL 생성 성공")
    void createDetailUrl_상세_페이지_URL_생성_성공() {
        // when
        String result = newNotice.createDetailUrl();

        // then
        assertThat(result).isEqualTo("https://doyouknowayu.netlify.app/notice/detail/3");
    }

    @Test
    @DisplayName("createMultipleNoticesNotificationBody - 여러 공지 알림 메시지 생성 성공")
    void createMultipleNoticesNotificationBody_여러_공지_알림_메시지_생성_성공() {
        // given
        int totalCount = 5;

        // when
        String result = newNotice.createMultipleNoticesNotificationBody(totalCount);

        // then
        assertThat(result).isEqualTo("[공지사항] 새로운 공지사항 외 4개");
    }

    @Test
    @DisplayName("getNoticeListUrl - 공지사항 목록 URL 반환 성공")
    void getNoticeListUrl_공지사항_목록_URL_반환_성공() {
        // when
        String result = Notice.getNoticeListUrl();

        // then
        assertThat(result).isEqualTo("https://doyouknowayu.netlify.app/notice");
    }

    @Test
    @DisplayName("from - DTO로부터 Entity 생성 성공")
    void from_DTO로부터_Entity_생성_성공() {
        // when
        Notice result = Notice.from(newNoticeDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getNoticeTitle()).isEqualTo("새로운 공지사항");
        assertThat(result.getNoticeWriter()).isEqualTo("관리자");
    }

    @Test
    @DisplayName("fromList - DTO 리스트를 Entity 리스트로 변환 성공")
    void fromList_DTO리스트를_Entity리스트로_변환_성공() {
        // given
        List<NoticeRequestDTO> dtoList = Arrays.asList(newNoticeDTO);

        // when
        List<Notice> result = Notice.fromList(dtoList);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getNoticeTitle()).isEqualTo("새로운 공지사항");
    }
}
