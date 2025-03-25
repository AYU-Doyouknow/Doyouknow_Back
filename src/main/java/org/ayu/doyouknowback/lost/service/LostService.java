package org.ayu.doyouknowback.lost.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.domain.Lost;
import org.ayu.doyouknowback.lost.form.LostDetailResponseDTO;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.lost.repository.LostRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;

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
        List<Lost> lostEntity = new ArrayList<>();

        for(LostRequestDTO lostRequestDTO : lostRequestDTOList){
            lostEntity.add(Lost.toSaveEntity(lostRequestDTO));
        }

        lostRepository.saveAll(lostEntity);
    }


}
