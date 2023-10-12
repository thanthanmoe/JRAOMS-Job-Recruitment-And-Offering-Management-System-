package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.service.DepartmentService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        // Prepare mock data
        Department department1 = new Department();
        department1.setName("HR");
        
        Department department2 = new Department();
        department2.setName("Finance");

        List<Department> expectedDepartments = Arrays.asList(department1, department2);

        // Mocking the repository call
        when(departmentRepository.findAll()).thenReturn(expectedDepartments);

        // Call the service method
        List<Department> actualDepartments = departmentService.findAll();

        // Assert the results and verify the interactions
        assertEquals(expectedDepartments, actualDepartments);
        verify(departmentRepository, times(1)).findAll();
    }

}
