package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping("/departmentList/{id}")
    @ResponseBody
    public List<Department> departmentList(@PathVariable("id") int id){
        return departmentRepository.findByCompanyId(id);
    }
}