package org.ayu.doyouknowback.lost.repository;


import org.ayu.doyouknowback.lost.domain.Lost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostRepository extends JpaRepository<Lost, Long> {
    Page<Lost> findAll(Pageable pageable);

    Page<Lost> findByLostTitleContainingOrLostBodyContaining(String value, String value1, Pageable pageable);
}
