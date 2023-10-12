package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationUserRepository extends JpaRepository<NotificationUser,Integer> {
}
