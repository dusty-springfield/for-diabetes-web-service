package webservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webservice.models.user.User;
import webservice.models.user.UserDetails;
import webservice.models.verification.VerificationToken;
import webservice.models.verification.VerificationTokenType;
import webservice.repository.UserRepository;
import webservice.repository.VerificationTokenRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class VerificationService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${hostname}")
    private String hostname;

    private final String REGISTRATION_CONFIRM_MESSAGE_TEXT;

    private final String NEW_EMAIL_CONFIRM_MESSAGE_TEXT;

    private final String NEW_PASSWORD_CONFIRM_MESSAGE_TEXT;

    private final String MESSAGE_SUBJECT;

    private Logger logger = LoggerFactory.getLogger(VerificationService.class);

    public VerificationService() {
        MESSAGE_SUBJECT = "4Diabetes. %s";
        String prefix = "http://%s/confirm/";
        REGISTRATION_CONFIRM_MESSAGE_TEXT = "Чтобы завершить регистрацию, перейдите по ссылке:\n" + prefix + "registration/%s";
        NEW_EMAIL_CONFIRM_MESSAGE_TEXT = "Чтобы изметь адрес электронной почты, перейдите по ссылке:\n" + prefix + "email/%s";
        NEW_PASSWORD_CONFIRM_MESSAGE_TEXT = "Чтобы сбросить пароль перейдите по ссылке:\n" + prefix + "password/%s" + "\n\nВаш новый пароль : %s" +
                "\n\nВ любое время вы можете изменить его в настройках приложения";
    }

    private String generateNewToken(User user, String credential, VerificationTokenType tokenType) throws TimeoutException {
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = tokenRepository.findByUser(user);
        if (vToken == null) {
            vToken = new VerificationToken(user, token, credential, tokenType);
        } else if (checkTimeDelta(vToken)) {
            vToken.setToken(token);
            vToken.setCredential(credential);
            vToken.setExpiryDate(vToken.calculateExpiryDate());
        } else {
            throw new TimeoutException("Too early for making new request. Wait a bit longer.");
        }
        tokenRepository.save(vToken);
        return token;
    }

    private boolean checkTimeDelta(VerificationToken token) {
        Date now = new Date();

        if (now.getTime() - token.getExpiryDate().getTime() > 0) {
            return true;
        }

        Calendar deltaTime = Calendar.getInstance();
        deltaTime.setTime(token.getExpiryDate());
        int minutesFromExpiryDate = -1 * VerificationToken.getExpiration() + VerificationToken.getTimeout();
        deltaTime.add(Calendar.MINUTE, minutesFromExpiryDate);

        Date deltaDate = deltaTime.getTime();

        boolean res = true;
        if (now.getTime() - deltaDate.getTime() < 0) {
            res = false;
            User u = token.getUser();
            logger.warn(String.format("User with ID = %d asks for new token too frequently", u.getId()));
        }
        return res;
    }

    public VerificationToken validateVerificationToken(String token, VerificationTokenType tokenType) {
        VerificationToken verificationToken = tokenRepository.findByTokenAndTokenType(token, tokenType);
        if (verificationToken == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return null;
        }

        tokenRepository.delete(verificationToken);
        return verificationToken;
    }

    public void deleteExpiredTokens(Date now) {
        int n = 0;
        for (VerificationToken expiredToken : tokenRepository.findAllByExpiryDateIsLessThan(now)) {
            User u = expiredToken.getUser();
            if (u != null && !u.isEnabled()) {
                userRepository.delete(u);
            }
            tokenRepository.delete(expiredToken);
            ++n;
        }

        if (n != 0) {
            logger.warn(String.format("%d expired tokens were deleted", n));
        } else {
            logger.info("There were no expired tokens");
        }
    }

    public void sendRegistrationVerification(User user) throws TimeoutException {
        String token = generateNewToken(user, "", VerificationTokenType.REGISTRATION_CONFIRMATION);
        SimpleMailMessage mailMessage = formMailMessage(
                user.getEmail(),
                String.format(MESSAGE_SUBJECT, "Registration"),
                String.format(REGISTRATION_CONFIRM_MESSAGE_TEXT, hostname, token)
        );

        mailSender.send(mailMessage);
    }

    public void sendNewEmailConfirm(User user, String newEmail) throws TimeoutException {
        String token = generateNewToken(user, newEmail, VerificationTokenType.EMAIL_CHANGING);
        String textFormat;
        UserDetails ud = user.getUserDetails();
        if (ud != null) {
            textFormat = ud.getSurname() + " " + ud.getName() + ",\n" + NEW_EMAIL_CONFIRM_MESSAGE_TEXT;
        } else {
            textFormat = NEW_EMAIL_CONFIRM_MESSAGE_TEXT;
        }
        SimpleMailMessage mailMessage = formMailMessage(
                newEmail,
                String.format(MESSAGE_SUBJECT, "Change Email"),
                String.format(textFormat, hostname, token)
        );
        mailSender.send(mailMessage);
    }

    public void sendResetPasswordConfirm(User user, String password) throws TimeoutException {
        String encoded = passwordEncoder.encode(password);
        String token = generateNewToken(user, encoded, VerificationTokenType.PASSWORD_RESET);
        String textFormat;
        UserDetails ud = user.getUserDetails();
        if (ud != null) {
            textFormat = ud.getSurname() + " " + ud.getName() + ",\n" + NEW_PASSWORD_CONFIRM_MESSAGE_TEXT;
        } else {
            textFormat = NEW_PASSWORD_CONFIRM_MESSAGE_TEXT;
        }

        SimpleMailMessage mailMessage = formMailMessage(
                user.getEmail(),
                String.format(MESSAGE_SUBJECT, "Reset Password"),
                String.format(textFormat, hostname, token, password)
        );

        mailSender.send(mailMessage);
    }

    public User getUser(String token) {
        VerificationToken vToken = tokenRepository.findByToken(token);
        if (vToken == null) {
            return null;
        }
        return vToken.getUser();
    }

    public void deleteVerificationToken(VerificationToken verificationToken) {
        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }
    }

    private SimpleMailMessage formMailMessage(String sendTo, String mailSubject, String mailText) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(mailSubject);
        mailMessage.setFrom("4Diabets");
        mailMessage.setTo(sendTo);
        mailMessage.setText(mailText);
        return mailMessage;
    }
}
