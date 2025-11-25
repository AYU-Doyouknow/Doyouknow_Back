package org.ayu.doyouknowback.domain.notice.infrastructure;

import lombok.NonNull;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @NonNull
    Page<Notice> findAll(@NonNull Pageable pageable);

    @NonNull
    List<Notice> findTop5ByOrderByIdDesc();

    @NonNull
    Page<Notice> findByNoticeCategory(String noticeCategory, @NonNull Pageable pageable);

    @NonNull
    Page<Notice> findByNoticeTitleContainingOrNoticeBodyContaining(
            String value,
            String value1,
            Pageable pageable
    );
}
