package com.blackjack.jraoms.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.blackjack.jraoms.dto.EmailContactDto;
import com.blackjack.jraoms.dto.FormDto;
import com.blackjack.jraoms.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.CandidateReportDto;
import com.blackjack.jraoms.dto.ChartsDto;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.entity.CvStatus;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.InterviewHistory;
import com.blackjack.jraoms.entity.InterviewProcess;
import com.blackjack.jraoms.entity.Role;
import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SeniorControllerTest {

    @Mock
    private CandidateCVService candidateCVService;
    @Mock
    private CandidateService candidateService;
    @Mock
    private UserService userService;
    @Mock
    MailService mailService;

    @Mock private HttpServletResponse response;
    @Mock private Authentication authentication;
    @Mock private InterviewHistoryService interviewHistoryService;
    @Mock private StatusHistoryService statusHistoryService;
    @Mock private VacancyService vacancyService;
    @Mock private VacancyHistoryService vacancyHistoryService;
    @Mock private InterviewProcessService interviewProcessService;
    @InjectMocks private SeniorController seniorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCandidatePagination() {
        ModelMap modelMap = new ModelMap();

        String viewName = seniorController.candidatePagination();

        assertEquals("tables-data", viewName);
    }

    @Test
    public void testAllCandidate() {
        List<Candidate> mockCandidates = Arrays.asList(new Candidate(), new Candidate());
        when(candidateService.findAllCandidate()).thenReturn(mockCandidates);

        ModelMap modelMap = new ModelMap();
        String viewName = seniorController.allCandidate(modelMap);

        assertEquals("tables-data", viewName);
        assertEquals(mockCandidates, modelMap.get("candidates"));
    }
    @Test
    public void testFindByCVStatus() throws Exception {
    	 String testCvStatus = "PASSED";
    	    ModelMap modelMap = new ModelMap();
    	 List<Candidate> expectedCandidates = Arrays.asList(new Candidate(), new Candidate());
         when(candidateService.findByCVStatus(testCvStatus)).thenReturn(expectedCandidates);
 
        String viewName = seniorController.findByCVStatus(modelMap,testCvStatus);

        assertEquals("tables-data", viewName);
    
        verify(candidateService, times(1)).findByCVStatus("PASSED");
    }
    @Test
    public void testDownloadAllCV() throws Exception {
        // Prepare test data
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	  String[] checkedId= {"1", "2"};
        int[] checkedIds = {1, 2}; // Sample checked IDs
        byte[] cvData1 = "Test CV Data".getBytes();
        byte[] cvData = "Test CV Data".getBytes();
        CandidateCV cv1 = new CandidateCV("cv.doc", "application/msword", cvData1);
        CandidateCV cv2 = new CandidateCV("cv2.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", cvData);
        Candidate candidate1 = new Candidate();
        candidate1.setId(1);
        candidate1.setName("John Doe");
        candidate1.setDob("1990-01-01"); 
        candidate1.setGender("Male");
        candidate1.setPhone("123-456-7890");
        candidate1.setEmail("johndoe@gmail.com");
        candidate1.setDegree("Bachelor's in Computer Science");
        candidate1.setTechnical("Java, Spring Boot");
        candidate1.setLanguage("English, Spanish");
        candidate1.setLevel("Intermediate");
        candidate1.setMainTechnical("Java");
        candidate1.setExperiences("5 years");
        candidate1.setSalary("50000");
        candidate1.setDate(LocalDate.now());
        candidate1.setCvStatus(CvStatus.VIEW);
        candidate1.setCandidateCV(cv1);
        
        Candidate candidate2 = new Candidate();
        candidate2.setId(1);
        candidate2.setName("John Doe");
        candidate2.setDob("1990-01-01"); 
        candidate2.setGender("Male");
        candidate2.setPhone("123-456-7890");
        candidate2.setEmail("johndoe@gmail.com");
        candidate2.setDegree("Bachelor's in Computer Science");
        candidate2.setTechnical("Java, Spring Boot");
        candidate2.setLanguage("English, Spanish");
        candidate2.setLevel("Intermediate");
        candidate2.setMainTechnical("Java");
        candidate2.setExperiences("5 years");
        candidate2.setSalary("50000");
        candidate2.setDate(LocalDate.now());
        candidate2.setCvStatus(CvStatus.VIEW);
        candidate1.setCandidateCV(cv1);
Department department = new Department(); // or mock this if necessary
        
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

       MyUserDetails userDetails = new MyUserDetails(user); // Assuming MyUserDetails has a setter for userId
      

        when(candidateCVService.findCVById(1)).thenReturn(Optional.of(cv1));
        when(candidateCVService.findCVById(2)).thenReturn(Optional.of(cv2));
        when(candidateService.findCandidateById(1)).thenReturn(Optional.of(candidate1));
        when(candidateService.findCandidateById(2)).thenReturn(Optional.of(candidate2));
        when(userService.findUserById(any())).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(new MyUserDetails(user), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Call the method
        seniorController.downloadCVs(checkedId, response, authentication);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("application/zip", response.getContentType());
        assertEquals("attachment;filename=candidate.zip", response.getHeader("Content-Disposition"));

        byte[] zipBytes = response.getContentAsByteArray();
        List<String> extractedFileNames = new ArrayList<>();

        // Extract filenames from the ZIP archive in the response.
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                extractedFileNames.add(zipEntry.getName());
                zipInputStream.closeEntry();
            }
        } catch (ZipException e) {
            fail("Response content is not a valid ZIP archive");
        }

        // Check if the expected files are present in the ZIP archive.
        assertEquals(2, extractedFileNames.size(), "The ZIP should contain 2 files");
     
        extractedFileNames.forEach(System.out::println);
    }

    @Test
    public void testDownloadReportInterviewProcessPdfFormat() throws Exception {
        testDownloadReportInterviewProcessFormat("pdf", "application/pdf", "attachment; filename=InterviewProcess.pdf");
    }

    @Test
    public void testDownloadReportInterviewProcessXlsxFormat() throws Exception {
        testDownloadReportInterviewProcessFormat("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "attachment; filename=InterviewProcess.xlsx");
    }

    private void testDownloadReportInterviewProcessFormat(String format, String expectedContentType, String expectedContentDisposition) throws Exception {
        // Arrange
        List<InterviewProcess> mockList = new ArrayList<>();
        when(interviewProcessService.getAllData()).thenReturn(mockList);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        // Act
        seniorController.downloadReportInterviewProcess(format, mockResponse);

        // Assert
        assertEquals(expectedContentType, mockResponse.getContentType());
        assertEquals(expectedContentDisposition, mockResponse.getHeader("Content-Disposition"));
    }
    @Test
    public void testCandidateVacancyChart() {
        // Arrange
        List<ChartsDto> mockCharts = new ArrayList<>();
        when(candidateService.fetchCountByVacancy_id()).thenReturn(mockCharts);

        ModelMap model = new ModelMap();

        // Act
        String viewName = seniorController.candidateVacancyChart(model);

        // Assert
        assertEquals("vacancy-candidate-charts", viewName);
        assertEquals(mockCharts, model.get("chartData"));
    }

    @Test
    public void testVacancyChart() {
        // Arrange
        List<Object[]> mockCharts = new ArrayList<>();
        when(vacancyHistoryService.getMonthlyCreateActions()).thenReturn(mockCharts);

        ModelMap model = new ModelMap();

        // Act
        String viewName = seniorController.vacancyChart(model);

        // Assert
        assertEquals("vacancy-charts", viewName);
        assertEquals(mockCharts, model.get("chartData"));
    }

    @Test
    void testUpdateCandidate_Success() {
        String id = "1";
        String vacancyId = "2";

        Candidate candidate = new Candidate();
        candidate.setId(1);

        User user = new User();
        user.setId(1);

        ModelMap model = new ModelMap();
        MockHttpSession session = new MockHttpSession();

        when(userService.findUserById(1)).thenReturn(user);
        when(candidateService.findCandidateById(1)).thenReturn(Optional.of(candidate));
        when(vacancyService.findById(Integer.parseInt(vacancyId))).thenReturn(Optional.of(new Vacancy()));

        // Perform the test
        String viewName = seniorController.updateCandidate(model, id, vacancyId, session);

        // Verify the behavior
        verify(candidateService, times(1)).candidate(candidate);
        verify(statusHistoryService, times(1)).saveHistory(any(StatusHistory.class));
        assertEquals("candidateStatusChange", viewName);
        assertEquals(vacancyId, model.get("vacancyId"));
        assertTrue(model.containsKey("vacancyName"));
    }

    @Test
    void testActionChangeHistory_Success() {
        CvStatus status = CvStatus.CONSIDERING;
        String id = "1";
        String vacancyId = "2";

        Candidate candidate = new Candidate();
        candidate.setId(1);

        User user = new User();
        user.setId(1);

        ModelMap model = new ModelMap();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));

        Optional<Vacancy> vacancy = Optional.of(new Vacancy());

        when(userService.findUserById(1)).thenReturn(user);
        when(candidateService.findCandidateById(1)).thenReturn(Optional.of(candidate));
        when(vacancyService.findById(Integer.parseInt(vacancyId))).thenReturn(vacancy);

        // Perform the test
        String viewName = seniorController.actionChangeHistory(status, id, vacancyId, model, authentication);

        // Verify the behavior
        verify(candidateService, times(1)).saveCandidate(candidate);
        verify(statusHistoryService, times(1)).saveHistory(any(StatusHistory.class));
        assertEquals("candidateStatusChange", viewName);
        assertEquals(vacancyId, model.get("vacancyId"));
        assertTrue(model.containsKey("vacancyName"));
    }

    @Test
    void testSendMail_Success() {
        FormDto formDto = new FormDto();
        // Set necessary properties of formDto

        // Perform the test
        ResponseEntity<?> responseEntity = seniorController.sendMail(formDto);

        // Verify the behavior
        verify(mailService, times(1)).send(any(EmailContactDto.class));
        assertEquals(ResponseEntity.ok().body("successfully"), responseEntity);
    }
    @Test
    public void testInterviewProcess() {
        // Arrange
        List<InterviewProcess> mockProcess = new ArrayList<>();
        when(interviewProcessService.getAllData()).thenReturn(mockProcess);

        ModelMap model = new ModelMap();

        // Act
        String viewName = seniorController.findProcess(model);

        // Assert
        assertEquals("interview-process", viewName);
        assertEquals(mockProcess, model.get("interviewList"));
    }
}