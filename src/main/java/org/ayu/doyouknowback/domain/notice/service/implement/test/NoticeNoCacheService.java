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
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 캐시 미사용 버전 - 성능 비교용
 * 매번 DB에서 최근 5개를 조회하여 신규 여부 판단
 */
@Slf4j
@Service("noticeNoCacheService")
@RequiredArgsConstructor
public class NoticeNoCacheService implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMonitorHelper noticeHelper;

    @Override
    @Transactional
    @Monitored("TOTAL_NO_CACHE")
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {

        // 1. DB에서 최근 5개 공지사항 조회 (AOP로 시간 측정)
        List<Notice> latestNotices = noticeHelper.findTop5Notice();

        // 2. 크롤링된 공지를 Entity로 변환
        List<Notice> crawledNotices = Notice.fromList(noticeRequestDTOList);

        // 3. 새로운 공지만 필터링
        List<Notice> newNoticesList = Notice.filterNewNotices(crawledNotices, latestNotices);

        int count = newNoticesList.size();
        log.info("[NO_CACHE] 새로 등록될 공지사항 수 : {}", count);

        if (count == 0) {
            log.info("[NO_CACHE] 신규 공지 없음");
            return;
        }

        // 4. 데이터 저장
        noticeHelper.saveNotice(newNoticesList);

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
        Page<Notice> noticePage = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(noticeSearchVal, noticeSearchVal, pageable);

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
