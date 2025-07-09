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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    /* // 학교소식 저장 (POST)
    @Transactional
    public void save(List<NewsRequestDTO> newsRequestDTOList) {
        List<News> newsList = new ArrayList<>();

        for (NewsRequestDTO newsRequestDTO : newsRequestDTOList) {
            newsList.add(News.toSaveEntity(newsRequestDTO));
        }

        newsRepository.saveAll(newsList);
    }*/
    @Transactional
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {

        // 1. DB에서 최신 뉴스 5개 가져오기 (id 기준 내림차순)
        List<News> latestNewsList = newsRepository.findTop5ByOrderByIdDesc();

        // 2. 저장할 뉴스 리스트 준비
        List<News> newsListToSave = new ArrayList<>();

        // 3. 새로 받은 뉴스들에 대해 중복 여부 확인
        for (NewsRequestDTO newsRequestDTO : newsRequestDTOList) {
            boolean isExist = false;

            for (News latestNews : latestNewsList) {
                if (Objects.equals(latestNews.getNewsTitle(), newsRequestDTO.getNewsTitle())) {
                    isExist = true;
                    break;
                }
            }

            // 4. 중복이 아니면 엔티티로 변환 후 저장 목록에 추가
            if (!isExist) {
                newsListToSave.add(News.toSaveEntity(newsRequestDTO));
            }
        }

        // 5. 중복 없는 뉴스들을 DB에 저장
        newsRepository.saveAll(newsListToSave);
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
        Page<News> newsEntityPage = newsRepository.findByNewsTitleContainingOrNewsBodyContaining(keyword, keyword, pageable);

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
