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

//    @Query("select "
//            + "distinct n "
//            + "from Notice n "
//            + "where "
//            + ""
//    )
//    Optional<Notice> findByLatestNoticeTitle(@Param("noticeTitle") String title, List<Notice> noticeList);

    // Spring Data JPA 사용
    @NonNull
    Page<Notice> findByNoticeCategory(String noticeCategory, @NonNull Pageable pageable);

    // findByNoticeTitleContaining : 키워드 기준으로 검색하되, 쿼리로 작성(공지 제목, 작성자, 내용 필드 값 전부 참조)
    @Query("select "
            + "distinct n "
            + "from Notice n "
            + "where "
            + "   n.noticeTitle like %:kw% "
            + "   or n.noticeWriter like %:kw% "
            + "   or n.noticeBody like %:kw% ")
    Page<Notice> findAllByKeyWord(@Param("kw") String noticeSearchVal, Pageable pageable);

}

//    @Query("select n from Notice n where n.noticeCategory = :noticeCategory")
//    Page<Notice> findAllByCategory(@Param("noticeCategory") String noticeCategory, Pageable pageable);

//    findByNoticeSearchContaining : 키워드 기준 검색
//    @NonNull
//    Page<Notice> findByNoticeTitleContaining(String noticeSearchVal, @NonNull Pageable pageable);