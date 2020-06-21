package webservice.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import webservice.models.user.User;
import webservice.models.user.UserDetails;
import webservice.repository.UserDetailsRepository;
import webservice.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Test
    void getCurrentUser() {
        for (User u : userRepository.findAll()) {
            UserDetails ud = u.getUserDetails();
            if (ud != null) {
                Optional<UserDetails> optionalUserDetails = userDetailsRepository.findById(ud.getId());
                assertTrue(optionalUserDetails.isPresent());
                UserDetails userDetails = optionalUserDetails.get();
                assertEquals(userDetails.getId(), ud.getId());
                assertEquals(userDetails.getUser().getId(), u.getId());
            }
        }
    }
}