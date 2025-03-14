package org.ayu.doyouknowback.notice.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.form.NoticeCategoryResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
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
        // noticeService.findAll();

        // return ResponseEntity.status(HttpStatus.OK).body();
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<NoticeDetailResponseDTO>> getDetailNotice(){
        // noticeService.findById();

        // return ResponseEntity.status(HttpStatus.OK).body();
        return null;
    }

    @GetMapping("/detail/{category}")
    public ResponseEntity<List<NoticeCategoryResponseDTO>> getCategoryNotice(@PathVariable Long NoticeId){
        // noticeService.findByCategory();

        // return ResponseEntity.status(HttpStatus.OK).body();
        return null;
    }

    @PostMapping("/addNotice")
    public ResponseEntity<String> createNotice(){

        return ResponseEntity.status(HttpStatus.OK).body("Notice Successfully Created");
    }


}
