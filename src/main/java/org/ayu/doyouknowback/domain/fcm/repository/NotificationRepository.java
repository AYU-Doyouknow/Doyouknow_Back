package org.ayu.doyouknowback.domain.fcm.repository;

import org.ayu.doyouknowback.domain.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 토큰 조회
    @Query("SELECT n FROM Notification n WHERE n.token.value = :token")
    Optional<Notification> findByToken(@Param("token") String token);
}
