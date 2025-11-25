package org.ayu.doyouknowback.notice.service;

import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.ayu.doyouknowback.domain.notice.application.NoticeServiceImpl;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.infrastructure.NoticeRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeService 단위 테스트")
class NoticeServiceTest {

        @Mock
        private NoticeRepository noticeRepository;

        @Mock
        private FcmService fcmService;

        @InjectMocks
        private NoticeServiceImpl noticeService;

        private Notice testNotice;
        private NoticeRequestDTO testRequestDTO;

        @BeforeEach
        void setUp() {
                testNotice = Notice.builder()
                                .id(1L)
                                .noticeTitle("테스트 공지사항")
                                .noticeWriter("관리자")
                                .noticeDate("2025-01-15")
                                .noticeCategory("학사")
                                .noticeBody("테스트 내용입니다.")
                                .noticeDownloadLink("http://example.com/file")
                                .noticeDownloadTitle("첨부파일.pdf")
                                .build();

                testRequestDTO = NoticeRequestDTO.builder()
                                .id(1L)
                                .noticeTitle("테스트 공지사항")
                                .noticeWriter("관리자")
                                .noticeDate("2025-01-15")
                                .noticeCategory("학사")
                                .noticeBody("테스트 내용입니다.")
                                .noticeDownloadLink("http://example.com/file")
                                .noticeDownloadTitle("첨부파일.pdf")
                                .build();
        }

        @Test
        @DisplayName("공지사항 전체 조회 - 페이징")
        void findAll_ShouldReturnPagedNotices() {
                int page = 0;
                int size = 10;
                String sort = "id,desc";

                List<Notice> noticeList = Arrays.asList(testNotice);
                Page<Notice> noticePage = new PageImpl<>(noticeList);

                when(noticeRepository.findAll(any(Pageable.class))).thenReturn(noticePage);

                Page<NoticeResponseDTO> result = noticeService.findAll(page, size, sort);

                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                verify(noticeRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("공지사항 상세 조회 - 성공")
        void findById_ShouldReturnNoticeDetail() {
                Long noticeId = 1L;
                when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(testNotice));

                NoticeDetailResponseDTO result = noticeService.findById(noticeId);

                assertThat(result).isNotNull();
                assertThat(result.getNoticeTitle()).isEqualTo("테스트 공지사항");
                verify(noticeRepository, times(1)).findById(noticeId);
        }

        @Test
        @DisplayName("공지사항 상세 조회 - 존재하지 않는 ID로 예외 발생")
        void findById_ShouldThrowException_WhenNoticeNotFound() {
                Long noticeId = 999L;
                when(noticeRepository.findById(noticeId)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> noticeService.findById(noticeId))
                                .isInstanceOf(ResourceNotFoundException.class);

                verify(noticeRepository, times(1)).findById(noticeId);
        }

        @Test
        @DisplayName("카테고리별 공지사항 조회")
        void findAllByCategory_ShouldReturnFilteredNotices() {
                String category = "학사";
                List<Notice> noticeList = Arrays.asList(testNotice);
                Page<Notice> noticePage = new PageImpl<>(noticeList);

                when(noticeRepository.findByNoticeCategory(eq(category), any(Pageable.class)))
                                .thenReturn(noticePage);

                Page<NoticeResponseDTO> result = noticeService.findAllByCategory(category, 0, 10, "id,desc");

                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                verify(noticeRepository, times(1)).findByNoticeCategory(eq(category), any(Pageable.class));
        }

        @Test
        @DisplayName("검색어로 공지사항 조회")
        void findAllBySearch_ShouldReturnMatchingNotices() {
                String searchValue = "테스트";
                List<Notice> noticeList = Arrays.asList(testNotice);
                Page<Notice> noticePage = new PageImpl<>(noticeList);

                when(noticeRepository.findByNoticeTitleContainingOrNoticeBodyContaining(
                                eq(searchValue), eq(searchValue), any(Pageable.class)))
                                .thenReturn(noticePage);

                Page<NoticeResponseDTO> result = noticeService.findAllBySearch(searchValue, 0, 10, "id,desc");

                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                verify(noticeRepository, times(1))
                                .findByNoticeTitleContainingOrNoticeBodyContaining(
                                                eq(searchValue), eq(searchValue), any(Pageable.class));
        }

        @Test
        @DisplayName("최신 공지사항 저장 - 새로운 공지 1개")
        void saveLatestNotice_ShouldSaveAndSendNotification_WhenOneNewNotice() {
                List<NoticeRequestDTO> newNotices = Arrays.asList(
                                NoticeRequestDTO.builder()
                                                .id(2L)
                                                .noticeTitle("새 공지사항")
                                                .noticeWriter("관리자")
                                                .noticeDate("2025-01-16")
                                                .noticeCategory("학사")
                                                .noticeBody("새 내용")
                                                .build());

                when(noticeRepository.findTop5ByOrderByIdDesc())
                                .thenReturn(Arrays.asList(testNotice));

                noticeService.saveLatestNotice(newNotices);

                verify(noticeRepository, times(1)).saveAll(anyList());
                verify(fcmService, times(1))
                                .sendNotificationToAllExpo(anyString(), anyString());
        }

        @Test
        @DisplayName("최신 공지사항 저장 - 중복된 공지는 저장 안 함")
        void saveLatestNotice_ShouldNotSave_WhenAllNoticesAreDuplicate() {
                List<NoticeRequestDTO> duplicateNotices = Arrays.asList(testRequestDTO);

                when(noticeRepository.findTop5ByOrderByIdDesc())
                                .thenReturn(Arrays.asList(testNotice));

                noticeService.saveLatestNotice(duplicateNotices);

                verify(noticeRepository, never()).saveAll(anyList());
                verify(fcmService, never()).sendNotificationToAllExpo(anyString(), anyString());
        }
}
