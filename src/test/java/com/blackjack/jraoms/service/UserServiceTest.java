package com.blackjack.jraoms.service;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blackjack.jraoms.dto.EmailContactDto;
import com.blackjack.jraoms.dto.UserRegistrationDto;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.entity.Role;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.exception.EmailAlreadyExistsException;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.repository.UserRepository;
import com.blackjack.jraoms.service.MailService;
import com.blackjack.jraoms.service.UserService;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testUserFindById() {
        int mockUserId = 1;
        Department department = new Department();
        
        User mockUser = User.builder()
                    .name("Than Than Moe")
                    .email("moelaybtu@gmail.com")
                    .password("123456")
                    .role(Role.ADMIN)
                    .department(department)
                    .registerDate(LocalDate.now())
                    .profilePicture("defaultUser.png")
                    .enable(true)
                    .build();
        

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));

        User result = userService.findUserById(mockUserId);
        assertEquals(mockUser, result);
    }
    
    @Test
    public void testUserRegistration() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("sma700.zkk@gmail.com");
        dto.setDepartmentId(1);
        dto.setName("Than Than Moe");
        dto.setRole("ADMIN");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(anyInt())).thenReturn(Optional.of(new Department()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(mailService).send(any(EmailContactDto.class));

        assertDoesNotThrow(() -> userService.userRegistration(dto));

        verify(userRepository, times(1)).save(any(User.class));
        verify(mailService, times(1)).send(any(EmailContactDto.class));
    }

    @Test
    public void testUserRegistrationWithExistingEmail() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("sma700.zkk@gmail.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(EmailAlreadyExistsException.class, () -> userService.userRegistration(dto));

    }
    @Test
    public void testSaveEntity() {
     
        Department department = new Department(); // or mock this if necessary
        
        User expectedUser = User.builder()
        		.name("Than Than Moe")
				.email("moelaybtu@gmail.com")
				.password("123456")
				.role(Role.ADMIN)
				.department(department)
				.registerDate(LocalDate.now())
				.profilePicture("defaultUser.png")
				.enable(true)
				.build();

        // When
        userService.saveEntity(expectedUser);

        // Then
        verify(userRepository, times(1)).save(any(User.class));  // verifying the save method was called once
    }
    @Test
    public void testSendVerificationCodeToEmailWithEmailExists() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(EmailAlreadyExistsException.class, () -> userService.sendVerificationCodeToEmail(email));
    }

    @Test
    public void testSendVerificationCodeToEmailWithNewEmail() {
        String email = "newuser@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        // Assuming sendOTPEmailTemplate just returns the given randomNumber
        when(mailService.sendOTPEmailTemplate(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        String otp = userService.sendVerificationCodeToEmail(email);

        assertTrue(otp.length() == 6);
        verify(mailService, times(1)).send(any(EmailContactDto.class));
    }

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    public void testSendCodeToResetPassword() {
        String email = "test@example.com";
        // Assuming sendOTPEmailTemplate just returns the given randomNumber
        when(mailService.sendOTPEmailTemplate(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        String otp = userService.sendCodeToResetPassword(email);

        assertTrue(otp.length() == 6);
        verify(mailService, times(1)).send(any(EmailContactDto.class));
    }
    
    @Test
    void testUpdateUser() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        dto.setStatus(1);
        dto.setEmail("sma700.zkk@gmail.com");
        dto.setDepartmentId(1);
        dto.setName("Than Than Moe");
        dto.setRole("ADMIN");
        User user = new User();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        // When
        userService.updateUser(dto);
        Boolean enable=true;
        // Then
        verify(userRepository, times(1)).findById(1); // Ensuring the `findById` method was called with the correct ID
        assertTrue(user.isEnable());
    }
    
    @Test
    public void testUpdateUserWithValidDepartmentAndSeniorRole() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        dto.setDepartmentId(2);
        dto.setRole("SENIOR");
        
        User user = new User();
        Department department = new Department();
        
        when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
        when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(department));
        
        boolean result = userService.updateUser(dto);
        
        assertFalse(result);
        verify(userRepository, times(1)).save(user);
        assertEquals(Role.SENIOR, user.getRole());
    }
    @Test
    public void testUpdateUserWithValidDepartmentAndJUNIORRole() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        dto.setDepartmentId(2);
        dto.setRole("JUNIOR");
        
        User user = new User();
        Department department = new Department();
        
        when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
        when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(department));
        
        boolean result = userService.updateUser(dto);
        
        assertFalse(result);
        verify(userRepository, times(1)).save(user);
        assertEquals(Role.JUNIOR, user.getRole());
    }
    @Test
    public void testUpdateUserWithValidDepartmentAndINTERVIEWERRole() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        dto.setDepartmentId(2);
        dto.setRole("INTERVIEWER");
        
        User user = new User();
        Department department = new Department();
        
        when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
        when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(department));
        
        boolean result = userService.updateUser(dto);
        
        assertFalse(result);
        verify(userRepository, times(1)).save(user);
        assertEquals(Role.INTERVIEWER, user.getRole());
    }
    // You can add similar tests for JUNIOR and INTERVIEWER roles following the above pattern.

    @Test
    public void testUpdateUserWithNoDepartment() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        dto.setDepartmentId(2);
        
        User user = new User();
        
        when(userRepository.findById(dto.getId())).thenReturn(Optional.of(user));
        when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.empty());
        
        boolean result = userService.updateUser(dto);
        
        assertTrue(result);
        verify(userRepository, never()).save(user);
    }

    @Test
    public void testUpdateUserWithNoUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setId(1);
        
        when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());
        
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> userService.updateUser(dto));
    }
}
