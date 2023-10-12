package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.dto.AddCompanyDto;
import com.blackjack.jraoms.dto.UserRegistrationDto;
import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.Role;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.exception.EmailAlreadyExistsException;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.service.CompanyService;
import com.blackjack.jraoms.service.DepartmentService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.UserViewService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private CompanyService companyService;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private UserService userService;
    @Mock private UserViewService userViewService;
    @InjectMocks private AdminController adminController;
    @Mock private Model model;
    @Mock private ModelAndView modelAndView;
    @Mock private ModelMap modelMap;
    @Mock private HttpSession session;

   
    @Test
    public void testUpdateCompany_WithDuplicateError() {
        String companyId = "1";
        Company mockCompany = new Company();

        when(companyService.updateCompany(eq(mockCompany), anyString())).thenReturn(true);

        String result = adminController.updateCompany(companyId, mockCompany,"newDepartment", session);

        verify(session).setAttribute("companyMessage", "duplicate");
        assertEquals("redirect:/system/updatecompany/" + companyId, result);
    }

    @Test
    public void testUpdateCompany_WithSuccess() {
        String companyId = "1";
        Company mockCompany = new Company();

        when(companyService.updateCompany(eq(mockCompany), anyString())).thenReturn(false);

        String result = adminController.updateCompany(companyId, mockCompany, "newDepartment", session);

        verify(session).setAttribute("companyMessage", "successUpdate");
        assertEquals("redirect:/system/companydetails/" + companyId, result);
    }


    @Test
    public void testCompanyDetails() {
        // Arrange
        String id = "1";
        Company company = new Company();
        company.setId(1);
        company.setName("Test Company");

        when(companyService.findById(1)).thenReturn(Optional.of(company));

        List<Department> departmentList = new ArrayList<>();
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("HR");
        department1.setEnable(true);
        departmentList.add(department1);

        when(departmentRepository.findByEnable(true)).thenReturn(departmentList);

        ModelMap model = new ModelMap();

        // Act
        String viewName = adminController.companyDetails(id, model);

        // Assert
        assertThat(viewName).isEqualTo("/admin/company-details");

        Company modelCompany = (Company) model.get("company");
        assertThat(modelCompany).isNotNull();
        assertThat(modelCompany.getId()).isEqualTo(1);
        assertThat(modelCompany.getName()).isEqualTo("Test Company");

        List<Department> modelDepartments = modelCompany.getDepartments();
        assertThat(modelDepartments).isNotNull();
        assertThat(modelDepartments.size()).isEqualTo(1);
        Department modelDepartment = modelDepartments.get(0);
        assertThat(modelDepartment.getId()).isEqualTo(1);
        assertThat(modelDepartment.getName()).isEqualTo("HR");
        assertThat(modelDepartment.isEnable()).isEqualTo(true);

        verify(companyService).findById(1);
        verify(departmentRepository).findByEnable(true);
    }

    @Test
    public void testUserRegisterForm() {
        String viewName = adminController.userRegisterForm(model);

        verify(model).addAttribute(eq("userRegistrationDto"), any(UserRegistrationDto.class));
        assertEquals("/admin/user_register", viewName);
    }

    @Test
    public void testUserRegistration_Success() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();

        adminController.userRegistration(userRegistrationDto, session);

        verify(userService).userRegistration(userRegistrationDto);
        verify(session).setAttribute("message", "registerSuccess");
        verify(session, never()).setAttribute(eq("message"), eq("registerExists"));
        verify(session, never()).setAttribute(eq("message"), eq("registerError"));
    }

    @Test
    public void testUserRegistration_EmailExists() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();

        doThrow(new EmailAlreadyExistsException("error")).when(userService).userRegistration(userRegistrationDto);

        adminController.userRegistration(userRegistrationDto, session);

        verify(userService).userRegistration(userRegistrationDto);
        verify(session, never()).setAttribute(eq("message"), eq("registerSuccess"));
        verify(session).setAttribute("message", "registerExists");
        verify(session, never()).setAttribute(eq("message"), eq("registerError"));
    }

    @Test
    public void testShowAllUsers() {
        String expectedViewName = "expectedViewName";

        when(userViewService.getAllUsers(modelMap)).thenReturn(expectedViewName);

        String actualViewName = adminController.showAllUsers(modelMap);

        verify(userViewService).getAllUsers(modelMap);
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testEditInfo() {
        Department department = new Department();
        int userId = 1;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("TestUser");
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.JUNIOR);
        mockUser.setEnable(true);
        mockUser.setDepartment(department);

        when(userService.findUserById(userId)).thenReturn(mockUser);

        ModelMap modelMap = new ModelMap();

        ModelAndView modelAndView = adminController.editInfo(String.valueOf(userId), modelMap);

        verify(userService).findUserById(userId);

        UserRegistrationDto expectedDto = new UserRegistrationDto();
        expectedDto.setId(mockUser.getId());
        expectedDto.setName(mockUser.getName());
        expectedDto.setEmail(mockUser.getEmail());
        expectedDto.setRole(mockUser.getRole().toString());
        expectedDto.setStatus(mockUser.isEnable() ? 1 : 0);

        assertEquals("/admin/user_update", modelAndView.getViewName());
        assertEquals(mockUser.getDepartment(), modelMap.get("department"));
    }

    @Test
    public void testUserEditInfo_Success() {
        String userId = "1";
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setId(Integer.valueOf(userId));

        when(userService.updateUser(userDto)).thenReturn(false);

        adminController.userEditInfo(userId, userDto, session);

        verify(userService).updateUser(userDto);
        verify(session).setAttribute("message", "userUpdateSuccess");
        verify(session, never()).setAttribute(eq("message"), eq("userUpdateError"));
    }

    @Test
    public void testUserEditInfo_Error() {
        String userId = "1";
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setId(Integer.valueOf(userId));

        when(userService.updateUser(userDto)).thenReturn(true);

        adminController.userEditInfo(userId, userDto, session);

        verify(userService).updateUser(userDto);
        verify(session, never()).setAttribute(eq("message"), eq("userUpdateSuccess"));
        verify(session).setAttribute("message", "userUpdateError");
    }

    @Test
    public void testToUpdateCompany() {
        // Arrange
        String id = "1";
        Company company = new Company();
        company.setId(1);
        company.setName("Test Company");

        when(companyService.findById(1)).thenReturn(Optional.of(company));

        List<Department> departmentList = new ArrayList<>();
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("HR");
        department1.setEnable(true);
        departmentList.add(department1);

        when(departmentRepository.findByEnable(true)).thenReturn(departmentList);

        // Act
        ModelAndView modelAndView = adminController.toUpdateCompany(id);

        // Assert
        assertThat(modelAndView.getViewName()).isEqualTo("/admin/company_update");

        Company updateCompanyDto = (Company) modelAndView.getModel().get("updateCompanyDto");
        assertThat(updateCompanyDto).isNotNull();
        assertThat(updateCompanyDto.getId()).isEqualTo(1);
        assertThat(updateCompanyDto.getName()).isEqualTo("Test Company");
        assertThat(updateCompanyDto.getDepartments()).isNotNull();
        assertThat(updateCompanyDto.getDepartments().size()).isEqualTo(1);

        verify(companyService).findById(1);
        verify(departmentRepository).findByEnable(true);
    }

    @Test
    void testKllAlertMessage_Success() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("companyMessage", "Some message");

        // Perform the test
        String result = adminController.kllAlertMessage(session);

        // Verify the behavior
        assertNull(session.getAttribute("companyMessage"));
        assertEquals("{\"status\": \"okey\"}", result);
    }
}