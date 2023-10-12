package com.blackjack.jraoms.service;

import com.blackjack.jraoms.dto.EmailContactDto;
import com.blackjack.jraoms.dto.UserRegistrationDto;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.Role;
import com.blackjack.jraoms.exception.EmailAlreadyExistsException;
import com.blackjack.jraoms.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	public User findUserById(Integer id){
		return userRepository.findById(id).get();
	}

	public void userRegistration(UserRegistrationDto userRegistrationDto) {

			// Check if email already exists
			if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
				log.error("Email already exists: {}", userRegistrationDto.getEmail());
				throw new EmailAlreadyExistsException("Email already exists.");
			}

			//Random 6-digit number for password
			Random random = new Random();
			int min = 100000; // Minimum 6-digit number
			int max = 999999; // Maximum 6-digit number
			String randomNumber = String.valueOf(random.nextInt(max - min + 1) + min);

			//Send default password email to user
			String content = mailService.sendPasswordEmailTemplate(randomNumber);
			var emailContactDto = EmailContactDto.builder()
					.email(userRegistrationDto.getEmail())
					.ccEmail(null)
					.subject("Login Password")
					.file(null)
					.content(content)
					.build();
			mailService.send(emailContactDto);

			//Save register user
			int departmentId = userRegistrationDto.getDepartmentId();
			Department department = departmentRepository.findById(departmentId).orElse(null);

			var user = User.builder()
					.name(userRegistrationDto.getName())
					.email(userRegistrationDto.getEmail())
					.password(passwordEncoder.encode(randomNumber))
					.role(Role.valueOf(userRegistrationDto.getRole()))
					.department(department)
					.registerDate(LocalDate.now())
					.profilePicture("defaultUser.png")
					.enable(true)
					.build();
			userRepository.save(user);

	}
	
	public void saveEntity(User user) {
		userRepository.save(user);
	}
	
	public String sendVerificationCodeToEmail(String email) {

			// Check if email already exists
			if (userRepository.existsByEmail(email)) {
				log.error("Email already exists: {}", email);
				throw new EmailAlreadyExistsException("Email already exists.");
			}

			//Random 6-digit number for password
			Random random = new Random();
			int min = 100000; // Minimum 6-digit number
			int max = 999999; // Maximum 6-digit number
			String randomNumber = String.valueOf(random.nextInt(max - min + 1) + min);

			//Send default password email to user
			String content = mailService.sendOTPEmailTemplate(randomNumber);
			EmailContactDto emailContactDto = EmailContactDto.builder()
					.email(email)
					.ccEmail(null)
					.subject("Verify Email")
					.file(null)
					.content(content)
					.build();
			mailService.send(emailContactDto);
            return randomNumber;
	}
	
	public Optional<User> findByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	public String sendCodeToResetPassword(String email) {

		//Random 6-digit number for password
		Random random = new Random();
		int min = 100000; // Minimum 6-digit number
		int max = 999999; // Maximum 6-digit number
		String randomNumber = String.valueOf(random.nextInt(max - min + 1) + min);

		//Send default password email to user
		String content = mailService.sendOTPResetPasswordTemplate(randomNumber);
		EmailContactDto emailContactDto = EmailContactDto.builder()
				.email(email)
				.ccEmail(null)
				.subject("Reset Password")
				.file(null)
				.content(content)
				.build();
		mailService.send(emailContactDto);
        return randomNumber;
     }
	
	 public boolean  updateUser(UserRegistrationDto dto) {
		 boolean error = false;
		 User user = userRepository.findById(dto.getId()).get();
		 if(dto.getStatus() == 0) {
			 user.setEnable(false);
		 }else {
			 user.setEnable(true);
		 }
		 user.setName(dto.getName());
		 Optional<Department> department = departmentRepository.findById(dto.getDepartmentId());
		 if(!(department.isEmpty())) {
			 user.setDepartment(department.get());
			 if(dto.getRole().equalsIgnoreCase("SENIOR")) {
				 user.setRole(Role.SENIOR);
			 }else if(dto.getRole().equalsIgnoreCase("JUNIOR")) {
				 user.setRole(Role.JUNIOR);
			 }else if(dto.getRole().equalsIgnoreCase("INTERVIEWER")){
				 user.setRole(Role.INTERVIEWER);
			 }
			 userRepository.save(user);
		 }else {
			 error = true;
		 }
		 return error;
	 }
}