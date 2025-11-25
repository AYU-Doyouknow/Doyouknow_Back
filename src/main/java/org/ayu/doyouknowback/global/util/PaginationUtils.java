package org.ayu.doyouknowback.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 페이징 및 정렬 관련 유틸리티 클래스
 */
public class PaginationUtils {

    /**
     * sort 문자열을 파싱하여 Sort 객체를 생성
     * 
     * @param sort 정렬 파라미터 문자열 (예: "id,desc" 또는 "createdAt,asc")
     * @return Sort 객체
     * 
     * 사용 예시:
     * Sort sorting = PaginationUtils.createSort("id,desc");
     */
    public static Sort createSort(String sort) {
        String[] sortParams = sort.split(",");
        
        // sortParams[0] = 정렬 필드명 (예: "id")
        // sortParams[1] = 정렬 방향 (예: "desc" 또는 "asc")
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        String fieldName = sortParams[0];
        
        return Sort.by(direction, fieldName);
    }

    /**
     * 페이지 번호, 크기, 정렬 정보를 받아 Pageable 객체를 생성
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (한 페이지당 항목 수)
     * @param sort 정렬 파라미터 문자열 (예: "id,desc")
     * @return Pageable 객체
     * 
     * 사용 예시:
     * Pageable pageable = PaginationUtils.createPageable(0, 10, "id,desc");
     */
    public static Pageable createPageable(int page, int size, String sort) {
        Sort sorting = createSort(sort);
        return PageRequest.of(page, size, sorting);
    }
}
