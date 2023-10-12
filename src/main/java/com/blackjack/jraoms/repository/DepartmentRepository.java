package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Integer> {
    public List<Department> findByCompanyId(int id);
    public Optional<Department> findByCompanyIdAndNameAndEnable(int id, String name,boolean enable);
    public Optional<Department> findByName(String name);
    public List<Department> findByEnable(boolean enable);
}
