package com.blackjack.jraoms.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;
    
    @Test
    public void testValidEmailAddress() {
        assertTrue(mailService.validateEmailAddress("test@example.com"));
    }

    @Test
    public void testInvalidEmailAddress() {
        assertFalse(mailService.validateEmailAddress("invalid-email"));
    }

    @Test
    public void testNullEmailAddress() {
        assertFalse(mailService.validateEmailAddress(null));
    }

    @Test
    public void testEmptyEmailAddress() {
        assertFalse(mailService.validateEmailAddress(""));
    }

    @Test
    public void testSendPasswordEmailTemplate() {
        String randomNumber = "123456";
        String expectedTemplate = expectedSendPasswordEmailTemplate(randomNumber);
        String generatedTemplate = mailService.sendPasswordEmailTemplate(randomNumber);
        assertEquals(expectedTemplate, generatedTemplate);
    }

    @Test
    public void testSendOTPEmailTemplate() {
        String randomNumber = "123456";
        String expectedTemplate = expectedSendOTPEmailTemplate(randomNumber);
        String generatedTemplate = mailService.sendOTPEmailTemplate(randomNumber);
        assertEquals(expectedTemplate, generatedTemplate);
    }
    
    @Test
    public void testSendOTPResetPasswordTemplate() {
        String randomNumber = "123456";
        String expectedTemplate = expectedSendOTPResetPasswordTemplate(randomNumber);
        String generatedTemplate = mailService.sendOTPResetPasswordTemplate(randomNumber);
        assertEquals(expectedTemplate, generatedTemplate);
    }

    public String expectedSendPasswordEmailTemplate(String randomNumber) {

        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "\n"
                + "<head>\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>Verify your login</title>\n"
                + "  <!--[if mso]><style type=\"text/css\">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style><![endif]-->\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n"
                + "  <table role=\"presentation\"\n"
                + "    style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n"
                + "    <tbody>\n"
                + "      <tr>\n"
                + "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n"
                + "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n"
                + "            <tbody>\n"
                + "              <tr>\n"
                + "                <td style=\"padding: 40px 0px 0px;\">\n"
                + "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n"
                + "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n"
                + "                      <h1 style=\"margin: 1rem 0\">Login Password</h1>\n"
                + "                      <p style=\"padding-bottom: 16px\">Please use the verification code below to sign in.<br>You can change your password later.\n"
                + "                      </p>\n"
                + "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 130%\">"+randomNumber+"</strong></p>\n"
                + "                    </div>\n"
                + "                  </div>\n"
                + "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n"
                + "                    <p style=\"padding-bottom: 16px\"> ACE Data Systems Ltd.</p>\n"
                + "                  </div>\n"
                + "                </td>\n"
                + "              </tr>\n"
                + "            </tbody>\n"
                + "          </table>\n"
                + "        </td>\n"
                + "      </tr>\n"
                + "    </tbody>\n"
                + "  </table>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
    }
    
    
    public String expectedSendOTPEmailTemplate(String randomNumber) {

        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "\n"
                + "<head>\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>Verify your login</title>\n"
                + "  <!--[if mso]><style type=\"text/css\">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style><![endif]-->\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n"
                + "  <table role=\"presentation\"\n"
                + "    style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n"
                + "    <tbody>\n"
                + "      <tr>\n"
                + "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n"
                + "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n"
                + "            <tbody>\n"
                + "              <tr>\n"
                + "                <td style=\"padding: 40px 0px 0px;\">\n"
                + "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n"
                + "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n"
                + "                      <h1 style=\"margin: 1rem 0\">Email verification Code</h1>\n"
                + "                      <p style=\"padding-bottom: 16px\">Please enter OTP code to Edit Email<br>This code will expire when you request another OTP <or> Logout from our Web.\n"
                + "                      </p>\n"
                + "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 130%\">"+randomNumber+"</strong></p>\n"
                + "                    </div>\n"
                + "                  </div>\n"
                + "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n"
                + "                    <p style=\"padding-bottom: 16px\"> ACE Data Systems Ltd.</p>\n"
                + "                  </div>\n"
                + "                </td>\n"
                + "              </tr>\n"
                + "            </tbody>\n"
                + "          </table>\n"
                + "        </td>\n"
                + "      </tr>\n"
                + "    </tbody>\n"
                + "  </table>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
    }
    
    public String expectedSendOTPResetPasswordTemplate(String randomNumber) {

        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "\n"
                + "<head>\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>Verify your login</title>\n"
                + "  <!--[if mso]><style type=\"text/css\">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style><![endif]-->\n"
                + "</head>\n"
                + "\n"
                + "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n"
                + "  <table role=\"presentation\"\n"
                + "    style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n"
                + "    <tbody>\n"
                + "      <tr>\n"
                + "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n"
                + "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n"
                + "            <tbody>\n"
                + "              <tr>\n"
                + "                <td style=\"padding: 40px 0px 0px;\">\n"
                + "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n"
                + "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n"
                + "                      <h1 style=\"margin: 1rem 0\">Email verification Code</h1>\n"
                + "                      <p style=\"padding-bottom: 16px\">This is your Verification Code to Reset Password<br>This code will expire when you request another OTP <or> exit from Verify Code page.\n"
                + "                      </p>\n"
                + "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 130%\">"+randomNumber+"</strong></p>\n"
                + "                    </div>\n"
                + "                  </div>\n"
                + "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n"
                + "                    <p style=\"padding-bottom: 16px\"> ACE Data Systems Ltd.</p>\n"
                + "                  </div>\n"
                + "                </td>\n"
                + "              </tr>\n"
                + "            </tbody>\n"
                + "          </table>\n"
                + "        </td>\n"
                + "      </tr>\n"
                + "    </tbody>\n"
                + "  </table>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
    }
    

}