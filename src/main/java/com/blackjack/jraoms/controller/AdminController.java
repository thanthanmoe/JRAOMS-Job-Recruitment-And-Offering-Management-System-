package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.dto.AddCompanyDto;
import com.blackjack.jraoms.dto.UserRegistrationDto;
import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.exception.EmailAlreadyExistsException;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.service.CompanyService;
import com.blackjack.jraoms.service.DepartmentService;
import com.blackjack.jraoms.service.UserService;

import com.blackjack.jraoms.service.UserViewService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping("/system")
public class AdminController {

    private final CompanyService companyService;
    private final UserService userService;
    private final UserViewService userViewService;
    private final DepartmentRepository departmentRepo;

 
    @GetMapping("/updatecompany/{id}")
    public ModelAndView toUpdateCompany(@PathVariable("id") String id) {
        Optional< Company> company=companyService.findById(Integer.parseInt(id));
        List<Department> list = departmentRepo.findByEnable(true);
        company.get().setDepartments(list);
        return new ModelAndView("/admin/company_update","updateCompanyDto",company.get());
    }

    @PostMapping("/updatecompany")
    public String updateCompany(@RequestParam("companyId")String id,@ModelAttribute("updateCompanyDto")Company company
            ,@RequestParam("department")String newDepartment,HttpSession session) {
        company.setId(Integer.valueOf(id));
       
        boolean error = companyService.updateCompany(company, newDepartment);
        if(error == true) {
            session.setAttribute("companyMessage", "duplicate");
            return "redirect:/system/updatecompany/"+id;
        }else {
            session.setAttribute("companyMessage", "successUpdate");
            return "redirect:/system/companydetails/"+id;
        }

    }

    @GetMapping("/companydetails/{id}")
    public String companyDetails(@PathVariable("id") String id,ModelMap model) {
        Optional< Company> company=companyService.findById(Integer.parseInt(id));
        List<Department> list = departmentRepo.findByEnable(true);
        company.get().setDepartments(list);
        company.ifPresent(value -> model.addAttribute("company", value));
        return "/admin/company-details";
    }

    @GetMapping("/killCompanyMessage")
    @ResponseBody
    public String kllAlertMessage(HttpSession session) {
        session.removeAttribute("companyMessage");
        return "{\"status\": \"okey\"}";
    }

    @GetMapping("/userregister")
    public String userRegisterForm(Model model){
        model.addAttribute("userRegistrationDto",new UserRegistrationDto());
        return "/admin/user_register";
    }

    @PostMapping("/userregister")
    public String userRegistration(@ModelAttribute("userRegistrationDto") UserRegistrationDto userRegistrationDto,HttpSession session){
        try {
            userService.userRegistration(userRegistrationDto);
            session.setAttribute("message", "registerSuccess");
            return "redirect:/system/userregister";
        }catch (EmailAlreadyExistsException e){
        	session.setAttribute("message", "registerExists");
            return "redirect:/system/userregister";
        }catch (Exception e){
        	session.setAttribute("message", "registerError");
            return "redirect:/system/userregister";
        }
    }

    @GetMapping("/manageusers")
    public String showAllUsers(ModelMap model) {
        return userViewService.getAllUsers(model);
    }

    @GetMapping("/usereditinfo/{id}")
    public ModelAndView editInfo(@PathVariable("id")String userId,ModelMap model) {
    	User user = userService.findUserById(Integer.valueOf(userId));
    	UserRegistrationDto dto = new UserRegistrationDto();
    	dto.setId(user.getId());
    	dto.setName(user.getName());
    	dto.setEmail(user.getEmail());
    	dto.setRole(user.getRole().toString());
    	Boolean enable = user.isEnable();
    	if(enable == true) {
    		dto.setStatus(1);
    	}else {
    		dto.setStatus(0);
    	}
    	model.addAttribute("department",user.getDepartment());
    	return new ModelAndView("/admin/user_update","userBean",dto);
    }
    
    @PostMapping("/usereditinfo")
    public String userEditInfo(@RequestParam("userId")String id,@ModelAttribute("userBean")UserRegistrationDto userDto,HttpSession session) {
    	userDto.setId(Integer.valueOf(id));
    	boolean error = userService.updateUser(userDto);
    	if(error == true) {
    		session.setAttribute("message", "userUpdateError");
    		return "redirect:/system/usereditinfo/"+id;
    	}else {
    		session.setAttribute("message", "userUpdateSuccess");
    		return "redirect:/system/usereditinfo/"+id;
    	}
    }
}