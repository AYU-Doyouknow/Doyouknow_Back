package org.ayu.doyouknowback.domain.notice.service.implement.product;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.domain.notice.service.NoticeService;
import org.ayu.doyouknowback.global.cache.CacheConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("noticeProduct")
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NotificationPushService notificationPushService;
    private final CacheConfig cacheService;

    public NoticeServiceImpl(
            NoticeRepository noticeRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService,
            CacheConfig cacheService) {
        this.noticeRepository = noticeRepository;
        this.notificationPushService = notificationPushService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {

        // 캐시에서 마지막 저장된 ID 조회
        Long lastSavedId = cacheService.getNoticeLastId();

        // 신규만 필터링
        List<Notice> newNoticesList = noticeRequestDTOList.stream()
                .filter(dto -> dto.getId() > lastSavedId)
                .map(Notice::from)
                .toList();

        int count = newNoticesList.size();

        if (count == 0) {
            log.info("[NO_CACHE] 신규 뉴스 없음");
            return;
        }

        log.info("새로 등록될 공지사항 수 : {}", count);

        noticeRepository.saveAll(newNoticesList);

        // 캐시 업데이트
        Long maxId = newNoticesList.stream()
                .mapToLong(Notice::getId)
                .max()
                .orElse(lastSavedId);
        cacheService.setNoticeLastId(maxId);

        sendNotification(newNoticesList, count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAll(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findAll(pageable);

        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDetailResponseDTO findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found NoticeDetail with id = " + id));

        return NoticeDetailResponseDTO.toDTO(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findByNoticeCategory(category, pageable);

        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(noticeSearchVal, noticeSearchVal, pageable);

        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }

    private void sendNotification(List<Notice> newNoticesList, int count) {
        if (count == 1) {
            Notice singleNotice = newNoticesList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    singleNotice.createNotificationTitle(),
                    singleNotice.createDetailUrl());
        } else {
            Notice latestNotice = newNoticesList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    latestNotice.createMultipleNoticesNotificationBody(count),
                    Notice.getNoticeListUrl());
        }
    }

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
