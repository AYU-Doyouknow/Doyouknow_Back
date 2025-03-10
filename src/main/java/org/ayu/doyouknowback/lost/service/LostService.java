package org.ayu.doyouknowback.lost.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.domain.Lost;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.repository.LostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LostService {

    private final LostRepository repository;

    @Transactional
    public void createLost(List<LostRequestDTO> lostRequestDTOList) {
        List<Lost> lostEntity = new ArrayList<>();

        for(LostRequestDTO lostRequestDTO : lostRequestDTOList){
            lostEntity.add(Lost.toSaveEntity(lostRequestDTO));
        }

        repository.saveAll(lostEntity);
    }
}
