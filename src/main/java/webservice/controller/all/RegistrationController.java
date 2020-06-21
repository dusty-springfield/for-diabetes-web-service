package webservice.controller.all;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import webservice.models.requests.AuthenticationRequest;
import webservice.models.user.User;
import webservice.models.user.UserEmail;
import webservice.repository.RoleRepository;
import webservice.repository.UserRepository;
import webservice.service.VerificationService;
import webservice.validator.UserValidator;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping(path = "/registration")
public class RegistrationController {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    VerificationService verificationService;

    @Autowired
    UserValidator userValidator;

    @PostMapping
    public @ResponseBody
    ResponseEntity registerUser(@RequestBody AuthenticationRequest request) {
        JSONObject jsonResponse = new JSONObject();
        String email = request.getEmail();
        String password = request.getPassword();

        User user = new User(email, password, Collections.singletonList(roleRepository.findByName("USER")), false);
        DataBinder dataBinder = new DataBinder(user);
        dataBinder.addValidators(userValidator);
        dataBinder.validate();

        if (dataBinder.getBindingResult().hasErrors()) {
            ObjectError error = dataBinder.getBindingResult().getAllErrors().get(0);
            jsonResponse.put("status", error.getCode() + " " + error.getDefaultMessage());
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            User existingUser = userRepository.findByEmail(email);

            if (existingUser != null && existingUser.isEnabled()) {
                jsonResponse.put("status", "email " + email + " already registered");
                return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);

            } else if (existingUser != null && !existingUser.isEnabled()) {
                verificationService.sendRegistrationVerification(existingUser);
                jsonResponse.put("status", "User " + email + " successfully resent confirmation.");
                return new ResponseEntity<>(jsonResponse, HttpStatus.OK);

            } else {

                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                verificationService.sendRegistrationVerification(user);
                jsonResponse.put("status", "User " + email + " successfully registered.");
                return ResponseEntity.ok(jsonResponse);
            }
        } catch (TimeoutException e) {
            jsonResponse.put("status", e.getMessage());
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/password")
    public @ResponseBody
    ResponseEntity<JSONObject> requestResetPassword(
            @RequestBody UserEmail email
    ) {
        JSONObject jsonResponse = new JSONObject();
        User user = userRepository.findByEmail(email.getEmail());
        if (user == null) {
            jsonResponse.put("status", "User with email " + email.getEmail() + " was no found.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        String password = RandomStringUtils.randomAlphanumeric(8, 10);
        try {
            verificationService.sendResetPasswordConfirm(user, password);
        } catch (TimeoutException e) {
            jsonResponse.put("status", e.getMessage());
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        jsonResponse.put("status", "Reset password requested");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }
}
