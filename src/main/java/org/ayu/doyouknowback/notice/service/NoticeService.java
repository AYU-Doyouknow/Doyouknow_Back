package org.ayu.doyouknowback.notice.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.fcm.service.FcmService;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.ayu.doyouknowback.notice.exception.ResourceNotFoundException;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.repository.NoticeRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FcmService fcmService;

//    @Transactional
//    public void save(List<NoticeRequestDTO> noticeRequestDTOList){
//
//        List<Notice> noticeList = new ArrayList<>();
//
//        for (NoticeRequestDTO noticeRequestDTO : noticeRequestDTOList) {
//            noticeList.add(Notice.toSaveEntity(noticeRequestDTO));
//        }
//
//        noticeRepository.saveAll(noticeList);
//    }

    @Transactional
    public void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList) {
        // 최근 5개의 공지사항 가져오기
        List<Notice> latestNotices = noticeRepository.findTop5ByOrderByIdDesc();

        System.out.println("========DB에서 불러온 최근 5개의 공지사항========");
        for (Notice notice : latestNotices) {
            System.out.println("id: " + notice.getId() + ", title: " + notice.getNoticeTitle());
        }

        System.out.println("=======크롤링으로 불러온 최근 5개의 공지사항=======");
        for(NoticeRequestDTO notice : noticeRequestDTOList){
            System.out.println("id: " + notice.getId() + ", title: " + notice.getNoticeTitle());
        }

        // DB에 있는 ID만 모으기
        List<Long> dbIds = new ArrayList<>();
        for (Notice notice : latestNotices) {
            dbIds.add(notice.getId());
        }

        // 새로운 공지사항 선별
        List<NoticeRequestDTO> newNotices = new ArrayList<>();
        for (NoticeRequestDTO dto : noticeRequestDTOList) {
            if (!dbIds.contains(dto.getId())) {
                newNotices.add(dto);
            }
        }

        int count = newNotices.size();
        System.out.println("새로 등록될 공지사항 수: " + count);

        if (count == 0) {
            return;
        }

        // 저장할 엔티티 변환
        List<Notice> noticeListToSave = new ArrayList<>();
        for (NoticeRequestDTO dto : newNotices) {
            noticeListToSave.add(Notice.toSaveEntity(dto));
        }

        // 알림 발송
        if (count == 1) {
            String title = newNotices.get(0).getNoticeTitle();
            fcmService.sendNotificationToAllUser("[공지사항]", title + " 공지사항이 등록되었습니다.");
        } else {
            NoticeRequestDTO latest = newNotices.get(0);
            for (NoticeRequestDTO dto : newNotices) {
                if (dto.getId() > latest.getId()) {
                    latest = dto;
                }
            }
            String title = latest.getNoticeTitle();
            fcmService.sendNotificationToAllUser("[공지사항]", title + " 외 " + (count - 1) + "개 공지사항이 등록되었습니다.");
        }

        // DB에 저장
        noticeRepository.saveAll(noticeListToSave);
    }

    @Transactional
    public Page<NoticeResponseDTO> findAll(int page, int size, String sort){ // 게시글 전체조회 로직 ( paging 기능 추가 )

        String[] sortParams = sort.split(","); // 매개변수 sort 배열으로 나누기
        // sortParams[1]=id, sortParams[2]=desc(내림차순: 11, 10, 9, 8, 7 .... [게시글 id 를 기준으로 한다.] )
        // 내림차순 정렬으로 해야 첫번째 페이지에 가장 최신의 게시글이 뜨게 된다.

        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<Notice> noticeEntityList = noticeRepository.findAll(pageable);

        List<NoticeResponseDTO> noticeResponseDTOList = new ArrayList<>();
        for (Notice notice : noticeEntityList) {
            noticeResponseDTOList.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(noticeResponseDTOList, pageable, noticeEntityList.getTotalElements());
        // new PageImpl<>(참조List, Pageable, page겍체.getTotalElements()); ==> Page 객체
    }

    public NoticeDetailResponseDTO findById(Long id){

        // SpringBoot 권장 API 에러 핸들러 방식
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Not found NoticeDetail with id = " + id));

        NoticeDetailResponseDTO noticeDetailResponseDTO = NoticeDetailResponseDTO.toDTO(notice);

        return noticeDetailResponseDTO;

    }

    public Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort){

        String[] sortParams = sort.split(","); // 매개변수 sort 배열으로 나누기
        // sortParams[1]=id, sortParams[2]=desc(내림차순: 11, 10, 9, 8, 7 .... [게시글 id 를 기준으로 한다.] )
        // 내림차순 정렬으로 해야 첫번째 페이지에 가장 최신의 게시글이 뜨게 된다.

        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<Notice> noticeList = noticeRepository.findByNoticeCategory(category, pageable);

        List<NoticeResponseDTO> responseDTOS = new ArrayList<>();

        for (Notice notice : noticeList) {
            responseDTOS.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(responseDTOS, pageable, noticeList.getTotalElements());

    }

    public Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort){

        String[] sortParams = sort.split(","); // 매개변수 sort 배열으로 나누기
        // sortParams[1]=id, sortParams[2]=desc(내림차순: 11, 10, 9, 8, 7 .... [게시글 id 를 기준으로 한다.] )
        // 내림차순 정렬으로 해야 첫번째 페이지에 가장 최신의 게시글이 뜨게 된다.

        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<Notice> noticeList = noticeRepository.findAllByKeyWord(noticeSearchVal, pageable);

        List<NoticeResponseDTO> responseDTOS = new ArrayList<>();

        for (Notice notice : noticeList) {
            responseDTOS.add(NoticeResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(responseDTOS, pageable, noticeList.getTotalElements());

    }

}
