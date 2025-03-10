package org.ayu.doyouknowback.lost.repository;

import org.ayu.doyouknowback.lost.domain.Lost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostRepository extends JpaRepository<Lost, Long> {

}
