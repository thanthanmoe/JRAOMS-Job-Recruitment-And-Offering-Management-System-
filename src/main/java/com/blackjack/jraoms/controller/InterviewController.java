package com.blackjack.jraoms.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.entity.CvStatus;
import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.service.CandidateCVService;
import com.blackjack.jraoms.service.CandidateService;
import com.blackjack.jraoms.service.InterviewHistoryService;
import com.blackjack.jraoms.service.StatusHistoryService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyService;


@Controller
@AllArgsConstructor

public class InterviewController {
	private final CandidateCVService candidateCVService;
	private final CandidateService candidateService;
	private final VacancyService vacancyService;
	private final UserService userService;
	private final InterviewHistoryService interviewHistoryService;
	private final StatusHistoryService statusHistoryService;
	
	@GetMapping("/interview/status/{vacancyId}")
	public String interviewerView(@PathVariable("vacancyId") String vacancyId,ModelMap model) {

		Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
		List<Candidate> candidates=candidateService.findAllCandidateByVacancyId(Integer.parseInt(vacancyId));
		model.addAttribute("candidateCount",candidates.size());
		model.addAttribute("vacancyName",vacancy.get().getPosition());
		model.addAttribute("vacancyId",vacancyId);
		model.addAttribute("actionStage","CONSIDERING");
		return "interviewer-view-candidate";
	}

	@GetMapping("/interview/statusnoti/{vacancyId}")
	public String interviewerViewNoti(@PathVariable("vacancyId") String vacancyId,ModelMap model) {

		Optional<Vacancy> vacancy=vacancyService.findById(Integer.parseInt(vacancyId));
		List<Candidate> candidates=candidateService.findAllCandidateByVacancyId(Integer.parseInt(vacancyId));
		model.addAttribute("candidateCount",candidates.size());
		model.addAttribute("vacancyName",vacancy.get().getPosition());
		model.addAttribute("vacancyId",vacancyId);
		model.addAttribute("actionStage","PENDING");
		return "interviewer-view-candidate";
	}

	@GetMapping("interview/seemorecandidate/{id}")
	public String seeMoreCandidateForInterviewer(ModelMap model,@PathVariable("id") String id) {
		try {
			int idInt = Integer.parseInt(id);
			model.addAttribute("interviewerSeeCandidate",true);
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
	@GetMapping("/interviewer/actionChange/{action}/{id}/{vacancyId}")
	public String actionChange(@PathVariable("action") CvStatus status,@PathVariable("id") String id,
			@PathVariable("vacancyId") String vacancyId,ModelMap model,Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
	    User user = userService.findUserById(userDetails.getUserId());
	    
		 model.addAttribute("vacancyId",vacancyId);
		 if(status.equals(CvStatus.PENDING) || status.equals(CvStatus.PASSED) || status.equals(CvStatus.CANCEL)) {
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
			model.addAttribute("errro","error");
		}
				
		return "interviewer-view-candidate";
	}
	@GetMapping("/interviewer/{action}/{id}/{vacancyId}")
	public String cancel(@PathVariable("action") CvStatus status,@PathVariable("id") String id,
			@PathVariable("vacancyId") String vacancyId,ModelMap model,Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
	    User user = userService.findUserById(userDetails.getUserId());
	    
		 model.addAttribute("vacancyId",vacancyId);
		 if(status.equals(CvStatus.CANCEL)) {
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
			  Candidate candidate =candidateService.findCandidateById(Integer.parseInt(id)).orElse(null);
		        candidate.setCvStatus(status);
				candidateService.saveCandidate(candidate); 
				
				 StatusHistory history=new StatusHistory();
				    history.setCandidate(candidate);
				    history.setAction(CvStatus.NOTINTERVIEW);
				    history.setUser(user);
				    history.setDate(LocalDate.now());
				    statusHistoryService.saveHistory(history);
		}
				
		return "interviewer-view-candidate";
	}
}

