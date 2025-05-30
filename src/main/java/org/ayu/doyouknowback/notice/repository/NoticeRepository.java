package org.ayu.doyouknowback.notice.repository;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @NonNull
    Page<Notice> findAll(@NonNull Pageable pageable);

    // Noticetitle 상위 5개 최신 글 조회
    @NonNull
    List<Notice> findTop5ByOrderByIdDesc(); // NoticeId 로 바꿔야하나?

    @NonNull
    Optional<Notice> findByNoticeTitle(String title);

    // Spring Data JPA 사용
    @NonNull
    Page<Notice> findByNoticeCategory(String noticeCategory, @NonNull Pageable pageable);

}
