package org.ayu.doyouknowback.news.service;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.news.domain.News;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
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
    public Page<NewsResponseDTO> getAll(int page, int size, String sort) {
        String[] sortParams = sort.split(",");

        // Sort.Direction.fromString(desc) => Sort.Direction.DESC로 변환
        // Sort.by(Sort.Direction.DESC, "id") => id별로 내림차순 정렬
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        //페이지 객체 생성
        Pageable pageable = PageRequest.of(page, size, sorting);

        //페이지 담고
        Page<News> newsEntity = newsRepository.findAll(pageable);

        // dto -> entity 변환
        List<NewsResponseDTO> newsDTO = new ArrayList<>();
        for(News news : newsEntity){
            newsDTO.add(NewsResponseDTO.fromEntity(news));
        }
        return new PageImpl<>(newsDTO, pageable, newsEntity.getTotalElements());
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
    @Transactional(readOnly = true) // 뉴스 제목 또는 본문을 통해 검색하기
    public Page<NewsResponseDTO> searchByTitle(String keyword, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";

        Sort sorting = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sorting);

        // 제목 또는 본문에 키워드가 포함된 뉴스 검색
        Page<News> newsEntityPage = newsRepository.findByNewsTitleContaining(keyword, pageable);

        List<NewsResponseDTO> newsDTOList = new ArrayList<>();
        for (News news : newsEntityPage) {
            newsDTOList.add(NewsResponseDTO.fromEntity(news));
        }

        return new PageImpl<>(newsDTOList, pageable, newsEntityPage.getTotalElements());
    }

    @Transactional
    public void SaveLateNews(List<NewsRequestDTO> NewsRequestDTOList) {
        List<News> newsList = new ArrayList<>();
        for (NewsRequestDTO newsRequestDTO : NewsRequestDTOList) {
            String newsTitle = newsRequestDTO.getNewsTitle();
        }
    }

    @Transactional(readOnly = true)
    public List<NewsResponseDTO> getTop5LatestNews() {
        List<News> newsList = newsRepository.findTop5ByOrderByIdDesc();
        List<NewsResponseDTO> result = new ArrayList<>();
        for (News news : newsList) {
            result.add(NewsResponseDTO.fromEntity(news));
        }
        return result;
    }

}
