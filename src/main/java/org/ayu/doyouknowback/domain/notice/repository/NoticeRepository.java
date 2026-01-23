package org.ayu.doyouknowback.domain.notice.repository;

import lombok.NonNull;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.projection.NoticeSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @NonNull
    Page<Notice> findAll(@NonNull Pageable pageable);

    // Noticetitle 상위 5개 최신 글 조회
    @NonNull
    List<Notice> findTop5ByOrderByIdDesc(); // NoticeId 로 바꿔야하나?

    // Spring Data JPA 사용
    @NonNull
    Page<Notice> findByNoticeCategory(String noticeCategory, @NonNull Pageable pageable);

    @NonNull
    Page<Notice> findByNoticeTitleContainingOrNoticeBodyContaining(String value, String value1, Pageable pageable);

    @Query( value = """
            SELECT *
            FROM notice
            WHERE MATCH(notice_title, notice_body)
                  AGAINST (:q IN BOOLEAN MODE)
            ORDER BY id DESC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM notice
            WHERE MATCH(notice_title, notice_body)
                  AGAINST (:q IN BOOLEAN MODE)
        """,
            nativeQuery = true )
    Page<Notice> searchByFullText(@Param("q") String q, Pageable pageable);

    // 최소 컬럼만 가져오기 위한 projection 적용
    @Query(
            value = """
        SELECT
            n.id              AS id,
            n.notice_title    AS noticeTitle,
            n.notice_writer   AS noticeWriter,
            n.notice_date     AS noticeDate,
            n.notice_category AS noticeCategory
        FROM notice n
        WHERE MATCH(n.notice_title, n.notice_body)
              AGAINST (:q IN BOOLEAN MODE)
        ORDER BY n.id DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM notice n
        WHERE MATCH(n.notice_title, n.notice_body)
              AGAINST (:q IN BOOLEAN MODE)
    """,
            nativeQuery = true
    )
    Page<NoticeSummaryView> searchByFullTextSummary(@Param("q") String q, Pageable pageable);

}
