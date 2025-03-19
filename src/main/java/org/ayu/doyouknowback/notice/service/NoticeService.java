package org.ayu.doyouknowback.notice.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.notice.domain.Notice;
import org.ayu.doyouknowback.notice.form.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.notice.form.NoticeResponseDTO;
import org.ayu.doyouknowback.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public List<NoticeResponseDTO> findAll(){

        List<Notice> noticeEntityList = noticeRepository.findAll();

        List<NoticeResponseDTO> noticeResponseDTOList = new ArrayList<>();

        for (Notice notice : noticeEntityList) {
            noticeResponseDTOList.add(NoticeResponseDTO.toDTO(notice));
        }

        return noticeResponseDTOList;
    }

    public NoticeDetailResponseDTO findById(Long id){

        Optional<Notice> optionalNotice = noticeRepository.findById(id);

        if (optionalNotice.isPresent()){
            Notice notice = optionalNotice.get();
            NoticeDetailResponseDTO noticeDetailResponseDTO = NoticeDetailResponseDTO.toDTO(notice);

            return noticeDetailResponseDTO;
        }else {
            return null;
        }
    }

}
