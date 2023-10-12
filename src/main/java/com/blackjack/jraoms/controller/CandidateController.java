package com.blackjack.jraoms.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

import com.blackjack.jraoms.entity.*;
import com.blackjack.jraoms.repository.CandidateRepository;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.service.*;
import org.jodconverter.core.office.OfficeException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.blackjack.jraoms.dto.CandidateDto;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CandidateController {
	private final CandidateService candidateService;
	private final CandidateCVService candidateCVService;
	private final VacancyService vacancyService;
	private final DepartmentService departmentService;
	private final CandidateRepository repo;
	private final DepartmentRepository departmentRepo;
	
	public HttpSession session;
	@GetMapping("/")
	public ModelAndView vacancyView(ModelMap model) {
		List<Department> departmentList = departmentRepo.findByEnable(true);
		List<Vacancy> list = vacancyService.findAllByStatus("active");
		model.addAttribute("departmentList",departmentList);
		return new ModelAndView("/candidate/candidateVacancyView","vacancyList",list);

	}

	@GetMapping("/jobs/details/{id}")
	public ModelAndView vacancyDetails(@PathVariable String id) {
		Vacancy vacancy = vacancyService.findByVacancy(Integer.valueOf(id));
		return new ModelAndView("/candidate/candidateVacancyDetails","vacancy" ,vacancy);
	}

	@GetMapping("/jobs/addcv/{id}")
	public ModelAndView candidate(@PathVariable String id,ModelMap model) {
		Vacancy vacancy = vacancyService.findByVacancy(Integer.valueOf(id));
		model.addAttribute("vacancy",vacancy);
		return new ModelAndView("/candidate/candidateAddCv","candidate",new CandidateDto());
	}

	@PostMapping("/jobs/addcv")
	public String saveCandidate(@ModelAttribute("candidate") @Validated CandidateDto candidateDto,
								@RequestParam("file") MultipartFile file,HttpSession session) throws Exception{
		int vacancyId=Integer.parseInt(candidateDto.getVacancyId());

		Optional<Candidate> duplicate = repo.findByEmailAndVacancyId(candidateDto.getEmail(), vacancyId);
		if(!(duplicate.isEmpty())){
			session.setAttribute("success", "duplicate");
			return "redirect:/jobs/addcv/"+vacancyId;
		}

		byte[] docData = file.getBytes();
		if(file.getContentType().equalsIgnoreCase("application/msword")) {
			byte[] pdfData = convertDocToPdf(docData);

			String uniqueFileName = UUID.randomUUID().toString() + "_" + candidateDto.getName() + ".pdf";
			CandidateCV candidateCV = new CandidateCV(uniqueFileName, "application/pdf", pdfData);
			candidateCVService.saveCandidateCV(candidateCV);

			Vacancy vacancy=vacancyService.findByVacancy(vacancyId);
			Candidate candidate=Candidate.builder()
					.name(candidateDto.getName())
					.degree(candidateDto.getDegree())
					.dob(candidateDto.getDob())
					.email(candidateDto.getEmail())
					.experiences(candidateDto.getExperiences())
					.gender(candidateDto.getGender())
					.language(candidateDto.getLanguage())
					.level(candidateDto.getLevel())
					.mainTechnical(candidateDto.getMainTechnical())
					.phone(candidateDto.getPhone())
					.salary(candidateDto.getSalary())
					.technical(candidateDto.getTechnical())
					.cvStatus(CvStatus.RECEIVE)
					.date(LocalDate.now())
					.candidateCV(candidateCV)
					.vacancy(vacancy)
					.build();
			candidateService.saveCandidate(candidate);
			session.setAttribute("success","cvSuccess");
			session.setAttribute("vacancyId", vacancyId);
			session.setAttribute("candidateEmail",candidateDto.getEmail());
			session.setAttribute("candidateName", candidateDto.getName());
			session.setAttribute("vacancyName", vacancy.getPosition());
			session.setAttribute("companyName", vacancy.getDepartment().getCompany().getName());
		}else if(file.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
			byte[] pdfData = convertDocxToPdf(docData);
			String uniqueFileName = UUID.randomUUID().toString() + "_" + candidateDto.getName() + ".pdf";
			CandidateCV candidateCV = new CandidateCV(uniqueFileName, "application/pdf", pdfData);
			candidateCVService.saveCandidateCV(candidateCV);

			Vacancy vacancy=vacancyService.findByVacancy(vacancyId);
			Candidate candidate=Candidate.builder()
					.name(candidateDto.getName())
					.degree(candidateDto.getDegree())
					.dob(candidateDto.getDob())
					.email(candidateDto.getEmail())
					.experiences(candidateDto.getExperiences())
					.gender(candidateDto.getGender())
					.language(candidateDto.getLanguage())
					.level(candidateDto.getLevel())
					.mainTechnical(candidateDto.getMainTechnical())
					.phone(candidateDto.getPhone())
					.salary(candidateDto.getSalary())
					.technical(candidateDto.getTechnical())
					.cvStatus(CvStatus.RECEIVE)
					.date(LocalDate.now())
					.candidateCV(candidateCV)
					.vacancy(vacancy)
					.build();
			candidateService.saveCandidate(candidate);
			session.setAttribute("success","cvSuccess");
			session.setAttribute("vacancyId", vacancyId);
			session.setAttribute("candidateEmail",candidateDto.getEmail());
			session.setAttribute("candidateName", candidateDto.getName());
			session.setAttribute("vacancyName", vacancy.getPosition());
			session.setAttribute("companyName", vacancy.getDepartment().getCompany().getName());
		}else {
			String uniqueFileName = UUID.randomUUID().toString() + "_" + candidateDto.getName() + ".pdf";
			CandidateCV candidateCV = new CandidateCV(uniqueFileName, "application/pdf", file.getBytes());
			candidateCVService.saveCandidateCV(candidateCV);

			Vacancy vacancy=vacancyService.findByVacancy(vacancyId);
			Candidate candidate=Candidate.builder()
					.name(candidateDto.getName())
					.degree(candidateDto.getDegree())
					.dob(candidateDto.getDob())
					.email(candidateDto.getEmail())
					.experiences(candidateDto.getExperiences())
					.gender(candidateDto.getGender())
					.language(candidateDto.getLanguage())
					.level(candidateDto.getLevel())
					.mainTechnical(candidateDto.getMainTechnical())
					.phone(candidateDto.getPhone())
					.salary(candidateDto.getSalary())
					.technical(candidateDto.getTechnical())
					.cvStatus(CvStatus.RECEIVE)
					.date(LocalDate.now())
					.candidateCV(candidateCV)
					.vacancy(vacancy)
					.build();
			candidateService.saveCandidate(candidate);
			session.setAttribute("success","cvSuccess");
			session.setAttribute("vacancyId", vacancyId);
			session.setAttribute("candidateEmail",candidateDto.getEmail());
			session.setAttribute("candidateName", candidateDto.getName());
			session.setAttribute("vacancyName", vacancy.getPosition());
			session.setAttribute("companyName", vacancy.getDepartment().getCompany().getName());
		}
		return "redirect:/";

	}

	@GetMapping("/jobs/toKillSuccessMessage")
	public String killSession(HttpSession session) {
		session.removeAttribute("success");
		session.removeAttribute("candidateEmail");
		session.removeAttribute("vacancyId");
		session.removeAttribute("candidateName");
		session.removeAttribute("vacancyName");
		session.removeAttribute("companyName");
		return "/candidate/candidateVacancyView";
	}

	public byte[] convertDocxToPdf(byte[] docxData) throws Exception {
	        ByteArrayInputStream docxIn = new ByteArrayInputStream(docxData);
	        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

	        IConverter converter = LocalConverter.builder().build();
	        converter
	                .convert(docxIn).as(DocumentType.DOCX)
	                .to(pdfOut).as(DocumentType.PDF)
	                .execute();
			return pdfOut.toByteArray();
	}

	public byte[] convertDocToPdf(byte[] docData) throws OfficeException {
       ByteArrayInputStream docIn = new ByteArrayInputStream(docData);
       ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
      
       IConverter converter = LocalConverter.builder().build();

       converter
               .convert(docIn).as(DocumentType.DOC)
               .to(pdfOut).as(DocumentType.PDF)
               .execute();
       return pdfOut.toByteArray();
   }

	
}
