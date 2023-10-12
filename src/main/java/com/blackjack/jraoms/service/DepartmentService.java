package com.blackjack.jraoms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.repository.DepartmentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

	 public List<Department> findAll() {
	    	return departmentRepository.findAll();
	    }
}
