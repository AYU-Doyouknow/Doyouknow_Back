package org.ayu.doyouknowback.domain.notice.presentation;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.domain.notice.application.NoticeService;
import org.ayu.doyouknowback.domain.notice.form.response.NoticeDetailResponseDTO;
import org.ayu.doyouknowback.domain.notice.form.request.NoticeRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.response.NoticeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/all")
    public ResponseEntity<Page<NoticeResponseDTO>> getAllNotice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Page<NoticeResponseDTO> noticePage = noticeService.findAll(page, size, sort);

        if (noticePage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.status(HttpStatus.OK).body(noticePage);
    }

    @GetMapping("/detail/{noticeId}")
    public ResponseEntity<NoticeDetailResponseDTO> getDetailNotice(@PathVariable("noticeId") Long noticeId) {

        NoticeDetailResponseDTO dto = noticeService.findById(noticeId);

        if (dto == null) { // 사실 findById가 예외를 던지니까 이 분기 안 타긴 함
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<NoticeResponseDTO>> getCategoryNotice(
            @RequestParam("noticeCategory") String noticeCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Page<NoticeResponseDTO> noticePage =
                noticeService.findAllByCategory(noticeCategory, page, size, sort);

        if (noticePage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.status(HttpStatus.OK).body(noticePage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDTO>> getSearchNotice(
            @RequestParam("noticeSearchVal") String noticeSearchVal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Page<NoticeResponseDTO> noticePage =
                noticeService.findAllBySearch(noticeSearchVal, page, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(noticePage);
    }

    @PostMapping("/addNotice")
    public ResponseEntity<String> createNotice(@RequestBody List<NoticeRequestDTO> noticeRequestDTOList) {

        try {
            noticeService.saveLatestNotice(noticeRequestDTOList);
            return ResponseEntity.status(HttpStatus.CREATED).body("Notice Successfully Created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
