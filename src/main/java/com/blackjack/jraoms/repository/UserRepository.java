package com.blackjack.jraoms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackjack.jraoms.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);
    public List<User> findByDepartmentId(int id);
}
