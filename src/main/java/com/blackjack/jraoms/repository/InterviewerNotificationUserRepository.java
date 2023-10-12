package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.InterviewerNotification;
import com.blackjack.jraoms.entity.InterviewerNotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterviewerNotificationUserRepository extends JpaRepository<InterviewerNotificationUser,Integer> {


}
