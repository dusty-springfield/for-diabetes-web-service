package webservice.controller.all;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import webservice.models.requests.AuthenticationRequest;
import webservice.models.user.User;
import webservice.repository.RoleRepository;
import webservice.repository.UserRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuthenticationControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User admin;

    @Test
    void adminPersistence() {
        Set<User> admins = roleRepository.findByName("ROLE_ADMIN").getUsers();
        assertFalse(admins.isEmpty());
        admin = admins.iterator().next();
    }

    @Test
    public void createAuthenticationToken() {
        AuthenticationController controller = new AuthenticationController();
        for (User u : userRepository.findAll()) {
            String email = u.getEmail();
            String pswd = u.getPassword();

            ResponseEntity<JSONObject> response = controller.createAuthenticationToken(
                    new AuthenticationRequest(email, pswd)
            );
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertFalse(response.getBody().getAsString("jwt").isEmpty());

            response = controller.createAuthenticationToken(
                    new AuthenticationRequest(email.substring(1), pswd)
            );
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

            response = controller.createAuthenticationToken(
                    new AuthenticationRequest(email, pswd.substring(1))
            );
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }
}