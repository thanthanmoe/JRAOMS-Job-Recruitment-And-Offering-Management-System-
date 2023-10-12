package com.blackjack.jraoms.service;

import com.blackjack.jraoms.dto.EmailContactDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    //Email Validation
    public boolean validateEmailAddress(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public void send(EmailContactDto emailContactDto) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //Email Validation
            String email = emailContactDto.getEmail();
            if (!validateEmailAddress(email)) {
                throw new IllegalArgumentException("Invalid email address: " + email);
            }

            helper.setFrom("smith2004prime@gmail.com", "ACE Data System");
            helper.setTo(emailContactDto.getEmail());
            helper.setSubject(emailContactDto.getSubject());
            helper.setText(emailContactDto.getContent(), true);

            //Add attach file
            if (emailContactDto.getFile()!=null && !emailContactDto.getFile().isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(emailContactDto.getFile().getOriginalFilename()));
                InputStreamSource source = () -> {
                    // TODO Auto-generated method stub
                    return emailContactDto.getFile().getInputStream();
                };
                helper.addAttachment(fileName, source);
            }

            // Add CC email addresses
            if (emailContactDto.getCcEmail() != null && !emailContactDto.getCcEmail().isEmpty()) {
                String[] ccEmails = emailContactDto.getCcEmail().toArray(new String[0]);
                helper.setCc(ccEmails);
            }

            //Send Email
            javaMailSender.send(message);

        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid email address: {}", e.getMessage());
        }
    }

    //Login password email template
    public String sendPasswordEmailTemplate(String randomNumber) {

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
    
    //send OTP code
    public String sendOTPEmailTemplate(String randomNumber) {

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

    //send to Reset Password
    public String sendOTPResetPasswordTemplate(String randomNumber) {

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

    public String sendWelcomeMail(String candidateName,String vacancyName,String companyName){
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "\n"
                + "<head>\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>Welcome to the team [employee's name]</title>\n"
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
                + "                      <p style=\"padding-bottom: 16px\">Dear "+candidateName+",</p>\n"
                + "                      <p style=\"padding-bottom: 16px\">We hope this email finds you well. We would like to express our gratitude for your interest\n"
                + "                        in the "+vacancyName+" position at "+companyName+". We are writing to confirm that we have received your submitted CV\n"
                + "                        and application.Your credentials and experience are impressive, and we are eager to review your application thoroughly.\n"
                + "                        Our team will carefully assess all applications and shortlist candidates for the interview phase. We appreciate your\n"
                + "                        patience during this process.</p>\n"
                + "                      <p style=\"padding-bottom: 16px\">Best regards,<br>Admin Team<br>ACE Data Systems Ltd</p>\n"
                + "                    </div>\n"
                + "                  </div>\n"
                + "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n"
                + "                    <p style=\"padding-bottom: 16px\"><a href=\"http://www.ace.com\" target=\"_blank\"\n"
                + "                        style=\"color: inherit; text-decoration: underline\">www.ace.com</a></p>\n"
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

    public String contactUs(String candidateName, String candidateEmail, String message){
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "\n"
                + "<head>\n"
                + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>RSVP: One week left!</title>\n"
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
                + "                      <p style=\"padding-bottom: 16px\">Name : "+candidateName+"<br>Email : "+candidateEmail+"</p>\n"
                + "                      <p style=\"padding-bottom: 16px\">"+message+"</p>\n"
                + "                    </div>\n"
                + "                  </div>\n"
                + "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n"
                + "                    <p style=\"padding-bottom: 16px\"><a href=\"http://www.ace.com\" target=\"_blank\"\n"
                + "                        style=\"color: inherit; text-decoration: underline\">www.ace.com</a></p>\n"
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
