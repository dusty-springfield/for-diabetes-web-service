package webservice.controller.user;

import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import webservice.models.requests.ChangeEmailRequest;
import webservice.models.requests.ChangePasswordRequest;
import webservice.models.user.User;
import webservice.models.user.UserDetails;
import webservice.models.user.UserDetailsDto;
import webservice.models.user.UserDto;
import webservice.repository.UserDetailsRepository;
import webservice.repository.UserRepository;
import webservice.service.VerificationService;
import webservice.validator.UserDetailsValidator;
import webservice.validator.UserEmailValidator;
import webservice.validator.UserPasswordValidator;

import java.util.Date;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping(path = "/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    VerificationService verificationService;

    @Autowired
    UserDetailsValidator userDetailsValidator;

    @Autowired
    UserPasswordValidator passwordValidator;

    @Autowired
    UserEmailValidator emailValidator;

    /*
     * GET
     */
    @GetMapping()
    public @ResponseBody
    ResponseEntity getCurrentUser(Authentication authentication) {
        User u = userRepository.findByEmail(authentication.getName());
        if (u.getUserDetails() == null) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "User details is null.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = modelMapper.map(u, UserDto.class);
        userDto.setUserDetailsDto(modelMapper.map(u.getUserDetails(), UserDetailsDto.class));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /*
     * PUT
     */
    @PutMapping("/details")
    public @ResponseBody
    ResponseEntity<JSONObject> updateUserInfo(
            Authentication authentication,
            @RequestBody UserDetailsDto userDetailsDto
    ) {
        JSONObject jsonResponse = new JSONObject();
        User userFromDB = userRepository.findByEmail(authentication.getName());
        UserDetails userDetails = modelMapper.map(userDetailsDto, UserDetails.class);
        userDetails.setModified(new Date().getTime());

        DataBinder dataBinder = new DataBinder(userDetails);
        dataBinder.addValidators(userDetailsValidator);
        dataBinder.validate();
        if (dataBinder.getBindingResult().hasErrors()) {
            jsonResponse.put("status", "User details validation failed.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        if (userFromDB.getUserDetails() == null) {
            userFromDB.setUserDetails(userDetails);
            userRepository.save(userFromDB);
            jsonResponse.put("status", "New user details added to current user.");
        } else {
            BeanUtils.copyProperties(userDetails, userFromDB.getUserDetails(), "id", "user");
            userDetailsRepository.save(userFromDB.getUserDetails());
            jsonResponse.put("status", "Current user details updated.");
        }

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @PutMapping("/password")
    public @ResponseBody
    ResponseEntity<JSONObject> updateUserPassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request
    ) {
        JSONObject jsonResponse = new JSONObject();

        DataBinder passwordBinder = new DataBinder(request);
        passwordBinder.addValidators(passwordValidator);
        passwordBinder.validate();
        if (passwordBinder.getBindingResult().hasErrors()) {
            jsonResponse.put("status", "Password validation failed.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(authentication.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        jsonResponse.put("status", "User password updated");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @PutMapping("/email")
    public @ResponseBody
    ResponseEntity<JSONObject> requestUpdateUserEmail(
            Authentication authentication,
            @RequestBody ChangeEmailRequest request
    ) {
        JSONObject jsonResponse = new JSONObject();

        DataBinder emailBinder = new DataBinder(request);
        emailBinder.addValidators(emailValidator);
        emailBinder.validate();
        if (emailBinder.getBindingResult().hasErrors()) {
            jsonResponse.put("status", "Email validation failed");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            jsonResponse.put("status", "Email already registered");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(authentication.getName());
        try {
            verificationService.sendNewEmailConfirm(user, request.getEmail());
        } catch (TimeoutException e) {
            jsonResponse.put("status", e.getMessage());
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        jsonResponse.put("status", "New email requested");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }
}
