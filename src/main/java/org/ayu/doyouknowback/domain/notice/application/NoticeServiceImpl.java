package org.ayu.doyouknowback.domain.notice.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.notice.application.port.NoticeNotificationPort;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.domain.notice.form.response.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.request.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.response.NoticeResponseDTO;
import org.ayu.doyouknowback.domain.notice.infrastructure.NoticeRepository;
import org.ayu.doyouknowback.global.util.DtoConversionUtils;
import org.ayu.doyouknowback.global.util.ItemFilterUtils;
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
    private final NoticeNotificationPort noticeNotificationPort; // ✅ FcmService 대신 포트 의존

    /**
     * 크롤링된 공지 목록을 받아 DB에 없는 것만 저장하고, 알림 포트를 통해 알림을 보냄
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

        // 2. DB에 있는 ID 목록 추출 (global util 사용)
        List<Long> dbIds = ItemFilterUtils.extractIdsFromNotice(latestNotices);

        // 3. 신규 공지사항만 필터링 (global util 사용)
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

        // 6. 알림 발송은 포트(추상화)에 위임
        noticeNotificationPort.notifyNewNotices(newNotices);
    }

    @Override
    public Page<NoticeResponseDTO> findAll(int page, int size, String sort) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }

    @Override
    public NoticeDetailResponseDTO findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Not found NoticeDetail with id = " + id));
        return NoticeDetailResponseDTO.toDTO(notice);
    }

    @Override
    public Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository.findByNoticeCategory(category, pageable);
        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }

    @Override
    public Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort) {
        Pageable pageable = PaginationUtils.createPageable(page, size, sort);
        Page<Notice> noticePage = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(noticeSearchVal, noticeSearchVal, pageable);
        return DtoConversionUtils.convertNoticePageToDto(noticePage, pageable);
    }
}
