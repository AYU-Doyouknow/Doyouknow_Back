package org.ayu.doyouknowback.domain.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NotificationPushService notificationPushService;

    public NoticeServiceImpl(
            NoticeRepository noticeRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService) {
        this.noticeRepository = noticeRepository;
        this.notificationPushService = notificationPushService;
    }

    // 크롤링된 공지 목록을 받아 DB에 없는 것만 저장하고, FCM 알림을 보냄
    @Override
    @Transactional
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {

        // 1. DB에서 최근 5개 공지사항 조회
        List<Notice> latestNotices = noticeRepository.findTop5ByOrderByIdDesc();

        log.info("========DB에서 불러온 최근 5개의 공지사항========");
        for (Notice notice : latestNotices) {
            log.info("id : {}, title : {}", notice.getId(), notice.getNoticeTitle());
        }

        log.info("========크롤링으로 불러온 최근 5개의 공지사항========");
        for (NoticeRequestDTO notice : noticeRequestDTOList) {
            log.info("id : {}, title : {}", notice.getId(), notice.getNoticeTitle());
        }

        // 2. 크롤링된 공지를 Entity로 변환
        List<Notice> crawledNotices = Notice.fromList(noticeRequestDTOList);

        // 3. 새로운 공지만 필터링
        List<Notice> newNoticesList = Notice.filterNewNotices(crawledNotices, latestNotices);

        int count = newNoticesList.size();
        log.info("새로 등록될 공지사항 수 : {}", count);

        if (count == 0) {
            return;
        }

        // 4. 데이터 저장
        noticeRepository.saveAll(newNoticesList);

        // 5. 알림 전송
        sendNotification(newNoticesList, count);
    }

    // 전체 공지 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAll(int page, int size, String sort) {
        // Pageable 생성
        Pageable pageable = createPageable(page, size, sort);

        // Repository 조회
        Page<Notice> noticePage = noticeRepository.findAll(pageable);

        // Entity -> DTO 변환
        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    // 공지 상세 조회
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailResponseDTO findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found NoticeDetail with id = " + id));

        return NoticeDetailResponseDTO.toDTO(notice);
    }

    // 카테고리 별 공지 목록 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort) {
        // Pageable 생성
        Pageable pageable = createPageable(page, size, sort);

        // Repository 조회
        Page<Notice> noticePage = noticeRepository.findByNoticeCategory(category, pageable);

        // Entity -> DTO 변환
        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    // 제목/본문 검색 결과 페이징 조회
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort) {
        // Pageable 생성
        Pageable pageable = createPageable(page, size, sort);

        // Repository 조회
        Page<Notice> noticePage = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(noticeSearchVal, noticeSearchVal, pageable);

        // Entity -> DTO 변환
        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    // 알림 전송 - Entity의 도메인 로직 활용
    private void sendNotification(List<Notice> newNoticesList, int count) {
        if (count == 1) {
            // 단일 공지: 상세 페이지로 이동
            Notice singleNotice = newNoticesList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    singleNotice.createNotificationTitle(),
                    singleNotice.createDetailUrl());
        } else {
            // 여러 공지: 목록 페이지로 이동
            Notice latestNotice = newNoticesList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    latestNotice.createMultipleNoticesNotificationBody(count),
                    Notice.getNoticeListUrl());
        }
    }

    // Pageable 객체 생성
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
