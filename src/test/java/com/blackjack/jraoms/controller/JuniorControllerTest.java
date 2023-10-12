package com.blackjack.jraoms.controller;
import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.VacancyDto;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.service.CompanyService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyHistoryService;
import com.blackjack.jraoms.service.VacancyService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JuniorControllerTest {
    @Mock private VacancyService vacancyService;
    @Mock private CompanyService companyService;
    @Mock private UserService userService;
    @Mock ModelMap modelMap;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private VacancyHistoryService vacancyHistoryService;
    @Mock private Authentication authentication;
    @Mock private HttpSession session;
    @InjectMocks private JuniorController juniorController;

    @Test
    void toAddVacancyTest() {
        Company companyOne = new Company();
        Company companyTwo = new Company();
        List<Company> companyList = Arrays.asList(companyOne, companyTwo);
        when(companyService.findAll()).thenReturn(companyList);

        ModelAndView modelAndView = juniorController.toAddVacancy(modelMap);

        verify(companyService).findAll();
        verify(modelMap).addAttribute("companyList", companyList);

        assertEquals("/junior/add-jobs", modelAndView.getViewName());
    }

    @Test
    public void testKillSession() {
        when(session.getAttribute("success")).thenReturn("someValue");

        String result = juniorController.killSession(session);

        verify(session).removeAttribute("success");

        assertEquals("/junior/add-jobs", result);
    }

    @Test
    public void testViewDetails() {
        int vacancyId = 1;
        Vacancy vacancy = new Vacancy();
        when(vacancyService.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        List<VacancyHistory> history = new ArrayList<>();
        VacancyHistory vacancyHistory = new VacancyHistory();
        history.add(vacancyHistory);
        when(vacancyHistoryService.findByVacancyId(vacancyId)).thenReturn(history);

        ModelMap modelMap = new ModelMap();

        ModelAndView modelAndView = juniorController.viewDetails(String.valueOf(vacancyId), modelMap);

        verify(vacancyService).findById(vacancyId);
        verify(vacancyHistoryService).findByVacancyId(vacancyId);

        assertEquals("adminVacancyDetails", modelAndView.getViewName());
        assertEquals(vacancy, modelAndView.getModel().get("vacancy"));
        assertEquals(vacancyHistory, modelMap.get("history"));
    }

    @Test
    public void testUserProfile() {
        int userId = 1;
        Department mockDepartment = mock(Department.class);
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setEnable(true);
        user.setRegisterDate(LocalDate.now());
        user.setProfilePicture("profile.jpg");
        user.setRole(Role.ADMIN);
        user.setDepartment(mockDepartment);
        MyUserDetails userDetails = new MyUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userService.findUserById(userId)).thenReturn(user);

        ModelAndView modelAndView = juniorController.userProfile(authentication);

        verify(authentication).getPrincipal();
        verify(userService).findUserById(userId);

        assertEquals("users-profile", modelAndView.getViewName());
        assertEquals(user, modelAndView.getModel().get("user"));
    }

    @Test
    public void testUpdateAvatar() {
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);

        String newImage = "newImage.jpg";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(1)).thenReturn(user);

        String result = juniorController.updateAvatar(newImage, session, authentication);

        verify(userService).saveEntity(user);
        verify(session).setAttribute(eq("updateMailMessage"), eq("successAvatar"));

        assertEquals("redirect:/userprofile", result);
    }

    @Test
    public void testSendOtp() {
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);

        String email = "test@example.com";
        String code = "123456";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(1)).thenReturn(user);
        when(userService.sendVerificationCodeToEmail(email)).thenReturn(code);

        String result = juniorController.sendOtp(email, session, modelMap, authentication);

        verify(modelMap).addAttribute("user", user);
        verify(session).setAttribute(email, code);

        assertEquals("{\"status\": \"okey\"}", result);
    }

    @Test
    public void testUpdateEmail() {
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);

        user.setEmail("test@example.com");

        String otp = "123456";
        String realOtp = "123456";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(1)).thenReturn(user);
        when(session.getAttribute("test@example.com")).thenReturn(realOtp);

        String result = juniorController.updateEmail(user, otp, session, modelMap, authentication);

        verify(session).setAttribute("updateMailMessage", "success");

        assertEquals("redirect:/userprofile", result);
    }

    @Test
    public void testKillMailMessage() {
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(1)).thenReturn(user);

        String result = juniorController.killMailMessage(session, modelMap, authentication);

        verify(session).removeAttribute("updateMailMessage");

        verify(userService).saveEntity(user);

        verify(modelMap).addAttribute("user", user);

        assertEquals("users-profile", result);
    }

    @Test
    public void testChangePassword() {
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(1)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        String result = juniorController.changePassword(oldPassword, newPassword, session, authentication);

        verify(session).setAttribute("updateMailMessage", "pwSuccess");
        verify(userService).saveEntity(user);

        assertEquals("redirect:/userprofile", result);
    }

    @Test
    public void testEditVacancy() {
        String id = "123";
        VacancyDto vacancyDto = new VacancyDto();
        when(companyService.findAll()).thenReturn(new ArrayList<>());
        when(vacancyService.findVacancyDtoById(Integer.valueOf(id))).thenReturn(vacancyDto);

        ModelMap modelMap = new ModelMap();

        ModelAndView modelAndView = juniorController.editVacancy(id, modelMap);

        verify(companyService).findAll();
        verify(vacancyService).findVacancyDtoById(Integer.valueOf(id));

        assertEquals("editVacancy", modelAndView.getViewName());
        assertEquals(vacancyDto, modelAndView.getModel().get("vacancyBean"));
    }

    @Test
    public void testKillVacancyEditSession() {

        String result = juniorController.killVacancyEditSession(session);

        verify(session).removeAttribute("vacancyEditMessage");

        assertEquals("adminVacancyDetails", result);
    }

    @Test
    public void testCloseVacancy() {
        String id = "1";
        LocalDate date = LocalDate.now();
        Vacancy vacancy = new Vacancy();
        User user = new User();
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(vacancyService.findById(Integer.valueOf(id))).thenReturn(Optional.of(vacancy));

        String result = juniorController.closeVacancy(id, authentication, session);

        verify(vacancyService).saveEntity(vacancy);
        verify(vacancyHistoryService).save(any(VacancyHistory.class));
        verify(session).setAttribute("vacancyEditMessage", "closed");

        assertEquals("redirect:/vacancydetails/1", result);
    }

    @Test
    public void testReopenVacancy() {
        String id = "1";
        VacancyDto vacancyDto = new VacancyDto();
        when(companyService.findAll()).thenReturn(new ArrayList<>());
        when(vacancyService.findVacancyDtoById(Integer.valueOf(id))).thenReturn(vacancyDto);

        ModelMap modelMap = new ModelMap();

        ModelAndView modelAndView = juniorController.reopenVacancy(id, modelMap);

        verify(companyService).findAll();
        verify(vacancyService).findVacancyDtoById(Integer.valueOf(id));

        assertEquals("reopenVacancy", modelAndView.getViewName());
        assertEquals(vacancyDto, modelAndView.getModel().get("vacancyBean"));
    }

    @Test
    public void testReopenVacancyWithValidDto() {
        VacancyDto dto = new VacancyDto();
        dto.setVacancyId(1);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(new User()));
        when(userService.findUserById(anyInt())).thenReturn(new User());

        String result = juniorController.reopenVacancy(dto, bindingResult, session, authentication, "1");

        verify(session).setAttribute("vacancyEditMessage", "reopen");

        assertEquals("redirect:/vacancydetails/1", result);
    }

    @Test
    public void testViewVacancyHistory() {
        // Mock the necessary objects
        String id = "1";
        List<VacancyHistory> historyList = new ArrayList<>();
        historyList.add(new VacancyHistory());

        when(vacancyHistoryService.findByVacancyId(Integer.valueOf(id))).thenReturn(historyList);
        when(vacancyService.findById(Integer.valueOf(id))).thenReturn(Optional.of(new Vacancy()));

        ModelMap modelMap = new ModelMap();

        String result = juniorController.viewVacancyHistory(id, modelMap);

        verify(vacancyHistoryService).findByVacancyId(Integer.valueOf(id));
        verify(vacancyService).findById(Integer.valueOf(id));

        assertEquals(historyList, modelMap.get("historyList"));
        assertNotNull(modelMap.get("vacancy"));

        assertEquals("vacancyManageHistory", result);
    }

    @Test
    void testAddJob_Success() {
        VacancyDto dto = new VacancyDto();
        BindingResult bs = mock(BindingResult.class);
        User user = new User();
        user.setId(123);
        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        MockHttpSession session = new MockHttpSession();

        String viewName = juniorController.addJob(dto, bs, session, authentication);

        assertTrue(session.getAttribute("success") != null);
        assertEquals("redirect:/vacancy/post", viewName);
    }

    @Test
    void testEditVacancy_Success() {
        VacancyDto dto = new VacancyDto();
        BindingResult bs = mock(BindingResult.class);
        User user = new User();
        user.setId(1);
        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        MockHttpSession session = new MockHttpSession();
        String vacancyId = "1";

        // Perform the test
        String viewName = juniorController.editVacancy(dto, bs, session, authentication, vacancyId);

        // Verify the behavior
        assertTrue(session.getAttribute("vacancyEditMessage") != null);
        assertEquals("redirect:/vacancydetails/1", viewName);
    }

}