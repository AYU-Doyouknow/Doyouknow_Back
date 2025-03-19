package org.ayu.doyouknowback.news.controller;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequiredArgsConstructor
    @RequestMapping("/news")

public class NewsController {
    private final NewsService newsService;

    @GetMapping("/all") // 게시글 전체조회 ENDPoint /doyouknow/news/all
    public ResponseEntity<List<NewsResponseDTO>> getAllNews(){ // 전체 공지를 반환하는 메서드
        List<NewsResponseDTO> newsResponseDTOList = newsService.getAll();
        return null;
    }
    @GetMapping("/{id}")//ENDPoint /doyouknow/news/{id}
    public ResponseEntity<List<NewsDetailResponseDTO>> getNewsDetail(@PathVariable Long id){ //세부사항 조회
        //구현
        return null;
    }
    @PostMapping("/addNews")//새로운 뉴스 추가
    public ResponseEntity<String> createNews(@RequestBody NewsRequestDTO newsRequestDTO){

        return null;
    }


}
