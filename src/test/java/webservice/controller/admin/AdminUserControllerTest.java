package webservice.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import webservice.models.user.User;
import webservice.models.user.UserDto;
import webservice.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdminUserControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        AdminUserController controller = new AdminUserController();
        ResponseEntity<List<UserDto>> response = controller.getAllUsers();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        for (UserDto u : response.getBody()) {
            User dbUSer = userRepository.findByEmail(u.getEmail());
            assertNotNull(dbUSer);
        }
    }

    @Test
    void getUserById() {
        AdminUserController controller = new AdminUserController();
        for (User u : userRepository.findAll()) {
            ResponseEntity<?> response = controller.getUserById(u.getId(), u);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(((UserDto) response.getBody()).getId(), u.getId());
        }
    }
}