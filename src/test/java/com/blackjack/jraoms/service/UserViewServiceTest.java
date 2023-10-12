package com.blackjack.jraoms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;

import com.blackjack.jraoms.dto.UserDto;
import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.repository.UserRepository;
import com.blackjack.jraoms.service.UserViewService;

import ch.qos.logback.core.model.Model;

class UserViewServiceTest {
	@Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private UserViewService userViewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        // Create a sample user and its associated department and company
        Company company = new Company();
        company.setId(1);
        company.setName("TestCo");

        Department department = new Department();
        department.setId(1);
        department.setName("IT");
        department.setCompany(company);

        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setDepartment(department);
        // ... Set other fields as necessary

        // Mock userRepository's behavior
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        ModelMap modelMap = new ModelMap();

        // Execute the method
        String resultViewName = userViewService.getAllUsers(modelMap);

        // Assert the result
        assertEquals("/admin/manage-users", resultViewName);

        // Verify interactions with mock
        verify(userRepository, times(1)).findAll();

        // Assert that the model contains the expected attribute
        List<UserDto> returnedUserDtos = (List<UserDto>) modelMap.get("users");
        assertEquals(1, returnedUserDtos.size());
        assertEquals(user.getName(), returnedUserDtos.get(0).getName());
    }
}
