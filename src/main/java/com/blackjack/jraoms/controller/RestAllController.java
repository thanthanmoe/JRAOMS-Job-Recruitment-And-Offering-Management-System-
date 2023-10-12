package com.blackjack.jraoms.controller;

import java.time.LocalDate;
import java.util.*;
import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.*;
import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.*;
import com.blackjack.jraoms.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.entity.CvStatus;
import com.blackjack.jraoms.entity.InterviewHistory;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.service.CandidateCVService;
import com.blackjack.jraoms.service.CandidateService;
import com.blackjack.jraoms.service.MailService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyService;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class RestAllController {
	private final CandidateRepository candidateRepo;
	private final CandidateService candidateService;
	private final CompanyService companyService;
	private final CandidateCVService candidateCVService;
	private final UserService userService;
	private final InterviewHistoryService interviewHistoryService;
	private final MailService mailService;
	private final VacancyRepository vacancyRepository;
	private final VacancyService vacancyService;
	private final HttpSession session;
	private final NotificationService notificationService;
	private final NotificationRepository notificationRepository;
	private final InterviewerNotificationRepository interviewerNotificationRepository;
	private final OfferMailNotificationRepository offerMailNotificationRepository;
	private final StatusHistoryService statusHistoryService;
	private final DepartmentRepository departmentRepo;
	private final UserRepository userRepo;

	@PostMapping("/sendEmail")
	public ResponseEntity<Map<String, String>> sendEmail(
			@RequestParam("to") String email,
			@RequestParam("cc") String cc,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam("file") MultipartFile file,
			@RequestParam("interviewType") String interviewType,
			@RequestParam("interviewStage") String interviewStage,
			@RequestParam("candidate_id") Integer candidate_id,
			@RequestParam("date") LocalDate interviewDate,
			Authentication authentication) {

		String[] ccEmailList = cc.split(",");
		List<String> newCc = new ArrayList<>();
		for (String ccMail : ccEmailList) {
			if (!ccMail.isEmpty()) {
				newCc.add(ccMail);
			}
		}

		Map<String, String> response = new HashMap<>();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		EmailContactDto emailContactDtos = EmailContactDto.builder()
				.email(email).ccEmail(newCc)
				.content(content).subject(subject)
				.file(file).build();
		mailService.send(emailContactDtos);
		Candidate candidate = candidateService.findCandidateById(candidate_id).orElse(null);

		InterviewHistory interviewHistory = new InterviewHistory();
		interviewHistory.setAction(interviewStage);
		interviewHistory.setCandidate(candidate);
		interviewHistory.setDate(interviewDate);
		interviewHistory.setUser(user);
		interviewHistory.setInterviewFormat(interviewType);
		interviewHistoryService.saveHistory(interviewHistory);

		assert candidate != null;
		var interviewerNotificationDto = InterviewerNotificationDto.builder()
				.candidateId(candidate.getId())
				.interviewDate(interviewDate)
				.action(interviewStage)
				.interviewFormat(interviewType)
				.vacancyId(candidate.getVacancy().getId())
				.build();
		session.setAttribute("interviewerNotificationDto", interviewerNotificationDto);
		try {
			// Handle the file and email data as needed.
			// For instance, you can save the file and send the email using a service.

			// Assuming everything went well
			response.put("message", "Email sent successfully!");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.put("error", "Failed to send the email. Error: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping("/getInterviewerNotificationDtoSession")
	public ResponseEntity<Object> getInterviewHistorySession(HttpSession session) {
		Object interviewHistoryForNotification = session.getAttribute("interviewerNotificationDto");

		if (interviewHistoryForNotification != null) {
			return ResponseEntity.ok(interviewHistoryForNotification);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping("/killInterviewerNotificationDtoSession")
	public ResponseEntity<String> killInterviewHistorySession(HttpSession session) {
		session.removeAttribute("interviewerNotificationDto");
		return ResponseEntity.ok("killInterviewerNotificationDtoSession Successful !!");
	}

	@PostMapping("/sendOfferEmail")
	public ResponseEntity<Map<String, String>> sendOfferEmail(
			@RequestParam("to") String email,
			@RequestParam("cc-offer") String cc,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam("file") MultipartFile file,
			@RequestParam("salary") String salary,
			@RequestParam("offer_candidate_id") Integer candidate_id,
			@RequestParam("joinDate") LocalDate joinDate, Authentication authentication) {

		String[] ccEmailList = cc.split(",");
		List<String> newCc = new ArrayList<>();
		for (String ccMail : ccEmailList) {
			if (!ccMail.isEmpty()) {
				newCc.add(ccMail);
			}
		}

		Map<String, String> response = new HashMap<>();
		EmailContactDto emailContactDtos = EmailContactDto.builder()
				.email(email).ccEmail(newCc)
				.content(content).subject(subject)
				.file(file).build();
		mailService.send(emailContactDtos);
		Candidate candidate = candidateService.findCandidateById(candidate_id).orElse(null);
		candidate.setJoinDate(joinDate);
		candidate.setWages(salary);
		candidateService.saveCandidate(candidate);

		try {
			// Handle the file and email data as needed.
			// For instance, you can save the file and send the email using a service.

			// Assuming everything went well
			response.put("message", "Email sent successfully!");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.put("error", "Failed to send the email. Error: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping("/seeMore/{id}")
	public ResponseEntity<String> seeMoreCandidate(@PathVariable("id") String id) {

		try {
			int idInt = Integer.parseInt(id);
			CandidateCV cv = candidateCVService.findCVById(idInt).orElse(null);
			String htmlResponse = "";
			String base64EncodedData = Base64.getEncoder().encodeToString(cv.getData());
			String dataUrl = "data:application/pdf;base64," + base64EncodedData;

			htmlResponse += "<embed src=\"" + dataUrl + "\" type=\"application/pdf\" width=\"100%\" height=\"900px\" />";

			return ResponseEntity.ok(htmlResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/candidatePage")
	public DataTablesOutput<Candidate> getCandidate(@Valid DataTablesInput input) {
		return candidateService.getCandidate(input);
	}

	@GetMapping("/companyList")
	public DataTablesOutput<Company> getCompany(@Valid DataTablesInput input) {
		return companyService.getCompany(input);
	}

	@GetMapping("/candidatePage/{vacancyId}/{status}")
	public DataTablesOutput<Candidate> getCandidateWithparameter(@Valid DataTablesInput input,
																 @PathVariable("status") String status, @PathVariable("vacancyId") String vacancyId, ModelMap model) {
		CvStatus cvStatus;
		Vacancy vacancy = vacancyService.findById(Integer.parseInt(vacancyId)).orElse(null);
		model.addAttribute("vacancyName", vacancy.getPosition());

		try {
			cvStatus = CvStatus.valueOf(status.toUpperCase()); // Assuming the status in the URL matches the enum
			// constant names (e.g., "RECEIVE")
		} catch (IllegalArgumentException ex) {
			throw new InvalidStatusException("Invalid status: " + status); // Custom exception for handling invalid

		}

		Specification<Candidate> spec = new Specification<Candidate>() {
			@Override
			public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query,
										 CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(criteriaBuilder.equal(root.get("cvStatus"), cvStatus));
				predicates.add(criteriaBuilder.equal(root.get("vacancy").get("id"), Integer.parseInt(vacancyId)));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		Specification<Candidate> specs = new Specification<Candidate>() {
			@Override
			public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query,
										 CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(criteriaBuilder.equal(root.get("vacancy").get("id"), Integer.parseInt(vacancyId)));

				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}
		};
		return candidateRepo.findAll(input, spec);
	}


	// Custom exception class for invalid status
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	class InvalidStatusException extends RuntimeException {
		public InvalidStatusException(String message) {
			super(message);
		}
	}

	@GetMapping("/totalActiveVacancy")
	public List<Vacancy> totalActiveVacancy() {
		return vacancyRepository.findBystatus("active");
	}

	@GetMapping("/totalInactiveVacancy")
	public List<Vacancy> totalInactiveVacancy() {
		List<String> statusList = Arrays.asList("Expired", "close");
		return vacancyRepository.findByStatusIn(statusList);
	}

	@GetMapping("/totalCandidate")
	public List<Candidate> totalCandidate() {
		return candidateRepo.findAll();
	}

	@GetMapping("/senior/report/{format}/{startDate}/{endDate}")
	public ResponseEntity<?> downloadReport(@PathVariable("format") String format,
											@PathVariable("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
											@PathVariable("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate)
			throws JRException, IOException {

		List<CandidateReportDto> allCandidate = candidateService.fetchCandidatesAndVacancies(startDate, endDate);

		if (allCandidate == null || allCandidate.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
		}

		File file = ResourceUtils.getFile("classpath:all_Candidate.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidate);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "Candidates Summary List By Month");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		if (format.equalsIgnoreCase("pdf")) {
			JRPdfExporter exporterPdf = new JRPdfExporter();
			exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
			exporterPdf.exportReport();

			HttpHeaders headers = new HttpHeaders();

			headers.add("Content-Disposition", "attachment; filename=AllCandidateByMonth.pdf");
			return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
		} else if (format.equalsIgnoreCase("xlsx")) {
			JRXlsxExporter exporterXLS = new JRXlsxExporter();
			exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
			exporterXLS.exportReport();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=AllCandidateByMonth.xlsx");
			return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // In case of a bad request.
	}

	@GetMapping("/senior/reports/{format}/{status}")
	public ResponseEntity<?> downloadReportByStatus(
			@PathVariable("format") String format,
			@PathVariable("status") String status) throws JRException, IOException {

		if (status.equals("NOTINTERVIEW") || status.equals("VIEW") || status.equals("RECEIVE") || status.equals("CANCEL") || status.equals("CONSIDERING")) {
			List<CandidateReportDto> allCandidates = candidateService.fetchCandidatesByStatus(status);

			if (allCandidates == null || allCandidates.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
			}

			File file = ResourceUtils.getFile("classpath:all_Candidate.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", status + " Stage of Candidates");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			if (format.equalsIgnoreCase("pdf")) {
				JRPdfExporter exporterPdf = new JRPdfExporter();
				exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterPdf.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=StageOfCandidate.pdf");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else if (format.equalsIgnoreCase("xlsx")) {
				JRXlsxExporter exporterXLS = new JRXlsxExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterXLS.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=StageOfCandidate.xlsx");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
			}
		} else if (status.equals("All")) {
			List<CandidateReportDto> allCandidates = candidateService.getAllCandidatesAndVacancies();

			if (allCandidates == null || allCandidates.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
			}

			File file = ResourceUtils.getFile("classpath:all_Candidate.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("createdBy", "All Candidates Summary List");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			if (format.equalsIgnoreCase("pdf")) {
				JRPdfExporter exporterPdf = new JRPdfExporter();
				exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterPdf.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=AllCandidate.pdf");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else if (format.equalsIgnoreCase("xlsx")) {
				JRXlsxExporter exporterXLS = new JRXlsxExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterXLS.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=AllCandidate.xlsx");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
			}
		} else if (status.equals("PENDING")) {
			List<CandidateReportDto> allCandidates = candidateService.fetchCandidatesByStatus(status);

			if (allCandidates == null || allCandidates.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
			}

			File file = ResourceUtils.getFile("classpath:interview.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", "Pending  Candidates Summary List");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			if (format.equalsIgnoreCase("pdf")) {
				JRPdfExporter exporterPdf = new JRPdfExporter();
				exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterPdf.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=PendingStageOfCandidate.pdf");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else if (format.equalsIgnoreCase("xlsx")) {
				JRXlsxExporter exporterXLS = new JRXlsxExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterXLS.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=PendingStageOfCandidate.xlsx");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
			}
		} else if (status.equals("PASSED")) {
			List<CandidateReportDto> allCandidates = candidateService.fetchCandidatesByStatus(status);

			if (allCandidates == null || allCandidates.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
			}

			File file = ResourceUtils.getFile("classpath:interviewPassed.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", "Passed Candidates Summary List");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();

			if (format.equalsIgnoreCase("pdf")) {
				JRPdfExporter exporterPdf = new JRPdfExporter();
				exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterPdf.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=PassedStageOfCandidate.pdf");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else if (format.equalsIgnoreCase("xlsx")) {
				JRXlsxExporter exporterXLS = new JRXlsxExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
				exporterXLS.exportReport();

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=PassedStageOfCandidate.xlsx");

				return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
		}
	}

	@GetMapping("/senior/report/{format}/{vacancyId}")
	public ResponseEntity<?> downloadReportForVacancy(@PathVariable("format") String format,
													  @PathVariable("vacancyId") String vacancyId)
			throws JRException, IOException {

		List<CandidateReportDto> allCandidates = candidateService.fetchCandidatesByVacancyId(Integer.parseInt(vacancyId));
		if (allCandidates == null || allCandidates.isEmpty()) {
			System.out.println("null thi sis  null");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidates found for the given status.");
		}

		File file = ResourceUtils.getFile("classpath:candidate-inVacancy.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", allCandidates.get(0).getPosition() + " position of Candidates");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		if (format.equalsIgnoreCase("pdf")) {
			JRPdfExporter exporterPdf = new JRPdfExporter();
			exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
			exporterPdf.exportReport();

			HttpHeaders headers = new HttpHeaders();

			headers.add("Content-Disposition", "attachment; filename=CandidatesForVacancy.pdf");
			return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
		} else if (format.equalsIgnoreCase("xlsx")) {
			JRXlsxExporter exporterXLS = new JRXlsxExporter();
			exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(outStream));
			exporterXLS.exportReport();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=CandidatesForVacancy.xlsx");
			return new ResponseEntity<>(outStream.toByteArray(), headers, HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported report format.");
		}
	}

	@PostMapping("/jobs/sendWelcomeMail")
	public ResponseEntity<String> candidateSendMail(@RequestBody WelcomeMailDto welcomeMailDto) {
		try {
			var emailContentDto = EmailContactDto.builder()
					.email(welcomeMailDto.getCandidateEmail())
					.content(mailService.sendWelcomeMail(
							welcomeMailDto.getCandidateName(),
							welcomeMailDto.getVacancyName(),
							welcomeMailDto.getCompanyName()
					))
					.subject("Welcome")
					.file(null)
					.build();
			mailService.send(emailContentDto);
			return ResponseEntity.ok("Email sent successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid email address: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
		}
	}

	@PostMapping("/jobs/contact")
	public ResponseEntity<String> candidateContact(@RequestParam("candidateName") String name,
												   @RequestParam("email") String email,
												   @RequestParam("subject") String subject,
												   @RequestParam("content") String content) {
		try {
			var emailContentDto = EmailContactDto.builder()
					.email("pcman3566@gmail.com")
					.subject(subject)
					.ccEmail(null)
					.content(mailService.contactUs(
							name, email, content
					))
					.file(null)
					.build();
			mailService.send(emailContentDto);
			return ResponseEntity.ok("Email sent successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid email address: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
		}
	}

//-------------------------------------------------------------------------------------------------------

	@PostMapping("/jobs/saveNotification")
	public Notification saveNotification(@RequestBody NotificationDto notificationDto) {
		return notificationService.saveNotification(notificationDto);
	}

	@PostMapping("/saveNotificationUser")
	public List<NotificationUser> saveNotificationUser(@RequestBody NotificationContentDto notificationContentDto,
													   Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.saveNotificationUser(notificationContentDto.getNotifications(), userDetails.getUserId());
	}

	@PostMapping("/clearAllNotification")
	public List<NotificationUser> clearAllNotification(@RequestBody List<NotificationContentDto> notificationContentDtoList,
													   Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.clearAllNotification(notificationContentDtoList, userDetails.getUserId());
	}

	@GetMapping("/totalNotification")
	public List<Notification> totalNotification(Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationRepository.getNotificationsByUser(userDetails.getUserId());
	}

	@GetMapping("/notificationContent")
	public List<NotificationContentDto> notificationContent(Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.notificationContent(userDetails.getUserId());
	}

//	------------------------------------------------------------------------------------------

	@PostMapping("/saveInterviewerNotification")
	public InterviewerNotification saveInterviewerNotification(
			@RequestBody InterviewerNotificationDto interviewerNotificationDto
	) {
		return notificationService.saveInterviewerNotification(interviewerNotificationDto);
	}

	@PostMapping("/saveInterviewerNotificationUser")
	public InterviewerNotificationUser saveInterviewerNotificationUser(
			@RequestBody InterviewerNotification interviewerNotification,
			Authentication authentication
	) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.saveInterviewerNotificationUser(interviewerNotification, userDetails.getUserId());
	}

	@PostMapping("/clearAllInterviewerNotification")
	public List<InterviewerNotificationUser> clearAllInterviewerNotification(
			@RequestBody List<InterviewerNotification> interviewerNotifications,
			Authentication authentication
	) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.clearAllInterviewerNotification(
				interviewerNotifications, userDetails.getUserId()
		);
	}

	@GetMapping("/totalInterviewerNotification")
	public List<InterviewerNotification> totalInterviewerNotification(Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return interviewerNotificationRepository.getInterviewerNotificationsByUser(userDetails.getUserId());
	}

//	------------------------------------------------------------------------------------------

	@PostMapping("/saveOfferMailNotification")
	public OfferMailNotification offerMailNotification(@RequestBody OfferMailNotificationDto offerMailNotificationDto) {
		return notificationService.saveOfferMailNotification(offerMailNotificationDto);
	}

	@GetMapping("/totalOfferMailNotification")
	public List<OfferMailNotification> totalOfferMailNotification(Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return offerMailNotificationRepository.GetOfferMailNotificationsByUser(userDetails.getUserId());
	}

	@PostMapping("/saveOfferMailNotificationUser")
	public OfferMailNotificationUser saveOfferMailNotification(
			@RequestBody OfferMailNotification offerMailNotification,
			Authentication authentication
	) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.saveOfferMailNotificationUser(offerMailNotification, userDetails.getUserId());
	}

	@PostMapping("/clearAllOfferMailNotification")
	public List<OfferMailNotificationUser> clearAllOfferMailNotification(

			@RequestBody List<OfferMailNotification> offerMailNotifications,
			Authentication authentication
	) {

		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		return notificationService.clearAllOfferMailNotification(offerMailNotifications, userDetails.getUserId());
	}

	@GetMapping("/countsCandidate/{vacancyId}/{status}")
	public List<Candidate> countCandidate(@PathVariable("vacancyId") String vacancyId, @PathVariable("status") CvStatus status) {
		return candidateService.findCandidateCounts(Integer.parseInt(vacancyId), status);
	}

    @GetMapping("/updatedepartment/{id}/{name}")
    @ResponseBody
    public String updateDepartment(@PathVariable("id")String id,@PathVariable("name")String name) {
    	Department department = departmentRepo.findById(Integer.valueOf(id)).get();
    	Optional<Department> duplicate = departmentRepo.findByName(name);
    	if(!(duplicate.isEmpty()) && Integer.valueOf(id) != duplicate.get().getId() ){
    	return "{\"status\": \"error\"}";
    	}else {
    	department.setName(name);
    	departmentRepo.save(department);
        return "{\"status\": \"okey\"}";
    	}
    }
    
    @GetMapping("/deletedepartment/{id}")
    @ResponseBody
    public String deleteDepartment(@PathVariable("id")String id) {
    	 int size = userRepo.findByDepartmentId(Integer.valueOf(id)).size();
    	 if(size == 0) {
    	 Department  department = departmentRepo.findById(Integer.valueOf(id)).get(); 
    	 department.setEnable(false) ;
    	 departmentRepo.save(department);
    	 return "{\"status\": \"okey\"}";
    	 }else {
         return "{\"status\": \"error\"}";
    	 }
    }
}