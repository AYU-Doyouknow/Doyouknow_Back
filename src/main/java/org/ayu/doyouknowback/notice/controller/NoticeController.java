package org.ayu.doyouknowback.notice.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.form.NoticeCategoryResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

//    @GetMapping("/all") // 게시글 전체조회
//    public ResponseEntity<List<NoticeResponseDTO>> getAllNotice(){
//
//        List<NoticeResponseDTO> noticeResponseDTOList = noticeService.findAll();
//
//        return ResponseEntity.status(HttpStatus.OK).body(noticeResponseDTOList);
//    }

    @GetMapping("/all") // 게시글 전체조회 ( paging 기능 구현 )
    public ResponseEntity<Page<NoticeResponseDTO>> getAllNotice(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort){

        Page<NoticeResponseDTO> noticeResponseDTOList = noticeService.findAll(page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(noticeResponseDTOList);
    }

    @GetMapping("/detail/{NoticeId}") // 게시글 상세조회 ( Path Variable )
    public ResponseEntity<NoticeDetailResponseDTO> getDetailNotice(@PathVariable Long NoticeId){

        NoticeDetailResponseDTO noticeDetailResponseDTO = noticeService.findById(NoticeId);

        return ResponseEntity.status(HttpStatus.OK).body(noticeDetailResponseDTO);
    }

//    @GetMapping("/category") // 카테고리 별 게시글 조회 ( Query String : Requestparam )
//    public ResponseEntity<List<NoticeCategoryResponseDTO>> getCategoryNotice(@RequestParam("noticeCategory") String noticeCategory){
//
//        List<NoticeCategoryResponseDTO> noticeCategoryResponseDTOList = noticeService.findAllByCategory(noticeCategory);
//
//        return ResponseEntity.status(HttpStatus.OK).body(noticeCategoryResponseDTOList);
//        // return null;
//    }

    @GetMapping("/category") // 카테고리 별 게시글 조회, 페이징 처리 ( Query String : Requestparam )
    public ResponseEntity<Page<NoticeCategoryResponseDTO>> getCategoryNotice(
            @RequestParam("noticeCategory") String noticeCategory,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort ){

        Page<NoticeCategoryResponseDTO> noticeCategoryResponseDTOList = noticeService.findAllByCategory(noticeCategory, page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(noticeCategoryResponseDTOList);
        // return null;
    }


    @PostMapping("/addNotice") // 게시글 데이터 요청 저장
    public ResponseEntity<String> createNotice(@RequestBody List<NoticeRequestDTO> noticeRequestDTOList){

        noticeService.save(noticeRequestDTOList);

        return ResponseEntity.status(HttpStatus.OK).body("Notice Successfully Created");
    }


}
