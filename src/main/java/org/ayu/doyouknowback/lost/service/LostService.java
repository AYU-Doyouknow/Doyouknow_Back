package org.ayu.doyouknowback.lost.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.fcm.service.FcmService;
import org.ayu.doyouknowback.lost.domain.Lost;
import org.ayu.doyouknowback.lost.form.LostDetailResponseDTO;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.lost.repository.LostRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;
    private final FcmService fcmService;

//    @Transactional(readOnly = true)
//    //jpa는 자동적으로 변경감지를 수행해서 엔티티의 변경 여부를 감시한다.
//    //readOnly=true를 사용하면 변경 감지가 비활성화 되어 성능이 향상된다.
//    public List<LostResponseDTO> getAll() {
//        List<Lost> lostEntityList = lostRepository.findAll();
//        List<LostResponseDTO> lostResponseList = new ArrayList<>();
//
//        for(Lost lost : lostEntityList){
//            lostResponseList.add(LostResponseDTO.fromEntity(lost));
//        }
//
//        return lostResponseList;
//    }

    @Transactional(readOnly = true)
    public Page<LostResponseDTO> getAll(int page, int size, String sort){
        String[] sortParams = sort.split(",");

        // Sort.Direction.fromString(desc) => Sort.Direction.DESC로 변환
        // Sort.by(Sort.Direction.DESC, "id") => id별로 내림차순 정렬
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);

        //페이지 객체 생성
        Pageable pageable = PageRequest.of(page, size, sorting);

        //페이지 담고
        Page<Lost> lostEntity = lostRepository.findAll(pageable);

        // dto -> entity 변환
        List<LostResponseDTO> lostDTO = new ArrayList<>();
        for(Lost lost : lostEntity){
            lostDTO.add(LostResponseDTO.fromEntity(lost));
        }

        return new PageImpl<>(lostDTO, pageable, lostEntity.getTotalElements());
    }

    public Page<LostResponseDTO> getSearch(String value, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Lost> lostEntity = lostRepository.findByLostTitleContainingOrLostBodyContaining(value, value, pageable);

        List<LostResponseDTO> lostDTO = new ArrayList<>();
        for(Lost lost : lostEntity){
            lostDTO.add(LostResponseDTO.fromEntity(lost));
        }

        return new PageImpl<>(lostDTO, pageable, lostEntity.getTotalElements());
    }

    public LostDetailResponseDTO getfindById(Long lostId) {
        Optional<Lost> lostOptional = lostRepository.findById(lostId);

        if(lostOptional.isPresent()){
            Lost lost = lostOptional.get();
            LostDetailResponseDTO lostDetailResponseDTO = LostDetailResponseDTO.fromEntity(lost);
            return lostDetailResponseDTO;
        }else{
            return null;
        }
    }

    @Transactional
    public void createLost(List<LostRequestDTO> lostRequestDTOList) {
        // 최근 5개 분실물 가져오기
        List<Lost> last = lostRepository.findTop5ByOrderByIdDesc();

        // DB에 있는 ID만 모아두기
        List<Long> dbIds = new ArrayList<>();
        for (Lost lost : last) {
            dbIds.add(lost.getId());
        }

        // 새로운 분실물 리스트 선별
        List<LostRequestDTO> newLost = new ArrayList<>();
        for (LostRequestDTO dto : lostRequestDTOList) {
            if (!dbIds.contains(dto.getId())) {
                newLost.add(dto);
            }
        }

        int count = newLost.size();
        System.out.println("새로 등록될 항목 수: " + count);
        System.out.println(count);

        if(count == 1){
            String title = newLost.get(0).getLostTitle();
            fcmService.sendNotificationToAllExpo("[분실습득]", title + " 게시글이 등록되었습니다.");
            saveLost(lostRequestDTOList);
        }else if(count > 1){
            // 가장 ID가 큰 항목 찾기
            LostRequestDTO latest = newLost.get(0);
            for (LostRequestDTO dto : newLost) {
                if (dto.getId() > latest.getId()) {
                    latest = dto;
                }
            }

            String title = latest.getLostTitle();
            fcmService.sendNotificationToAllExpo("[분실습득]", title + " 외 " + (count -1) + "개 게시글이 등록되었습니다.");
            saveLost(lostRequestDTOList);
        }else{
            return;
        }
    }

    private boolean isSame(Lost db, LostRequestDTO dto) {
        System.out.println("비교 : " + db.getId() + " " + dto.getId());
        return Objects.equals(db.getLostTitle(), dto.getLostTitle());
    }

    private void saveLost(List<LostRequestDTO> lostRequestDTOList){
        List<Lost> lostEntity = new ArrayList<>();

        for(LostRequestDTO lostRequestDTO : lostRequestDTOList){
            lostEntity.add(Lost.toSaveEntity(lostRequestDTO));
        }

        lostRepository.saveAll(lostEntity);
    }
}
