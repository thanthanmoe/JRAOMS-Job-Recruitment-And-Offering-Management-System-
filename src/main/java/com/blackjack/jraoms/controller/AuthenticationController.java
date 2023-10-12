package com.blackjack.jraoms.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AuthenticationController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	
    @GetMapping("/login")
    public String login(Principal principal) {
		if (principal == null){
			return "/auth/login";
		}
		else return "redirect:/dashboard";
    }

    @GetMapping("/auth/verifyEmailToResetPassword")
    public String verify(){
    	return "/auth/verifyEmail";
    }
    
    @PostMapping("/auth/verifyEmailToResetPassword")
    public String verifyMail(@RequestParam("email")String email,HttpSession session,ModelMap model) {
         
    	Optional<User> user = userService.findByEmail(email);
    	if(user.isEmpty()) {
    		session.setAttribute("message", "Invaild");
    		return "redirect:/auth/verifyEmailToResetPassword";
    	}
    	String code = userService.sendCodeToResetPassword(email);
		session.setAttribute(email, code);
		model.addAttribute("email",email);
    	return "/auth/verifyCode";
    }
    
    @PostMapping("/auth/verifyOtpToResetPassword")
    public String verifyOtp(@RequestParam("verificationCode")String inputCode,@RequestParam("email")String email,
    		HttpSession session,ModelMap model){
 
    	String realVerifyCode = (String) session.getAttribute(email);
    	if (realVerifyCode == null || !realVerifyCode.equals(inputCode)) {
    		model.addAttribute("email",email);
    		session.setAttribute("message", "wrongCode");
    		return "/auth/verifyCode";
    	}else {
    		model.addAttribute("email",email);
    		return "/auth/setNewPassword";
    	}
    }
    
    @PostMapping("/auth/newPasswordToResetPassword")
    public String newPassword(@RequestParam("email")String email,@RequestParam("confirmPassword")String password,
    		HttpSession session,ModelMap model){
 
    	User user = userService.findByEmail(email).get();
    	String newPassword = passwordEncoder.encode(password);
    	if(passwordEncoder.matches(password, user.getPassword())) {
    		model.addAttribute("email",email);
    		session.setAttribute("message", "oldPassword");
    		return "/auth/setNewPassword";
    	}else {
    	user.setPassword(newPassword);
    	userService.saveEntity(user);
    	session.setAttribute("message", "changedPassword");
    	return "redirect:/login";
    	}
    }

    @GetMapping("/auth/clearSessionMessage")
    @ResponseBody
    public String clearSession(HttpSession session) {
    	session.removeAttribute("message");
    	return "{\"status\": \"okey\"}";
    }
    
    @GetMapping("/auth/resendVerificationCode/{email}")
    @ResponseBody
    public String resendOtp(HttpSession session,@PathVariable("email")String email) {
    
    	String code = userService.sendCodeToResetPassword(email);
        session.setAttribute(email, code);
    	return "{\"status\": \"okey\"}";
    }


}