package org.ayu.doyouknowback.notice.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void save(List<NoticeRequestDTO> noticeRequestDTOList){

        List<Notice> noticeList = new ArrayList<>();

        for (NoticeRequestDTO noticeRequestDTO : noticeRequestDTOList) {
            noticeList.add(Notice.toSaveEntity(noticeRequestDTO));
        }

        noticeRepository.saveAll(noticeList);
    }
}
