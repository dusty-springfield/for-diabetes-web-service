package webservice.controller.admin;

import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webservice.models.user.User;
import webservice.models.user.UserDetailsDto;
import webservice.models.user.UserDto;
import webservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/admin/user")
public class AdminUserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(AdminUserController.class);


    /*
     * GET
     */
    @GetMapping()
    public @ResponseBody
    ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Admin requested all users");

        List<UserDto> users = new ArrayList<>();
        userRepository.findAll().forEach(
                u -> {
                    UserDto ud = modelMapper.map(u, UserDto.class);
                    if (u.getUserDetails() != null) {
                        ud.setUserDetailsDto(modelMapper.map(u.getUserDetails(), UserDetailsDto.class));
                    }
                    users.add(ud);
                });
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<?> getUserById(@PathVariable("id") long id, @PathVariable("id") User user) {
        logger.info(String.format("Admin requested user with ID = %d", id));

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("status", "User with Id = " + id + " was not found.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(modelMapper.map(user, UserDto.class), HttpStatus.OK);
    }
}
