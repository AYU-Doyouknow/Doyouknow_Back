package org.ayu.doyouknowback.lost.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.domain.Lost;
import org.ayu.doyouknowback.lost.form.LostDetailResponseDTO;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.lost.repository.LostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;

    @Transactional(readOnly = true)
    //jpa는 자동적으로 변경감지를 수행해서 엔티티의 변경 여부를 감시한다.
    //readOnly=true를 사용하면 변경 감지가 비활성화 되어 성능이 향상된다.
    public List<LostResponseDTO> getAll() {
        List<Lost> lostEntityList = lostRepository.findAll();
        List<LostResponseDTO> lostResponseList = new ArrayList<>();

        for(Lost lost : lostEntityList){
            lostResponseList.add(LostResponseDTO.fromEntity(lost));
        }

        return lostResponseList;
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
