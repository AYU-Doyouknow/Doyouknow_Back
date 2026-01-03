package org.ayu.doyouknowback.domain.fcm.repository;

import org.ayu.doyouknowback.domain.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT f FROM Notification f WHERE f.token.value = :token")
    Optional<Notification> findByToken(@Param("token") String token);

    List<Notification> findAll();

    @Modifying
    @Query("DELETE FROM Notification f WHERE f.token.value = :token")
    void deleteByToken(@Param("token") String token);
}
