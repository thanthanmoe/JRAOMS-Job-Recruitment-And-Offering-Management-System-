package com.blackjack.jraoms.controller;

import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.blackjack.jraoms.config.MyUserDetails;
import com.blackjack.jraoms.dto.VacancyDto;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.entity.VacancyHistory;
import com.blackjack.jraoms.exception.EmailAlreadyExistsException;
import com.blackjack.jraoms.service.CompanyService;
import com.blackjack.jraoms.service.UserService;
import com.blackjack.jraoms.service.VacancyHistoryService;
import com.blackjack.jraoms.service.VacancyService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class JuniorController {

	private final VacancyService vacancyService;
	private final CompanyService companyService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final VacancyHistoryService vacancyHistoryService;

	@GetMapping("/vacancy/post")
	public ModelAndView toAddVacancy(ModelMap model) {
		model.addAttribute("companyList", companyService.findAll());
		return new ModelAndView("/junior/add-jobs", "vacancyBean", new VacancyDto());
	}

	@PostMapping("/vacancy/post")
	public String addJob(@ModelAttribute("vacancyBean") @Validated VacancyDto dto, BindingResult bs,
						 HttpSession session, Authentication authentication) {

		if (bs.hasErrors()) {
			 
			session.setAttribute("success", "nullError");
			return "redirect:/vacancy/post";
		}
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		try {
		vacancyService.saveDto(dto,user);
		}catch(Exception e) {
			session.setAttribute("success", "exception");
			return "redirect:/vacancy/post";
		}
		session.setAttribute("success", "success");
		return "redirect:/vacancy/post";
	}

	@GetMapping("/toKillSession")
	public String killSession(HttpSession session) {
		session.removeAttribute("success");
		return "/junior/add-jobs";
	}

	@GetMapping("/vacancydetails/{vacancyId}")
	public ModelAndView viewDetails(@PathVariable String vacancyId,ModelMap modal) {
		Vacancy vacancy = vacancyService.findById(Integer.valueOf(vacancyId)).get();
		List<VacancyHistory> history = vacancyHistoryService.findByVacancyId(Integer.valueOf(vacancyId));
		VacancyHistory vacancyHistory = history.get(history.size()-1);
		modal.addAttribute("history", vacancyHistory);
		return new ModelAndView("adminVacancyDetails","vacancy",vacancy);
	}

	@GetMapping("/userprofile")
	public ModelAndView userProfile(Authentication authentication) {
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		return new ModelAndView("users-profile", "user", user);
	}

	@PostMapping("/userUpdateAvatar")
	public String updateAvatar(@RequestParam("userAvatar")String image,HttpSession session,Authentication authentication) {
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		user.setProfilePicture(image);
		userService.saveEntity(user);
		
		userDetails.setUserImage(image);
		session.setAttribute("updateMailMessage", "successAvatar");
		return "redirect:/userprofile";
	}

	@GetMapping("/sendOtpCode/{email}")
	@ResponseBody
	public String sendOtp(@PathVariable String email, HttpSession session, ModelMap model,Authentication authentication) {

		try {
			MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
			User user = userService.findUserById(userDetails.getUserId());
			model.addAttribute("user", user);
			String code = userService.sendVerificationCodeToEmail(email);
			session.setAttribute(email, code);
			return "{\"status\": \"okey\"}";

		} catch (EmailAlreadyExistsException e) {
			model.addAttribute("otpError", "AlreadyExists");
			System.err.print("exists");
			return "{\"status\": \"exists\"}";
		} catch (Exception e) {
			model.addAttribute("otpError", "Error");
			return "{\"status\": \"error\"}";
		}

	}

	@PostMapping("/updateEmail")
	public String updateEmail(@ModelAttribute("user") User user, @RequestParam("otpCode") String otp,
			HttpSession session, ModelMap model,Authentication authentication) {

		String realOtp = (String) session.getAttribute(user.getEmail());
		System.err.print(realOtp);
		if (realOtp == null || !realOtp.equals(otp)) {
			session.setAttribute("updateMailMessage", "error");
		} else {
			MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
			User entity = userService.findUserById(userDetails.getUserId());
			entity.setEmail(user.getEmail());
			userService.saveEntity(entity);
			session.setAttribute("updateMailMessage", "success");
		}
		return "redirect:/userprofile";
	}

	@GetMapping("/killUpdateMailMessage")
	public String killMailMessage(HttpSession session, ModelMap model,Authentication authentication) {
		session.removeAttribute("updateMailMessage");
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		userService.saveEntity(user);
		model.addAttribute("user", user);
		return "users-profile";
	}

	@PostMapping("/userChangePassword")
	public String changePassword(@RequestParam("password") String inputOldPw,
			@RequestParam("newpassword") String newPw, HttpSession session,Authentication authentication) {

		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());

		if (passwordEncoder.matches(inputOldPw, user.getPassword()) == true) {
			if (inputOldPw.equals(newPw)) {
				session.setAttribute("updateMailMessage", "oldVsNewMatch");
				return "redirect:/userprofile";
			} else {
				user.setPassword(passwordEncoder.encode(newPw));
				userService.saveEntity(user);
				session.setAttribute("updateMailMessage", "pwSuccess");
			}
		} else {
			session.setAttribute("updateMailMessage", "wrongCurrentPw");
			return "redirect:/userprofile";
		}

		return "redirect:/userprofile";
	}
	
	@GetMapping("/vacancy/edit/{id}")
	public ModelAndView editVacancy (@PathVariable String id, ModelMap model) {
		model.addAttribute("companyList", companyService.findAll());
		VacancyDto bean = vacancyService.findVacancyDtoById(Integer.valueOf(id));
		return new ModelAndView("editVacancy","vacancyBean",bean);
	}
	
	@PostMapping("/vacancy/edit")
	public String editVacancy(@ModelAttribute("vacancyBean") @Validated VacancyDto dto, BindingResult bs,
			HttpSession session, Authentication authentication,@RequestParam("vacancyId")String id ) {
 
		if (bs.hasErrors()) {
			session.setAttribute("success", "nullError");
			return "redirect:/vacancy/edit/"+id;
		}
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		try {
		vacancyService.saveDto(dto,user);
		}catch(Exception e) {
			session.setAttribute("success", "error");
			return "redirect:/vacancy/edit/"+id;
		}
		session.setAttribute("vacancyEditMessage", "success");
		return "redirect:/vacancydetails/"+id;
	}
	
	@GetMapping("/killVacancyEditSession")
	public String killVacancyEditSession(HttpSession session) {
		session.removeAttribute("vacancyEditMessage");
		return "adminVacancyDetails";
	}
	
	@GetMapping("/vacancy/close/{id}")
	public String closeVacancy(@PathVariable String id, Authentication authentication,HttpSession session) {
		LocalDate date =  LocalDate.now();
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
		Vacancy vacancy = vacancyService.findById(Integer.valueOf(id)).get();
		vacancy.setStatus("close");
		vacancyService.saveEntity(vacancy);
		VacancyHistory history = new VacancyHistory();
		history.setUser(user);
		history.setVacancy(vacancy);
		history.setDate(date);
		history.setAction("close");
		vacancyHistoryService.save(history);
		session.setAttribute("vacancyEditMessage", "closed");
		return "redirect:/vacancydetails/"+id;
	}
	
	@GetMapping("/vacancy/reopen/{id}")
	public ModelAndView reopenVacancy (@PathVariable String id, ModelMap model) {
		model.addAttribute("companyList", companyService.findAll());
		VacancyDto bean = vacancyService.findVacancyDtoById(Integer.valueOf(id));
		return new ModelAndView("reopenVacancy","vacancyBean",bean);
	}
	
	@PostMapping("/vacancy/reopen")
	public String reopenVacancy(@ModelAttribute("vacancyBean") @Validated VacancyDto dto, BindingResult bs,
			HttpSession session, Authentication authentication,@RequestParam("vacancyId")String id ) {
 
		if (bs.hasErrors()) {
			return "redirect:/vacancy/edit/"+id;
		}
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		User user = userService.findUserById(userDetails.getUserId());
	    vacancyService.reopenVacancy(dto, user);
		session.setAttribute("vacancyEditMessage", "reopen"); 
		return "redirect:/vacancydetails/"+id;
	}
	
	@GetMapping("/vacancy/history/{id}")
	public String viewVacancyHistory(@PathVariable String id,ModelMap model) {
		List<VacancyHistory> historyList = vacancyHistoryService.findByVacancyId(Integer.valueOf(id));
		model.addAttribute("vacancy",vacancyService.findById(Integer.valueOf(id)).get());
		Collections.reverse(historyList);
		model.addAttribute("historyList", historyList);
		return "vacancyManageHistory";
	}
}
