package org.ayu.doyouknowback.domain.lost.repository;


import org.ayu.doyouknowback.domain.lost.domain.Lost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostRepository extends JpaRepository<Lost, Long> {
    Page<Lost> findAll(Pageable pageable);
    List<Lost> findTop5ByOrderByIdDesc();
    Page<Lost> findByLostTitleContainingOrLostBodyContaining(String value, String value1, Pageable pageable);
}
