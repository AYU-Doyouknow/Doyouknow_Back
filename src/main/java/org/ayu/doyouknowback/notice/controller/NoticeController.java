package org.ayu.doyouknowback.notice.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

//    @GetMapping("/all") // 게시글 전체조회 ( paging 기능 구현 )
//    public ResponseEntity<Page<NoticeResponseDTO>> getAllNotice(
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @RequestParam(required = false, defaultValue = "10") int size,
//            @RequestParam(required = false, defaultValue = "id,desc") String sort){
//
//        Page<NoticeResponseDTO> noticeResponseDTOList = noticeService.findAll(page, size, sort);
//
//        if (noticeResponseDTOList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else return ResponseEntity.status(HttpStatus.OK).body(noticeResponseDTOList);
//    }



    @GetMapping("/all") // 게시글 전체조회 ( paging 기능 구현 )
    public ResponseEntity<Page<NoticeResponseDTO>> getAllNotice(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort){

        Page<NoticeResponseDTO> noticeResponseDTOList = noticeService.findAll(page, size, sort);

        if (noticeResponseDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return ResponseEntity.status(HttpStatus.OK).body(noticeResponseDTOList);
    }

    @GetMapping("/detail/{NoticeId}") // 게시글 상세조회 ( Path Variable )
    public ResponseEntity<NoticeDetailResponseDTO> getDetailNotice(@PathVariable Long NoticeId, @PageableDefault(page=1) Pageable pageable ){
        NoticeDetailResponseDTO noticeDetailResponseDTO = noticeService.findById(NoticeId);
        if (noticeDetailResponseDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            // 204 에러를 반환 : 204 에러는 HTTP 상태 코드로, 서버가 요청을 성공적으로 처리했지만 응답 본문이 없음을 나타냅니다.
        }
        return ResponseEntity.status(HttpStatus.OK).body(noticeDetailResponseDTO);
    }

    @GetMapping("/category") // 카테고리 별 게시글 조회, 페이징 처리 ( Query String : Requestparam )
    public ResponseEntity<Page<NoticeResponseDTO>> getCategoryNotice(
            @RequestParam("noticeCategory") String noticeCategory,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort ){

        Page<NoticeResponseDTO> noticeCategoryResponseDTOList = noticeService.findAllByCategory(noticeCategory, page, size, sort);

        if (noticeCategoryResponseDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return ResponseEntity.status(HttpStatus.OK).body(noticeCategoryResponseDTOList);
    }

    @GetMapping("/search") // title 키워드 별 게시글 조회, 페이징 처리 ( Query String : Requestparam )
    public ResponseEntity<Page<NoticeResponseDTO>> getSearchNotice(
            @RequestParam("noticeSearchVal") String noticeSearchVal,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort ){

        Page<NoticeResponseDTO> noticeSearchResponseDTOList = noticeService.findAllBySearch(noticeSearchVal, page, size, sort);

        if (noticeSearchResponseDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return ResponseEntity.status(HttpStatus.OK).body(noticeSearchResponseDTOList);
    }

    @PostMapping("/addNotice") // 게시글 데이터 요청 저장
    public ResponseEntity<String> createNotice(@RequestBody List<NoticeRequestDTO> noticeRequestDTOList){

        try {
            noticeService.saveLatestNotice(noticeRequestDTOList);
            return ResponseEntity.status(HttpStatus.CREATED).body("Notice Successfully Created");
        } catch (IllegalArgumentException e) { // IllegalArgumentException : 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
