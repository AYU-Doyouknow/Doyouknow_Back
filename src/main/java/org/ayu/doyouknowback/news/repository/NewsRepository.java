package org.ayu.doyouknowback.news.repository;

import org.ayu.doyouknowback.news.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findAll(Pageable pageable);//Page는 페이징된 결과를 담음 즉, 제목, 개수, 번호 등 내용을 담아옴
    Page<News> findByNewsTitleContainingOrNewsBodyContaining(String title, String body, Pageable pageable);// 뉴스 제목 검색 기능
}
