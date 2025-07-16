package org.ayu.doyouknowback.news.service;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.fcm.service.FcmService;
import org.ayu.doyouknowback.news.domain.News;
import org.ayu.doyouknowback.news.form.NewsDetailResponseDTO;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.news.repository.NewsRepository;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
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
    private final FcmService fcmService;

    // 학교소식 저장 (POST)
    @Transactional
    public void save(List<NewsRequestDTO> newsRequestDTOList) {
        List<News> newsList = new ArrayList<>();

        for (NewsRequestDTO newsRequestDTO : newsRequestDTOList) {
            newsList.add(News.toSaveEntity(newsRequestDTO));
        }

        newsRepository.saveAll(newsList);
    }

    @Transactional
    public void saveLatestNews(List<NewsRequestDTO> newsRequestDTOList) {
        // 최근 5개 뉴스 가져오기
        List<News> latestNews = newsRepository.findTop5ByOrderByIdDesc();

        System.out.println("========DB에서 불러온 최근 5개의 학교소식========");
        for (News news : latestNews) {
            System.out.println("id: " + news.getId() + ", title: " + news.getNewsTitle());
        }

        System.out.println("=======크롤링으로 불러온 최근 5개의 학교소식=======");
        for(NewsRequestDTO news : newsRequestDTOList){
            System.out.println("id: " + news.getId() + ", title: " + news.getNewsTitle());
        }

        // DB에 있는 ID만 모아두기
        List<Long> dbIds = new ArrayList<>();
        for (News news : latestNews) {
            dbIds.add(news.getId());
        }

        // 새로운 뉴스 리스트 선별
        List<NewsRequestDTO> newNewsList = new ArrayList<>();
        for (NewsRequestDTO dto : newsRequestDTOList) {
            if (!dbIds.contains(dto.getId())) {
                newNewsList.add(dto);
            }
        }

        int count = newNewsList.size();
        System.out.println("새로 등록될 뉴스 수: " + count);

        if (count == 0) {
            return;
        }

        // 저장할 뉴스 변환
        List<News> newsToSave = new ArrayList<>();
        for (NewsRequestDTO dto : newNewsList) {
            newsToSave.add(News.toSaveEntity(dto));
        }

        // 알림 메시지 전송 (1개 또는 여러 개)
        if (count == 1) {
            String title = newNewsList.get(0).getNewsTitle();
            fcmService.sendNotificationToAllUser("[뉴스]", title + " 뉴스가 등록되었습니다.");
        } else {
            // 가장 ID가 큰 뉴스 제목 선택
            NewsRequestDTO latest = newNewsList.get(0);
            for (NewsRequestDTO dto : newNewsList) {
                if (dto.getId() > latest.getId()) {
                    latest = dto;
                }
            }
            String title = latest.getNewsTitle();
            fcmService.sendNotificationToAllUser("[뉴스]", title + " 외 " + (count - 1) + "개 뉴스가 등록되었습니다.");
        }

        // DB 저장
        newsRepository.saveAll(newsToSave);
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
