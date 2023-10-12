package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.InterviewerNotification;
import com.blackjack.jraoms.entity.OfferMailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferMailNotificationRepository extends JpaRepository<OfferMailNotification,Integer> {

    @Query(value = "CALL GetOfferMailNotificationsByUser(:userId)", nativeQuery = true)
    List<OfferMailNotification> GetOfferMailNotificationsByUser(int userId);
}
