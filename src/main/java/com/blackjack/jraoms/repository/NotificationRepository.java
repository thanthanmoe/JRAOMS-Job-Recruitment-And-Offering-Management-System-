package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query(value = "CALL GetNotificationsByUser(:userId)", nativeQuery = true)
    List<Notification> getNotificationsByUser(int userId);

    @Query(value = "CALL GetNotificationByUserAndPost(:userId, :vacancyId)", nativeQuery = true)
    List<Notification> getNotificationsByUserAndPost(int userId, int vacancyId);

}