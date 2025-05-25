package org.ayu.doyouknowback.news.controller;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequiredArgsConstructor
    @RequestMapping("/news")

public class NewsController {
    private final NewsService newsService;

    @GetMapping("/all") // 게시글 전체조회 ENDPoint /doyouknow/news/all
    public ResponseEntity<Page<NewsResponseDTO>> getAllNews(// 전체 공지를 반환하는 메서드
    @RequestParam(required = false, defaultValue = "0") int page, //요청한 페이지 번호를 받아옵니다. 파라미터는 필수 전달x, 클라이언트 첫 페이지는 0번부터 시작합니다.
    @RequestParam(required = false, defaultValue = "10") int size, //한 페이지에서 몇 개의 항목을 조회할지 결정합니다. 기본 값은 10개입니다.
    @RequestParam(required = false, defaultValue = "id,desc") String sort){ //파라미터 필수 x, id를 기준으로 내림차순 최신순으로 정려한다.
        Page<NewsResponseDTO> newsResponseDTOList = newsService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(newsResponseDTOList);
    }
    @GetMapping("/detail/{NewsId}")//ENDPoint /doyouknow/news/{id}
    public ResponseEntity<NewsDetailResponseDTO> getNewsDetail(@PathVariable Long NewsId){ //세부사항 조회
        NewsDetailResponseDTO newsDetailResponseDTO = newsService.findById(NewsId);
        return ResponseEntity.status(HttpStatus.OK).body(newsDetailResponseDTO);
    }
    @PostMapping("/addNews")//새로운 뉴스 추가
    public ResponseEntity<String> createNews(@RequestBody List<NewsRequestDTO> newsRequestDTOList){
        newsService.save(newsRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("News successfully created");
    }

    // 뉴스 제목 검색 API
    @GetMapping("/search")
    public ResponseEntity<Page<NewsResponseDTO>> searchNews(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort) {

        Page<NewsResponseDTO> result = newsService.searchByTitle(keyword, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
