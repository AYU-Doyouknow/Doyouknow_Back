package org.ayu.doyouknowback.news.repository;


import org.ayu.doyouknowback.news.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
