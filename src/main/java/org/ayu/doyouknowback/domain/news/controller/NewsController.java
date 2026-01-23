package org.ayu.doyouknowback.domain.news.controller;

import org.ayu.doyouknowback.domain.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.news.service.NewsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(@Qualifier("newsCacheService") NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<NewsResponseDTO>> getAllNews(
    @RequestParam(required = false, defaultValue = "0") int page,
    @RequestParam(required = false, defaultValue = "10") int size,
    @RequestParam(required = false, defaultValue = "id,desc") String sort){
        Page<NewsResponseDTO> newsResponseDTOList = newsService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(newsResponseDTOList);
    }

    @GetMapping("/detail/{NewsId}")
    public ResponseEntity<NewsDetailResponseDTO> getNewsDetail(@PathVariable Long NewsId){ //세부사항 조회
        NewsDetailResponseDTO newsDetailResponseDTO = newsService.findById(NewsId);
        return ResponseEntity.status(HttpStatus.OK).body(newsDetailResponseDTO);
    }

    @PostMapping("/addNews")
    public ResponseEntity<String> createNews(@RequestBody List<NewsRequestDTO> newsRequestDTOList){
        newsService.saveLatestNews(newsRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("News successfully created");
    }

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
