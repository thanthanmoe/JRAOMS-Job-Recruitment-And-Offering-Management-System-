package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.*;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.CandidateRepository;
import com.blackjack.jraoms.repository.NotificationRepository;
import com.blackjack.jraoms.repository.VacancyRepository;
import com.blackjack.jraoms.service.*;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestAllControllerTest {
    @Mock private CandidateService candidateService;
    @Mock private VacancyRepository vacancyRepository;
    @Mock private CandidateRepository candidateRepository;
    @Mock private CompanyService companyService;
    @Mock private VacancyService vacancyService;
    @Mock private CandidateCVService candidateCVService;
    @Mock private MailService mailService;
    @Mock private InterviewHistoryService interviewHistoryService;
    @Mock private UserService userService;
    @Mock
    private JasperCompileManager jasperCompileManager;

    @Mock
    private JasperFillManager jasperFillManager;
    @InjectMocks private RestAllController restAllController;
    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private Authentication authentication;
    @Mock
    private HttpSession session;

    @Test
    void testSendOfferEmail_Success() throws Exception {
        String email = "example@example.com";
        String cc = "cc1@example.com,cc2@example.com";
        String subject = "Job Offer";
        String content = "Congratulations! You have a job offer.";
        MockMultipartFile file = new MockMultipartFile("file", "offer_letter.pdf", "application/pdf", "Offer letter content".getBytes());
        String salary = "50000";
        Integer candidateId = 1;
        LocalDate joinDate = LocalDate.now();
        Authentication authentication = mock(Authentication.class);

        Candidate candidate = new Candidate();
        candidate.setId(candidateId);
        when(candidateService.findCandidateById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Map<String, String>> responseEntity = restAllController.sendOfferEmail(
                email, cc, subject, content, file, salary, candidateId, joinDate, authentication);

        verify(mailService, times(1)).send(any()); // Verify that the email was sent
        verify(candidateService, times(1)).saveCandidate(any()); // Verify candidate updates

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertTrue(responseEntity.getBody().containsKey("message")),
                () -> assertEquals("Email sent successfully!", responseEntity.getBody().get("message"))
        );
    }

    @Test
    void testSeeMoreCandidate_Success() throws Exception {
        int id = 1;
        byte[] cvData = "PDF_CONTENT".getBytes();

        CandidateCV cv = new CandidateCV();
        cv.setData(cvData);
        when(candidateCVService.findCVById(id)).thenReturn(Optional.of(cv));

        ResponseEntity<String> responseEntity = restAllController.seeMoreCandidate(Integer.toString(id));

        String expectedDataUrl = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(cvData);
        String expectedHtmlResponse = "<embed src=\"" + expectedDataUrl + "\" type=\"application/pdf\" width=\"100%\" height=\"900px\" />";

        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals(expectedHtmlResponse, responseEntity.getBody())
        );
    }

    @Test
    void testSeeMoreCandidate_NotFound() throws Exception {
        int id = 1;
        when(candidateCVService.findCVById(id)).thenReturn(Optional.empty());
        ResponseEntity<String> responseEntity = restAllController.seeMoreCandidate(Integer.toString(id));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void testGetCandidate_Success() {
        DataTablesInput input = new DataTablesInput();
        DataTablesOutput<Candidate> expectedOutput = new DataTablesOutput<>();
        expectedOutput.setData(Collections.singletonList(new Candidate()));
        when(candidateService.getCandidate(input)).thenReturn(expectedOutput);
        DataTablesOutput<Candidate> result = restAllController.getCandidate(input);
        verify(candidateService, times(1)).getCandidate(input);
        assertEquals(expectedOutput, result);
    }

    @Test
    void testGetCompany_Success() {
        DataTablesInput input = new DataTablesInput();
        DataTablesOutput<Company> expectedOutput = new DataTablesOutput<>();
        expectedOutput.setData(Collections.singletonList(new Company()));
        when(companyService.getCompany(input)).thenReturn(expectedOutput);
        DataTablesOutput<Company> result = restAllController.getCompany(input);
        verify(companyService, times(1)).getCompany(input);
        assertEquals(expectedOutput, result);
    }

    @Test
    void testGetCandidateWithParameter_InvalidStatus() {
        DataTablesInput input = new DataTablesInput();
        String status = "invalid";
        String vacancyId = "1";

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1);
        vacancy.setPosition("Software Developer");
        when(vacancyService.findById(1)).thenReturn(Optional.of(vacancy));

        ModelMap model = new ModelMap();
        RestAllController.InvalidStatusException exception = assertThrows(RestAllController.InvalidStatusException.class,
                () -> restAllController.getCandidateWithparameter(input, status, vacancyId, model));
        assertEquals("Invalid status: invalid", exception.getMessage());
    }


    @Test
    void testTotalActiveVacancy_Success() {
        Vacancy activeVacancy = new Vacancy();
        when(vacancyRepository.findBystatus("active")).thenReturn(Collections.singletonList(activeVacancy));
        List<Vacancy> result = restAllController.totalActiveVacancy();
        verify(vacancyRepository, times(1)).findBystatus("active");
        assertEquals(Collections.singletonList(activeVacancy), result);
    }

    @Test
    void testTotalInactiveVacancy_Success() {
        List<String> statusList = Arrays.asList("Expired", "close");
        Vacancy expiredVacancy = new Vacancy();
        Vacancy closedVacancy = new Vacancy();
        when(vacancyRepository.findByStatusIn(statusList)).thenReturn(Arrays.asList(expiredVacancy, closedVacancy));
        List<Vacancy> result = restAllController.totalInactiveVacancy();
        verify(vacancyRepository, times(1)).findByStatusIn(statusList);
        assertEquals(Arrays.asList(expiredVacancy, closedVacancy), result);
    }

    @Test
    void testTotalCandidate_Success() {
        Candidate candidate1 = new Candidate();
        Candidate candidate2 = new Candidate();
        when(candidateRepository.findAll()).thenReturn(Arrays.asList(candidate1, candidate2));
        List<Candidate> result = restAllController.totalCandidate();
        verify(candidateRepository, times(1)).findAll();
        assertEquals(Arrays.asList(candidate1, candidate2), result);
    }
    @Test
    public void testDownloadReportPdf() throws Exception {
        // Arrange
        Date startDate = new Date(); // put your desired start date
        Date endDate = new Date();   // put your desired end date

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesAndVacancies(any(Date.class), any(Date.class)))
                .thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReport("pdf", startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // other assertions
    }
    @Test
    public void testDownloadReportExcel() throws Exception {
        // Arrange
        Date startDate = new Date(); // put your desired start date
        Date endDate = new Date();   // put your desired end date

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesAndVacancies(any(Date.class), any(Date.class)))
                .thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReport("xlsx", startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // other assertions
    }
    @Test
    public void testDownloadReportByStatus_NotInterviewPdf() throws Exception {
        // Arrange
        String status = "NOTINTERVIEW";
        String format = "pdf";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportByStatus_NotInterviewExcel() throws Exception {
        // Arrange
        String status = "NOTINTERVIEW";
        String format = "xlsx";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportByStatus_All_PDF() throws Exception {
        testDownloadReportByStatusWithFormatAndCandidates("All", "pdf", true);
    }

    @Test
    public void testDownloadReportByStatus_All_XLSX() throws Exception {
        testDownloadReportByStatusWithFormatAndCandidates("All", "xlsx", true);
    }

    @Test
    public void testDownloadReportByStatus_All_NoCandidates() throws Exception {
        testDownloadReportByStatusWithFormatAndCandidates("All", "pdf", false);
    }


    private void testDownloadReportByStatusWithFormatAndCandidates(String status, String format, boolean hasCandidates) throws Exception {
        // Arrange
        List<CandidateReportDto> dummyCandidates = hasCandidates ? Arrays.asList(new CandidateReportDto(), new CandidateReportDto()) : new ArrayList<>();
        when(candidateService.getAllCandidatesAndVacancies()).thenReturn(dummyCandidates);

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        // Assert
        if (hasCandidates) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } else {
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
        // You can add more detailed assertions depending on the scenario.
    }
    @Test
    public void testDownloadReportPendingInterviewPdf() throws Exception {
        // Arrange
        String status = "PENDING";
        String format = "pdf";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportPendingExcel() throws Exception {
        // Arrange
        String status = "PENDING";
        String format = "xlsx";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportPassedInterviewPdf() throws Exception {
        // Arrange
        String status = "PASSED";
        String format = "pdf";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportPassedExcel() throws Exception {
        // Arrange
        String status = "PASSED";
        String format = "xlsx";

        List<CandidateReportDto> dummyCandidates = Arrays.asList(new CandidateReportDto(), new CandidateReportDto()); // Dummy data
        when(candidateService.fetchCandidatesByStatus(status)).thenReturn(dummyCandidates);

        // Mock other components like the JasperCompileManager, etc. For simplicity, I've omitted it here.

        // Act
        ResponseEntity<?> response = restAllController.downloadReportByStatus(format, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
    @Test
    public void testDownloadReportForVacancy_noCandidates() throws Exception {
        when(candidateService.fetchCandidatesByVacancyId(anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = restAllController.downloadReportForVacancy("pdf", "1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No candidates found for the given status.", response.getBody());
    }

    @Test
    public void testDownloadReportForVacancy_pdfFormat() throws Exception {
        List<CandidateReportDto> mockList = new ArrayList<>();
        mockList.add(new CandidateReportDto()); // Add mock data to this list

        when(candidateService.fetchCandidatesByVacancyId(anyInt())).thenReturn(mockList);

        ResponseEntity<?> response = restAllController.downloadReportForVacancy("pdf", "1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Content-Disposition"));
    }

    @Test
    public void testDownloadReportForVacancy_xlsxFormat() throws Exception {
        List<CandidateReportDto> mockList = new ArrayList<>();
        mockList.add(new CandidateReportDto()); // Add mock data to this list

        when(candidateService.fetchCandidatesByVacancyId(anyInt())).thenReturn(mockList);

        ResponseEntity<?> response = restAllController.downloadReportForVacancy("xlsx", "1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Content-Disposition"));
    }

    @Test
    public void testDownloadReportForVacancy_unsupportedFormat() throws Exception {
        List<CandidateReportDto> mockList = new ArrayList<>();
        mockList.add(new CandidateReportDto()); // Add mock data to this list

        when(candidateService.fetchCandidatesByVacancyId(anyInt())).thenReturn(mockList);

        ResponseEntity<?> response = restAllController.downloadReportForVacancy("unsupportedFormat", "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unsupported report format.", response.getBody());
    }
    @Test
    public void testSaveNotification() {
        NotificationDto notificationDto = new NotificationDto();
        Notification notification = new Notification();

        when(notificationService.saveNotification(notificationDto)).thenReturn(notification);

        Notification result = restAllController.saveNotification(notificationDto);

        assertEquals(notification, result);
    }

    @Test
    public void testSaveNotificationUser() {
        Department department = new Department(); // or mock this if necessary

        User user = User.builder()
                .id(1)
                .name("Than Than Moe")
                .email("moelaybtu@gmail.com")
                .password("123456")
                .role(Role.SENIOR)
                .department(department)
                .registerDate(LocalDate.now())
                .profilePicture("defaultUser.png")
                .enable(true)
                .build();

        MyUserDetails mockUserDetails = new MyUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        NotificationContentDto notificationContentDto = new NotificationContentDto();
        List<NotificationUser> notificationUsers = new ArrayList<>();

        when(notificationService.saveNotificationUser(notificationContentDto.getNotifications(), 1)).thenReturn(notificationUsers);

        List<NotificationUser> result = restAllController.saveNotificationUser(notificationContentDto, authentication);

        assertEquals(notificationUsers, result);
    }

    @Test
    public void testClearAllNotification() {
        Department department = new Department(); // or mock this if necessary

        User user = User.builder()
                .id(1)
                .name("Than Than Moe")
                .email("moelaybtu@gmail.com")
                .password("123456")
                .role(Role.ADMIN)
                .department(department)
                .registerDate(LocalDate.now())
                .profilePicture("defaultUser.png")
                .enable(true)
                .build();

        MyUserDetails mockUserDetails = new MyUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        List<NotificationContentDto> notificationContentDtoList = new ArrayList<>();
        List<NotificationUser> notificationUsers = new ArrayList<>();

        when(notificationService.clearAllNotification(notificationContentDtoList, 1)).thenReturn(notificationUsers);

        List<NotificationUser> result = restAllController.clearAllNotification(notificationContentDtoList, authentication);

        assertEquals(notificationUsers, result);
    }

    @Test
    public void testTotalNotification() {
        Department department = new Department(); // or mock this if necessary

        User user = User.builder()
                .id(1)
                .name("Than Than Moe")
                .email("moelaybtu@gmail.com")
                .password("123456")
                .role(Role.ADMIN)
                .department(department)
                .registerDate(LocalDate.now())
                .profilePicture("defaultUser.png")
                .enable(true)
                .build();

        MyUserDetails mockUserDetails = new MyUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        List<Notification> notifications = new ArrayList<>();

        when(notificationRepository.getNotificationsByUser(1)).thenReturn(notifications);

        List<Notification> result = restAllController.totalNotification(authentication);

        assertEquals(notifications, result);
    }

    @Test
    public void testNotificationContent() {
        Department department = new Department(); // or mock this if necessary

        User user = User.builder()
                .id(1)
                .name("Than Than Moe")
                .email("moelaybtu@gmail.com")
                .password("123456")
                .role(Role.ADMIN)
                .department(department)
                .registerDate(LocalDate.now())
                .profilePicture("defaultUser.png")
                .enable(true)
                .build();

        MyUserDetails mockUserDetails = new MyUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        List<NotificationContentDto> notificationContents = new ArrayList<>();

        when(notificationService.notificationContent(1)).thenReturn(notificationContents);

        List<NotificationContentDto> result = restAllController.notificationContent(authentication);

        assertEquals(notificationContents, result);
    }

}

