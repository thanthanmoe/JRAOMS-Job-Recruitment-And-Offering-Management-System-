package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.InterviewerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterviewerNotificationRepository extends JpaRepository<InterviewerNotification,Integer> {
    @Query(value = "CALL GetInterviewerNotificationsByUser(:userId)", nativeQuery = true)
    List<InterviewerNotification> getInterviewerNotificationsByUser(int userId);
}
