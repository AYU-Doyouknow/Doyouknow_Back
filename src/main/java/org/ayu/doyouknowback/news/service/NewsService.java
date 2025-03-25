package org.ayu.doyouknowback.news.service;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.news.domain.News;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.repository.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    // 학교소식 저장 (POST)
    @Transactional
    public void save(List<NewsRequestDTO> newsRequestDTOList) {
        List<News> newsList = new ArrayList<>();

        for (NewsRequestDTO newsRequestDTO : newsRequestDTOList) {
            newsList.add(News.toSaveEntity(newsRequestDTO));
        }

        newsRepository.saveAll(newsList);
    }

    // 학교소식 전체 조회 (GET)
    @Transactional(readOnly = true)
    public List<NewsResponseDTO> getAll() {
        List<News> newsEntityList = newsRepository.findAll();
        List<NewsResponseDTO> newsResponseDTOList = new ArrayList<>();

        for (News news : newsEntityList) {
            newsResponseDTOList.add(NewsResponseDTO.fromEntity(news));
        }

        return newsResponseDTOList;
    }

    // 학교소식 세부 조회 (GET)
    @Transactional(readOnly = true)
    public NewsDetailResponseDTO findById(Long Id) {
        Optional<News> optionalNews = newsRepository.findById(Id);

        if (optionalNews.isPresent()) {
            News news = optionalNews.get();
            return NewsDetailResponseDTO.fromEntity(news);
        } else {
            return null;
        }
    }


}
