package org.ayu.doyouknowback.notice.service;

import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.domain.notice.service.implement.product.NoticeServiceImpl;
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
@DisplayName("NoticeServiceImpl 단위 테스트")
class NoticeServiceImplTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NotificationPushService fcmService;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private Notice existingNotice1;
    private Notice existingNotice2;
    private NoticeRequestDTO newNoticeDTO1;
    private NoticeRequestDTO newNoticeDTO2;
    private NoticeRequestDTO newNoticeDTO3;

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

        newNoticeDTO1 = NoticeRequestDTO.builder()
                .id(3L)
                .noticeTitle("새로운 공지사항 1")
                .noticeWriter("관리자")
                .noticeDate("2025-01-03")
                .noticeCategory("학사")
                .noticeBody("새 공지 내용 1")
                .noticeUrl("https://example.com/3")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();

        newNoticeDTO2 = NoticeRequestDTO.builder()
                .id(4L)
                .noticeTitle("새로운 공지사항 2")
                .noticeWriter("관리자")
                .noticeDate("2025-01-04")
                .noticeCategory("일반")
                .noticeBody("새 공지 내용 2")
                .noticeUrl("https://example.com/4")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();

        newNoticeDTO3 = NoticeRequestDTO.builder()
                .id(5L)
                .noticeTitle("새로운 공지사항 3")
                .noticeWriter("관리자")
                .noticeDate("2025-01-05")
                .noticeCategory("학사")
                .noticeBody("새 공지 내용 3")
                .noticeUrl("https://example.com/5")
                .noticeDownloadLink(null)
                .noticeDownloadTitle(null)
                .build();
    }

    @Test
    @DisplayName("saveLatestNotice - 새로운 공지 1개 저장 및 단일 알림 전송 성공")
    void saveLatestNotice_새로운_공지_1개_저장_및_단일_알림_전송_성공() {
        // given
        List<NoticeRequestDTO> newNoticesList = Arrays.asList(newNoticeDTO1);
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);

        when(noticeRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNotices);

        // when
        noticeService.saveLatestNotice(newNoticesList);

        // then
        // 1. DB 저장 검증
        ArgumentCaptor<List<Notice>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(noticeRepository, times(1)).saveAll(saveCaptor.capture());
        List<Notice> savedNotices = saveCaptor.getValue();
        assertThat(savedNotices).hasSize(1);
        assertThat(savedNotices.get(0).getId()).isEqualTo(3L);

        // 2. 단일 알림 전송 검증 (상세 페이지 URL)
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(fcmService, times(1)).sendNotificationAsync(
                titleCaptor.capture(),
                bodyCaptor.capture(),
                urlCaptor.capture());

        assertThat(titleCaptor.getValue()).isEqualTo("이거아냥?");
        assertThat(bodyCaptor.getValue()).isEqualTo("[공지사항] 새로운 공지사항 1");
        assertThat(urlCaptor.getValue()).isEqualTo("https://doyouknowayu.netlify.app/notice/detail/3");
    }

    @Test
    @DisplayName("saveLatestNotice - 새로운 공지 여러개 저장 및 복수 알림 전송 성공")
    void saveLatestNotice_새로운_공지_여러개_저장_및_복수_알림_전송_성공() {
        // given
        List<NoticeRequestDTO> newNoticesList = Arrays.asList(newNoticeDTO1, newNoticeDTO2, newNoticeDTO3);
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);

        when(noticeRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNotices);

        // when
        noticeService.saveLatestNotice(newNoticesList);

        // then
        // 1. DB 저장 검증
        ArgumentCaptor<List<Notice>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(noticeRepository, times(1)).saveAll(saveCaptor.capture());
        List<Notice> savedNotices = saveCaptor.getValue();
        assertThat(savedNotices).hasSize(3);

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
        assertThat(urlCaptor.getValue()).isEqualTo("https://doyouknowayu.netlify.app/notice");
    }

    @Test
    @DisplayName("saveLatestNotice - 새로운 공지 없는 경우 저장 및 알림 없음")
    void saveLatestNotice_새로운_공지_없는_경우_저장_및_알림_없음() {
        // given
        NoticeRequestDTO duplicateDTO = NoticeRequestDTO.builder()
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

        List<NoticeRequestDTO> duplicateNoticesList = Arrays.asList(duplicateDTO);
        List<Notice> existingNotices = Arrays.asList(existingNotice1, existingNotice2);

        when(noticeRepository.findTop5ByOrderByIdDesc()).thenReturn(existingNotices);

        // when
        noticeService.saveLatestNotice(duplicateNoticesList);

        // then
        // 1. DB 저장 호출 안 됨
        verify(noticeRepository, never()).saveAll(anyList());

        // 2. 알림 전송 호출 안 됨
        verify(fcmService, never()).sendNotificationAsync(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("findAll - 페이징 조회 성공")
    void findAll_페이징_조회_성공() {
        // given
        List<Notice> noticeList = Arrays.asList(existingNotice1, existingNotice2);
        Page<Notice> noticePage = new PageImpl<>(noticeList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")),
                2);

        when(noticeRepository.findAll(any(Pageable.class))).thenReturn(noticePage);

        // when
        Page<NoticeResponseDTO> result = noticeService.findAll(0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(noticeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findById - 존재하는 공지 조회 성공")
    void findById_존재하는_공지_조회_성공() {
        // given
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(existingNotice1));

        // when
        NoticeDetailResponseDTO result = noticeService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNoticeTitle()).isEqualTo("기존 공지사항 1");
        verify(noticeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findAllByCategory - 카테고리별 검색 성공")
    void findAllByCategory_카테고리별_검색_성공() {
        // given
        String category = "학사";
        List<Notice> noticeList = Arrays.asList(existingNotice1);
        Page<Notice> noticePage = new PageImpl<>(noticeList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")),
                1);

        when(noticeRepository.findByNoticeCategory(eq(category), any(Pageable.class))).thenReturn(noticePage);

        // when
        Page<NoticeResponseDTO> result = noticeService.findAllByCategory(category, 0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(noticeRepository, times(1)).findByNoticeCategory(eq(category), any(Pageable.class));
    }

    @Test
    @DisplayName("findAllBySearch - 제목으로 검색 성공")
    void findAllBySearch_제목으로_검색_성공() {
        // given
        String keyword = "공지사항";
        List<Notice> noticeList = Arrays.asList(existingNotice1, existingNotice2);
        Page<Notice> noticePage = new PageImpl<>(noticeList, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")),
                2);

        when(noticeRepository.findByNoticeTitleContainingOrNoticeBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class))).thenReturn(noticePage);

        // when
        Page<NoticeResponseDTO> result = noticeService.findAllBySearch(keyword, 0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(noticeRepository, times(1)).findByNoticeTitleContainingOrNoticeBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("findAllBySearch - 검색 결과 없는 경우 빈 페이지 반환")
    void findAllBySearch_검색_결과_없는_경우_빈_페이지_반환() {
        // given
        String keyword = "존재하지않는키워드";
        Page<Notice> emptyPage = new PageImpl<>(Collections.emptyList());

        when(noticeRepository.findByNoticeTitleContainingOrNoticeBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class))).thenReturn(emptyPage);

        // when
        Page<NoticeResponseDTO> result = noticeService.findAllBySearch(keyword, 0, 10, "id,desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(noticeRepository, times(1)).findByNoticeTitleContainingOrNoticeBodyContaining(
                eq(keyword), eq(keyword), any(Pageable.class));
    }
}
