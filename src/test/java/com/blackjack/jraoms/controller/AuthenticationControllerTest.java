package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationControllerTest {

    @InjectMocks AuthenticationController authenticationController;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock ModelMap modelMap;
    @Mock HttpSession session;
    @Mock Principal principal;

    @Test
    public void testLoginWithPrincipalIsNotNull() {
        // Arrange
        Principal principal = mock(Principal.class);

        // Act
        String viewName = authenticationController.login(principal);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/dashboard");
    }

    @Test
    public void testVerify() {
        String expectedViewName = "/auth/verifyEmail";
        String actualViewName = authenticationController.verify();
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testVerifyMail_UserNotFound() {
        String email = "test@example.com";

        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        String viewName = authenticationController.verifyMail(email, session, new ModelMap());

        verify(userService).findByEmail(email);
        verify(session).setAttribute("message", "Invaild");
        assertEquals("redirect:/auth/verifyEmailToResetPassword", viewName);
    }

    @Test
    public void testVerifyMail_UserFound() {
        String email = "test@example.com";
        String code = "123456";

        User mockUser = new User();
        mockUser.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userService.sendCodeToResetPassword(email)).thenReturn(code);
        String viewName = authenticationController.verifyMail(email, session, modelMap);

        verify(userService).findByEmail(email);
        verify(userService).sendCodeToResetPassword(email);
        verify(session).setAttribute(email, code);
        verify(modelMap).addAttribute("email", email);
        assertEquals("/auth/verifyCode", viewName);
    }

    @Test
    public void testVerifyOtp_CorrectCode() {
        String inputCode = "123456";
        String email = "test@example.com";

        when(session.getAttribute(email)).thenReturn(inputCode);

        String viewName = authenticationController.verifyOtp(inputCode, email, session, modelMap);

        verify(session).getAttribute(email);
        verify(modelMap).addAttribute("email", email);
        assertEquals("/auth/setNewPassword", viewName);
    }

    @Test
    public void testVerifyOtp_WrongCode() {
        String inputCode = "123456";
        String email = "test@example.com";
        String realVerifyCode = "654321"; // Wrong code

        when(session.getAttribute(email)).thenReturn(realVerifyCode);

        String viewName = authenticationController.verifyOtp(inputCode, email, session, modelMap);

        verify(session).getAttribute(email);
        verify(modelMap).addAttribute("email", email);
        verify(session).setAttribute("message", "wrongCode");
        assertEquals("/auth/verifyCode", viewName);
    }

    @Test
    public void testVerifyOtp_RealCodeNull() {
        String inputCode = "123456";
        String email = "test@example.com";

        when(session.getAttribute(email)).thenReturn(null);
        String viewName = authenticationController.verifyOtp(inputCode, email, session, modelMap);

        verify(session).getAttribute(email);
        verify(modelMap).addAttribute("email", email);
        verify(session).setAttribute("message", "wrongCode");
        assertEquals("/auth/verifyCode", viewName);
    }

    @Test
    public void testClearSession() {
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("message", "someMessage");

        when(session.getAttribute("message")).thenReturn("someMessage");
        when(session.getId()).thenReturn(mockSession.getId());

        String jsonResponse = authenticationController.clearSession(session);

        verify(session).removeAttribute("message");
        assertEquals("{\"status\": \"okey\"}", jsonResponse);
    }

    @Test
    public void testResendOtp() {
        String email = "test@example.com";
        String code = "newOtpCode";

        MockHttpSession mockSession = new MockHttpSession();

        when(userService.sendCodeToResetPassword(email)).thenReturn(code);
        when(session.getId()).thenReturn(mockSession.getId());

        String jsonResponse = authenticationController.resendOtp(session, email);

        verify(userService).sendCodeToResetPassword(email);
        verify(session).setAttribute(email, code);
        assertEquals("{\"status\": \"okey\"}", jsonResponse);
    }

    @Test
    void testNewPassword_PasswordMatches() {
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        MockHttpSession session = new MockHttpSession();
        ModelMap model = new ModelMap();

        when(userService.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        String viewName = authenticationController.newPassword(email, password, session, model);
        assertEquals("/auth/setNewPassword", viewName);
        assertEquals(email, model.get("email"));
    }

    @Test
    void testNewPassword_PasswordDoesNotMatch() {
        String email = "test@example.com";
        String password = "newPassword123";
        String encodedPassword = "encodedOldPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        MockHttpSession session = new MockHttpSession();
        ModelMap model = new ModelMap();

        when(userService.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        String viewName = authenticationController.newPassword(email, password, session, model);

        verify(userService, times(1)).saveEntity(user);
        assertEquals("redirect:/login", viewName);
    }

}