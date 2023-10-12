package com.blackjack.jraoms.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.controller.CandidateController;
import com.blackjack.jraoms.controller.InterviewController;
import com.blackjack.jraoms.controller.SeniorController;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.entity.CvStatus;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.InterviewHistory;
import com.blackjack.jraoms.entity.Role;
import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.service.CandidateCVService;
import com.blackjack.jraoms.service.CandidateService;
import com.blackjack.jraoms.service.StatusHistoryService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class InterviewControllerTest {
	@Autowired CandidateController candidateController;
    @InjectMocks private InterviewController interviewController;
    @Autowired private MockMvc mockMvc;
    @Mock private CandidateService candidateService;
    @Mock private VacancyService vacancyService;
    @MockBean private CandidateCVService candidateCvService;
    @Mock private StatusHistoryService statusHistoryService;
    @Mock private UserService userService;
    @Mock private Authentication authentication;
    @Test
    public void testInterviewerView() throws Exception {
        // Setup
        String vacancyId = "1";
        Vacancy mockVacancy = new Vacancy();
        mockVacancy.setPosition("Software Engineer");

        when(vacancyService.findById(Integer.parseInt(vacancyId))).thenReturn(Optional.of(mockVacancy));

        ModelMap modelMap = new ModelMap();
        String viewName = interviewController.interviewerView(vacancyId, modelMap);

        assertEquals("interviewer-view-candidate", viewName);

        verify(vacancyService, times(1)).findById(Integer.parseInt(vacancyId));
    }

   
    @Test
    public void testSeeMoreCandidateForInterviewer() {
        String id = "1";

        Candidate candidate = new Candidate();
        Vacancy vacancy = new Vacancy();
        candidate.setVacancy(vacancy);
  

        CandidateCV cv = new CandidateCV();

        when(candidateService.findCandidateById(Integer.parseInt(id))).thenReturn(Optional.of(candidate));
        when(candidateCvService.findCVById(Integer.parseInt(id))).thenReturn(Optional.of(cv));

        ModelMap modelMap = new ModelMap();
        String viewName = interviewController.seeMoreCandidateForInterviewer(modelMap, id);

        assertEquals("candidateSeeMore", viewName);
        assertEquals(candidate, modelMap.get("candidates"));
        assertEquals(vacancy, modelMap.get("vacancy"));
   
    }
    @Test
    public void testActionChangeHistory() {
        CvStatus status = CvStatus.PASSED;
        String id = "1";
        String vacancyId = "1";
Department department = new Department();
        
        User user = User.builder()
        		.name("Than Than Moe")
				.email("moelaybtu@gmail.com")
				.password("123456")
				.role(Role.ADMIN)
				.department(department)
				.registerDate(LocalDate.now())
				.profilePicture("defaultUser.png")
				.enable(true)
				.build();

       MyUserDetails userDetails = new MyUserDetails(user);

        Candidate candidate = new Candidate();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findUserById(anyInt())).thenReturn(user);
        when(candidateService.findCandidateById(Integer.parseInt(id))).thenReturn(Optional.of(candidate));

        String viewName = interviewController.actionChange(status, id, vacancyId, new ModelMap(), authentication);

        assertEquals("interviewer-view-candidate", viewName);
        verify(candidateService, times(1)).saveCandidate(candidate);
        verify(statusHistoryService, times(1)).saveHistory(any());
    }

    @Test
    void testCancel_NotInterviewStatus() {
        CvStatus status = CvStatus.NOTINTERVIEW;
        String id = "1";
        String vacancyId = "2";

        Candidate candidate = new Candidate();
        candidate.setId(1);

        User user = new User();
        user.setId(123);

        MyUserDetails userDetails = new MyUserDetails(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(candidateService.findCandidateById(1)).thenReturn(Optional.of(candidate));

        String viewName = interviewController.cancel(status, id, vacancyId, new ModelMap(), authentication);

        verify(candidateService, times(1)).findCandidateById(1);
        verify(candidateService, times(1)).saveCandidate(candidate);
        verify(statusHistoryService, times(1)).saveHistory(any(StatusHistory.class));
        assertEquals("interviewer-view-candidate", viewName);
    }
}
