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

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @NonNull
    Page<Notice> findAll(@NonNull Pageable pageable);

//    @Query("select n from Notice n where n.noticeCategory = :noticeCategory")
//    List<Notice> findAllByCategory(@Param("noticeCategory") String noticeCategory);

    @Query("select n from Notice n where n.noticeCategory = :noticeCategory")
    Page<Notice> findAllByCategory(@Param("noticeCategory") String noticeCategory, Pageable pageable);

}