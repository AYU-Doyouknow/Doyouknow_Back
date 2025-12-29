package org.ayu.doyouknowback.domain.notice.service;

import org.ayu.doyouknowback.domain.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NoticeService {

    void saveLatestNotice(List<NoticeRequestDTO> noticeRequestDTOList);

    Page<NoticeResponseDTO> findAll(int page, int size, String sort);

    NoticeDetailResponseDTO findById(Long id);

    Page<NoticeResponseDTO> findAllByCategory(String category, int page, int size, String sort);

    Page<NoticeResponseDTO> findAllBySearch(String noticeSearchVal, int page, int size, String sort);
}
