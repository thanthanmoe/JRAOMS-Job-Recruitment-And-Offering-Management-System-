package com.blackjack.jraoms.controller;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.blackjack.jraoms.dto.*;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.ChartsDto;
import com.blackjack.jraoms.dto.EmailContactDto;
import com.blackjack.jraoms.dto.FormDto;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.entity.CvStatus;
import com.blackjack.jraoms.entity.InterviewProcess;
import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.service.CandidateCVService;
import com.blackjack.jraoms.service.CandidateService;
import com.blackjack.jraoms.service.InterviewProcessService;
import com.blackjack.jraoms.service.MailService;
import com.blackjack.jraoms.service.StatusHistoryService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyHistoryService;
import com.blackjack.jraoms.service.VacancyService;
import java.util.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
public class SeniorController {
	private final MailService mailService;
	private final CandidateCVService candidateCVService;
	private final CandidateService candidateService;
	private final UserService userService;
	private final StatusHistoryService statusHistoryService;
	private final VacancyService vacancyService;
	private final VacancyHistoryService vacancyHistoryService;
	private final InterviewProcessService interviewProcessService;
	
	@GetMapping("/manage/allcandidate")
	public String candidatePagination() {

		return "tables-data";
	}

	@GetMapping("/manage/status/{vacancyId}")
	public String candidatePage(@PathVariable("vacancyId") String vacancyId,ModelMap model) {
		model.addAttribute("vacancyId",vacancyId);
		Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
		List<Candidate> candidates=candidateService.findAllCandidateByVacancyId(Integer.parseInt(vacancyId));
		model.addAttribute("candidateCount",candidates.size());
		model.addAttribute("vacancyName",vacancy.get().getPosition());
		model.addAttribute("vacancyDepartment",vacancy.get().getDepartment().getName());
		model.addAttribute("actionStage","RECEIVE");
		return "candidateStatusChange";
	}

	@GetMapping("/manage/statusnoti/{vacancyId}")
	public String candidatePageByNoti(@PathVariable("vacancyId") String vacancyId,ModelMap model) {
		model.addAttribute("vacancyId",vacancyId);
		Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
		List<Candidate> candidates=candidateService.findAllCandidateByVacancyId(Integer.parseInt(vacancyId));
		model.addAttribute("candidateCount",candidates.size());
		model.addAttribute("vacancyName",vacancy.get().getPosition());
		model.addAttribute("actionStage","PASSED");
		return "candidateStatusChange";
	}

	@GetMapping("/allCandidate")
	public String allCandidate(ModelMap model) {
		List<Candidate> candidates=candidateService.findAllCandidate();
		model.addAttribute("candidates",candidates);
		return "tables-data";
	}
	
	@GetMapping("/allCandidate/{status}")
	public String findByCVStatus(ModelMap model,@PathVariable("status") String cvStatus) {
		List<Candidate> candidates=candidateService.findByCVStatus(cvStatus);
		model.addAttribute("candidates",candidates);
		return "tables-data";
	}

	@PostMapping("/senior/downloadAllCV")
	public void downloadCVs(@RequestParam("id") String[] id, HttpServletResponse response, Authentication authentication) throws IOException {
		int[] checkedId = Arrays.stream(id).mapToInt(Integer::parseInt).toArray();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			for (int checkId : checkedId) {
				Candidate candidate = candidateService.findCandidateById(checkId).orElse(null);
				if(candidate == null) {
					continue; // Skip if no candidate found
				}

				candidate.setCvStatus(CvStatus.VIEW);
				candidateService.candidate(candidate);

				MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
				User user = userService.findUserById(userDetails.getUserId());

				StatusHistory history = new StatusHistory();
				history.setCandidate(candidate);
				history.setAction(CvStatus.VIEW);
				history.setUser(user);
				history.setDate(LocalDate.now());
				statusHistoryService.saveHistory(history);

				CandidateCV cv = candidateCVService.findCVById(checkId).orElse(null);
				if(cv == null) {
					continue; // Skip if no CV found
				}

				byte[] data = cv.getData();
				String fileName = cv.getName() + cv.getType(); // Assume all CVs are in PDF format

				zipOutputStream.putNextEntry(new ZipEntry(fileName));
				zipOutputStream.write(data);
				zipOutputStream.closeEntry();
			}
		}

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=candidate.zip");
		response.setStatus(HttpStatus.OK.value());

		response.getOutputStream().write(byteArrayOutputStream.toByteArray());
		response.getOutputStream().flush();
	}


	@GetMapping("/updateCVStatus/{id}/{vacancyId}")
	public String updateCandidate(ModelMap model,@PathVariable("id") String id,@PathVariable("vacancyId") String vacancyId,HttpSession session) {

		 User user = userService.findUserById(1);
		  model.addAttribute("vacancyId",vacancyId);
		  Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
			model.addAttribute("vacancyName",vacancy.get().getPosition());
		        Candidate candidate =candidateService.findCandidateById(Integer.parseInt(id)).orElse(null);
		        candidate.setCvStatus(CvStatus.CONSIDERING);
				candidateService.candidate(candidate); 
		        
		        StatusHistory history=new StatusHistory();
			    history.setCandidate(candidate);
			    history.setAction(CvStatus.CONSIDERING);
			    history.setUser(user);
			    history.setDate(LocalDate.now());
			 statusHistoryService.saveHistory(history);
		
		  	return "candidateStatusChange";
		}

	
	@GetMapping("/senior/actionChange/{action}/{id}/{vacancyId}")
	public String actionChangeHistory(@PathVariable("action") CvStatus status,@PathVariable("id") String id,
			@PathVariable("vacancyId") String vacancyId,ModelMap model,Authentication authentication) {
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
	    User user = userService.findUserById(userDetails.getUserId());
		 model.addAttribute("vacancyId",vacancyId);
		 Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
			model.addAttribute("vacancyName",vacancy.get().getPosition());
		 if(status.equals(CvStatus.RECEIVE) || status.equals(CvStatus.VIEW)
				 || status.equals(CvStatus.CONSIDERING)) {
		        Candidate candidate =candidateService.findCandidateById(Integer.parseInt(id)).orElse(null);
		        candidate.setCvStatus(status);
				candidateService.saveCandidate(candidate); 
				
				 StatusHistory history=new StatusHistory();
				    history.setCandidate(candidate);
				    history.setAction(status);
				    history.setUser(user);
				    history.setDate(LocalDate.now());
				    statusHistoryService.saveHistory(history);
		}else {
			model.addAttribute("error","error");
		}
				
		return "candidateStatusChange";
	}
	
	@GetMapping("/manage/seemorecandidate/{id}")
	public String seeMoreCandidate(ModelMap model,@PathVariable("id") String id) {
		try {
	        int idInt = Integer.parseInt(id);
			model.addAttribute("statusSeeMore",true);

	        Candidate candidate = candidateService.findCandidateById(idInt).orElse(null);
	        Vacancy vacancy=candidate.getVacancy();
			model.addAttribute("vacancy",vacancy);
	        model.addAttribute("candidates", candidate);

	        CandidateCV cv = candidateCVService.findCVById(idInt).orElse(null);
	        model.addAttribute("candidatesId",candidate.getId());

			if(candidate.getStatusHistories() != null && !candidate.getStatusHistories().isEmpty()){
				model.addAttribute("history", candidate.getStatusHistories().get(0).getAction());
			}
			if(candidate.getInterviewHistories() != null && !candidate.getInterviewHistories().isEmpty()){
				model.addAttribute("intHistory", candidate.getInterviewHistories().get(0).getAction());
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return "candidateSeeMore";
	}
	@GetMapping("/manage/seemore/{id}")
	public String seeMore(ModelMap model,@PathVariable("id") String id) {
		try {
			int idInt = Integer.parseInt(id);
			model.addAttribute("allCandidate",true);
			Candidate candidate = candidateService.findCandidateById(idInt).orElse(null);
			Vacancy vacancy=candidate.getVacancy();
			model.addAttribute("vacancy",vacancy);
			model.addAttribute("candidates", candidate);

			CandidateCV cv = candidateCVService.findCVById(idInt).orElse(null);
			model.addAttribute("candidatesId",candidate.getId());
			if(candidate.getStatusHistories() != null && !candidate.getStatusHistories().isEmpty()){
				model.addAttribute("history", candidate.getStatusHistories().get(0).getAction());
			}
			if(candidate.getInterviewHistories() != null && !candidate.getInterviewHistories().isEmpty()){
				model.addAttribute("intHistory", candidate.getInterviewHistories().get(0).getAction());
			}

		}catch (Exception e) {
			// TODO: handle exception
		}

		return "candidateSeeMore";
	}
	@GetMapping("/manage/{format}")
	public void downloadReportInterviewProcess(@PathVariable("format") String format,HttpServletResponse response)
			throws JRException, IOException {
	
		List<InterviewProcess> allCandidates = interviewProcessService. getAllData();
		File file = ResourceUtils.getFile("classpath:interview_process.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCandidates);
		Map<String, Object> parameters = new HashMap<>();

		parameters.put("createdBy", "InterView Process Summary List");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		
		if (format.equalsIgnoreCase("pdf")) {
			  response.setContentType("application/pdf");
              response.setHeader("Content-Disposition", "attachment; filename=InterviewProcess.pdf");

              JRPdfExporter exporterPdf = new JRPdfExporter();
              exporterPdf.setExporterInput(new SimpleExporterInput(jasperPrint));
              exporterPdf.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
              exporterPdf.exportReport();
          }else if(format.equalsIgnoreCase("xlsx")) {
        	    try {
        	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        	        response.setHeader("Content-Disposition", "attachment; filename=InterviewProcess.xlsx");

        	        JRXlsxExporter exporterXLS = new JRXlsxExporter();
        	        exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
        	        exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
        	        exporterXLS.exportReport();
        	    } catch (Exception e) {
        	        // handle exception
        	        e.printStackTrace();
        	    }
          }
		}

	@GetMapping("/candidatecharts")
	public String candidateVacancyChart(ModelMap model) {
	    List<ChartsDto> charts = candidateService.fetchCountByVacancy_id();
	    model.addAttribute("chartData", charts);

	    return "vacancy-candidate-charts";
	}

	@GetMapping("/vacancycharts")
	public String vacancyChart(ModelMap model) {
		List<Object[]> charts = vacancyHistoryService.getMonthlyCreateActions();
		model.addAttribute("chartData", charts);
		List<MonthlyCountDto> chart = vacancyService.getMonthlyCounts();
		model.addAttribute("candidateData", chart);
		return "vacancy-charts";
	}
	@GetMapping(name = "/senior/sendEmail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> sendMail(@RequestBody FormDto formDto){
		EmailContactDto emailContactDtos=EmailContactDto.builder()
				.email(formDto.getTo())
				.ccEmail(formDto.getCc())
				.content(formDto.getContent())
				.subject(formDto.getSubject())
				.file(formDto.getFile())
				.build();
		mailService.send(emailContactDtos);
		return ResponseEntity.ok().body("successfully");
	}

    @GetMapping("/manage/interviewprocess")
    public String findProcess(ModelMap model){
    	List<InterviewProcess> allCandidates = interviewProcessService. getAllData();
        model.addAttribute("interviewList",allCandidates);
        return "interview-process";
    }
}
