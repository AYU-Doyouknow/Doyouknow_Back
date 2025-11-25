package org.ayu.doyouknowback.global.util;

import org.ayu.doyouknowback.domain.lost.domain.Lost;
import org.ayu.doyouknowback.domain.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsResponseDTO;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.form.NoticeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity를 DTO로 변환하는 유틸리티 클래스
 */
public class DtoConversionUtils {

    /**
     * Lost 엔티티 리스트를 LostResponseDTO 리스트로 변환
     * 
     * @param entities Lost 엔티티 리스트
     * @return LostResponseDTO 리스트
     * 
     * 사용 예시:
     * List<LostResponseDTO> dtoList = DtoConversionUtils.convertLostListToDto(lostEntities);
     */
    public static List<LostResponseDTO> convertLostListToDto(List<Lost> entities) {
        List<LostResponseDTO> dtoList = new ArrayList<>();
        for (Lost entity : entities) {
            dtoList.add(LostResponseDTO.fromEntity(entity));
        }
        return dtoList;
    }

    /**
     * News 엔티티 리스트를 NewsResponseDTO 리스트로 변환
     * 
     * @param entities News 엔티티 리스트
     * @return NewsResponseDTO 리스트
     * 
     * 사용 예시:
     * List<NewsResponseDTO> dtoList = DtoConversionUtils.convertNewsListToDto(newsEntities);
     */
    public static List<NewsResponseDTO> convertNewsListToDto(List<News> entities) {
        List<NewsResponseDTO> dtoList = new ArrayList<>();
        for (News entity : entities) {
            dtoList.add(NewsResponseDTO.fromEntity(entity));
        }
        return dtoList;
    }

    /**
     * Notice 엔티티 리스트를 NoticeResponseDTO 리스트로 변환
     * 
     * @param entities Notice 엔티티 리스트
     * @return NoticeResponseDTO 리스트
     * 
     * 사용 예시:
     * List<NoticeResponseDTO> dtoList = DtoConversionUtils.convertNoticeListToDto(noticeEntities);
     */
    public static List<NoticeResponseDTO> convertNoticeListToDto(List<Notice> entities) {
        List<NoticeResponseDTO> dtoList = new ArrayList<>();
        for (Notice entity : entities) {
            dtoList.add(NoticeResponseDTO.toDTO(entity));
        }
        return dtoList;
    }

    /**
     * Lost 엔티티 Page를 LostResponseDTO Page로 변환
     * 
     * @param entityPage Lost 엔티티 Page
     * @param pageable Pageable 객체
     * @return LostResponseDTO Page
     * 
     * 사용 예시:
     * Page<LostResponseDTO> dtoPage = DtoConversionUtils.convertLostPageToDto(entityPage, pageable);
     */
    public static Page<LostResponseDTO> convertLostPageToDto(Page<Lost> entityPage, Pageable pageable) {
        List<LostResponseDTO> dtoList = convertLostListToDto(entityPage.getContent());
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    /**
     * News 엔티티 Page를 NewsResponseDTO Page로 변환
     * 
     * @param entityPage News 엔티티 Page
     * @param pageable Pageable 객체
     * @return NewsResponseDTO Page
     * 
     * 사용 예시:
     * Page<NewsResponseDTO> dtoPage = DtoConversionUtils.convertNewsPageToDto(entityPage, pageable);
     */
    public static Page<NewsResponseDTO> convertNewsPageToDto(Page<News> entityPage, Pageable pageable) {
        List<NewsResponseDTO> dtoList = convertNewsListToDto(entityPage.getContent());
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    /**
     * Notice 엔티티 Page를 NoticeResponseDTO Page로 변환
     * 
     * @param entityPage Notice 엔티티 Page
     * @param pageable Pageable 객체
     * @return NoticeResponseDTO Page
     * 
     * 사용 예시:
     * Page<NoticeResponseDTO> dtoPage = DtoConversionUtils.convertNoticePageToDto(entityPage, pageable);
     */
    public static Page<NoticeResponseDTO> convertNoticePageToDto(Page<Notice> entityPage, Pageable pageable) {
        List<NoticeResponseDTO> dtoList = convertNoticeListToDto(entityPage.getContent());
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }
}
