package org.ayu.doyouknowback.fcm.repository;

import org.ayu.doyouknowback.fcm.domain.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<Fcm, Long> {
    Optional<Fcm> findByToken(String token);
    List<Fcm> findAll();
    void deleteByToken(String toekn);
}
