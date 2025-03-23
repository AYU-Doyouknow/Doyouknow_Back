package org.ayu.doyouknowback.notice.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.ayu.doyouknowback.notice.form.NoticeCategoryResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.repository.NoticeRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void save(List<NoticeRequestDTO> noticeRequestDTOList){

        List<Notice> noticeList = new ArrayList<>();

        for (NoticeRequestDTO noticeRequestDTO : noticeRequestDTOList) {
            noticeList.add(Notice.toSaveEntity(noticeRequestDTO));
        }

        noticeRepository.saveAll(noticeList);
    }

//    @Transactional(readOnly = true)
//    public List<NoticeResponseDTO> findAll(){
//
//        List<Notice> noticeEntityList = noticeRepository.findAll();
//
//        List<NoticeResponseDTO> noticeResponseDTOList = new ArrayList<>();
//
//        for (Notice notice : noticeEntityList) {
//            noticeResponseDTOList.add(NoticeResponseDTO.toDTO(notice));
//        }
//
//        return noticeResponseDTOList;
//    }

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

        Optional<Notice> optionalNotice = noticeRepository.findById(id);

        if (optionalNotice.isPresent()){
            Notice notice = optionalNotice.get();
            NoticeDetailResponseDTO noticeDetailResponseDTO = NoticeDetailResponseDTO.toDTO(notice);

            return noticeDetailResponseDTO;
        }else {
            return null;
        }
    }

//    public List<NoticeCategoryResponseDTO> findAllByCategory(String category){
//
//        List<Notice> noticeList = noticeRepository.findAllByCategory(category);
//
//        List<NoticeCategoryResponseDTO> noticeCategoryResponseDTOList = new ArrayList<>();
//
//        for (Notice notice : noticeList) {
//            noticeCategoryResponseDTOList.add(NoticeCategoryResponseDTO.toDTO(notice));
//        }
//
//        return noticeCategoryResponseDTOList;
//
//    }

    public Page<NoticeCategoryResponseDTO> findAllByCategory(String category, int page, int size, String sort){

        String[] sortParams = sort.split(","); // 매개변수 sort 배열으로 나누기
        // sortParams[1]=id, sortParams[2]=desc(내림차순: 11, 10, 9, 8, 7 .... [게시글 id 를 기준으로 한다.] )
        // 내림차순 정렬으로 해야 첫번째 페이지에 가장 최신의 게시글이 뜨게 된다.

        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<Notice> noticeList = noticeRepository.findAllByCategory(category, pageable);

        List<NoticeCategoryResponseDTO> noticeCategoryResponseDTOList = new ArrayList<>();

        for (Notice notice : noticeList) {
            noticeCategoryResponseDTOList.add(NoticeCategoryResponseDTO.toDTO(notice));
        }

        return new PageImpl<>(noticeCategoryResponseDTOList, pageable, noticeList.getTotalElements());

    }

}
