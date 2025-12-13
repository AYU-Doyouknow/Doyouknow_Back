package org.ayu.doyouknowback.domain.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.global.util.DtoConversionUtils;
import org.ayu.doyouknowback.global.util.ItemFilterUtils;
import org.ayu.doyouknowback.global.util.NotificationMessageUtils;
import org.ayu.doyouknowback.global.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final FcmService fcmService;   // ✅ 포트 대신 FcmService 직접 주입

    /**
     * 크롤링된 공지 목록을 받아 DB에 없는 것만 저장하고, FCM 알림을 보냄
     */
    @Override
    @Transactional
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {

        // 1. 최근 5개의 공지사항 가져오기
        List<Notice> latestNotices = noticeRepository.findTop5ByOrderByIdDesc();

        log.debug("========DB에서 불러온 최근 5개의 공지사항========");
        for (Notice notice : latestNotices) {
            log.debug("id: {}, title: {}", notice.getId(), notice.getNoticeTitle());
        }

        log.debug("=======크롤링으로 불러온 최근 공지사항=======");
        for (NoticeRequestDTO notice : noticeRequestDTOList) {
            log.debug("id: {}, title: {}", notice.getId(), notice.getNoticeTitle());
        }

        // 2. DB에 있는 ID 목록 추출 (공통 유틸 사용)
        List<Long> dbIds = ItemFilterUtils.extractIdsFromNotice(latestNotices);

        // 3. 신규 공지사항만 필터링 (공통 유틸 사용)
        List<NoticeRequestDTO> newNotices =
                ItemFilterUtils.filterNewNoticeItems(noticeRequestDTOList, dbIds);

        int count = newNotices.size();
        log.info("새로 등록될 공지사항 수: {}", count);

        if (count == 0) {
            return;
        }

        // 4. DTO → 엔티티 변환 후 저장 목록 구성
        List<Notice> noticeListToSave = new ArrayList<>();
        for (NoticeRequestDTO dto : newNotices) {
            noticeListToSave.add(Notice.toSaveEntity(dto));
        }

        // 5. DB에 저장
        noticeRepository.saveAll(noticeListToSave);

        // 6. 알림 메시지 생성 + FCM 발송 (공통 유틸 + FcmService)
        String[] message = NotificationMessageUtils.buildNoticeNotificationMessage(newNotices);
        if (message != null) {
            fcmService.sendNotificationToAllExpoWithUrl(message[0], message[1], message[2]);
        }
    }

    /**
     * 전체 공지 목록 페이징 조회
     */
    @Override
    public Page<NoticeResponseDTO> findAll(int page, int size, String sort) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findAll(pageable);

        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }

    /**
     * 공지 상세 조회
     */
    @Override
    public NoticeDetailResponseDTO findById(Long id) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Not found NoticeDetail with id = " + id));

        return NoticeDetailResponseDTO.toDTO(notice);
    }

    /**
     * 카테고리 별 공지 목록 페이징 조회
     */
    @Override
    public Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findByNoticeCategory(category, pageable);

        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }

    /**
     * 제목/본문 검색 결과 페이징 조회
     */
    @Override
    public Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(noticeSearchVal, noticeSearchVal, pageable);

        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }
}
