package org.ayu.doyouknowback.domain.notice.service.implement.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.domain.notice.service.NoticeMonitorHelper;
import org.ayu.doyouknowback.domain.notice.service.NoticeService;
import org.ayu.doyouknowback.global.cache.CacheConfig;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 캐시 사용 버전 - 성능 비교용
 * 인메모리 캐시에서 lastId를 조회하여 신규 여부 판단
 */
@Slf4j
@Service("noticeCacheService")
@RequiredArgsConstructor
public class NoticeCacheService implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMonitorHelper noticeHelper;
    private final CacheConfig cacheService;

    @Override
    @Transactional
    @Monitored("TOTAL_CACHE")
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {

        // 1. 캐시에서 마지막 저장된 ID 조회 (DB 조회 없이 O(1))
        Long lastSavedId = cacheService.getNoticeLastId();
        log.info("[CACHE] 캐시에서 조회한 lastNoticeId: {}", lastSavedId);

        // 2. 크롤링된 공지 중 신규만 필터링 (캐시 비교)
        List<Notice> newNoticesList = noticeRequestDTOList.stream()
                .filter(dto -> dto.getId() > lastSavedId)
                .map(Notice::from)
                .toList();

        int count = newNoticesList.size();
        log.info("[CACHE] 새로 등록될 공지사항 수 : {}", count);

        if (count == 0) {
            log.info("[CACHE] 신규 공지 없음 - DB 조회 스킵");
            return;
        }

        // 3. 신규 데이터만 저장
        noticeHelper.saveNotice(newNoticesList);

        // 4. 캐시 업데이트
        Long maxId = newNoticesList.stream()
                .mapToLong(Notice::getId)
                .max()
                .orElse(lastSavedId);
        cacheService.setNoticeLastId(maxId);
        log.info("[CACHE] 캐시 업데이트 완료 - 새로운 lastNoticeId: {}", maxId);

        // 5. 알림 전송
        noticeHelper.sendNotification(newNoticesList, count);
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

        // DB 검색은 Helper에서 수행(= AOP 측정 대상)
        Page<Notice> noticePage = noticeHelper.fullTextSearchByTitleOrBody(noticeSearchVal, pageable);

        // DTO 변환은 기존대로(측정 범위 밖)
        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice notice : noticePage.getContent()) {
            dtoList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(dtoList, pageable, noticePage.getTotalElements());
    }


    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return PageRequest.of(page, size, sorting);
    }
}
