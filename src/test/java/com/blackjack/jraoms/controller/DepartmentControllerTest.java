package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {
    @Mock DepartmentRepository departmentRepository;

    @InjectMocks DepartmentController departmentController;

    @Test
    void testDepartmentList() {
        int companyId = 1;
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("Department One");
        department1.setCompany(new Company());

        Department department2 = new Department();
        department2.setId(2);
        department2.setName("Department Two");
        department2.setCompany(new Company());

        List<Department> mockDepartments = Arrays.asList(department1, department2);

        when(departmentRepository.findByCompanyId(companyId)).thenReturn(mockDepartments);

        List<Department> result = departmentController.departmentList(companyId);

        verify(departmentRepository, times(1)).findByCompanyId(companyId);

        assertEquals(mockDepartments, result);
    }
}