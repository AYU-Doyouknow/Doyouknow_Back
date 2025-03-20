package org.ayu.doyouknowback.notice.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.form.NoticeCategoryResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.service.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/all") // 게시글 전체조회
    public ResponseEntity<List<NoticeResponseDTO>> getAllNotice(){

        List<NoticeResponseDTO> noticeResponseDTOList = noticeService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(noticeResponseDTOList);
    }

    @GetMapping("/detail/{NoticeId}")
    public ResponseEntity<NoticeDetailResponseDTO> getDetailNotice(@PathVariable Long NoticeId){

        NoticeDetailResponseDTO noticeDetailResponseDTO = noticeService.findById(NoticeId);

        return ResponseEntity.status(HttpStatus.OK).body(noticeDetailResponseDTO);
    }

    @GetMapping("/category")
    public ResponseEntity<List<NoticeCategoryResponseDTO>> getCategoryNotice(@RequestParam("noticeCategory") String noticeCategory){

        List<NoticeCategoryResponseDTO> noticeCategoryResponseDTOList = noticeService.findAllByCategory(noticeCategory);

        return ResponseEntity.status(HttpStatus.OK).body(noticeCategoryResponseDTOList);
        // return null;
    }

    @PostMapping("/addNotice")
    public ResponseEntity<String> createNotice(@RequestBody List<NoticeRequestDTO> noticeRequestDTOList){

        noticeService.save(noticeRequestDTOList);

        return ResponseEntity.status(HttpStatus.OK).body("Notice Successfully Created");
    }


}
