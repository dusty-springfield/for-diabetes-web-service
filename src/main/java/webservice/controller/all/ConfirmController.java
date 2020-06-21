package webservice.controller.all;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import webservice.models.user.User;
import webservice.models.verification.VerificationToken;
import webservice.models.verification.VerificationTokenType;
import webservice.repository.UserRepository;
import webservice.service.VerificationService;

@Controller
@RequestMapping(path = "/confirm")
public class ConfirmController {
    @Autowired
    VerificationService verificationService;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/registration/{token}", produces = "text/html;charset=UTF-8")
    public String confirmRegistration(@PathVariable("token") String token) {
        VerificationToken vToken = verificationService.validateVerificationToken(
                token, VerificationTokenType.REGISTRATION_CONFIRMATION
        );

        if (vToken != null) {
            User u = vToken.getUser();
            u.setEnabled(true);
            userRepository.save(u);
            return "confirm_reg_succ";
        } else {
            return "confirm_reg_fail";
        }
    }

    @GetMapping("/password/{token}")
    public String confirmResetPassword(@PathVariable("token") String token) {
        VerificationToken verificationToken = verificationService.validateVerificationToken(
                token, VerificationTokenType.PASSWORD_RESET
        );

        if (verificationToken != null) {
            User u = verificationToken.getUser();
            if (u == null) {
                throw new RuntimeException("Attempt to change user password failed. User is null");
            }
            u.setPassword(verificationToken.getCredential());
            userRepository.save(u);
            return "confirm_pswd_succ";
        } else {
            return "confirm_pswd_fail";
        }
    }

    @GetMapping("/email/{token}")
    public String confirmEmail(@PathVariable("token") String token) {
        VerificationToken verificationToken = verificationService.validateVerificationToken(
                token, VerificationTokenType.EMAIL_CHANGING
        );

        if (verificationToken != null) {
            User u = verificationToken.getUser();
            if (u == null) {
                throw new RuntimeException("Attempt to change email failed. User is null");
            }
            u.setEmail(verificationToken.getCredential());
            userRepository.save(u);
            return "confirm_email_succ";
        } else {
            return "confirm_email_fail";
        }
    }
}
