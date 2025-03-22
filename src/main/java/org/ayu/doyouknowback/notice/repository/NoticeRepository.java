package org.ayu.doyouknowback.notice.repository;

import org.ayu.doyouknowback.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("select n from Notice n where n.noticeCategory = :noticeCategory")
    List<Notice> findAllByCategory(@Param("noticeCategory") String noticeCategory);

}
