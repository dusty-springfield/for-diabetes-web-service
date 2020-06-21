package webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import webservice.models.record.RecordType;
import webservice.models.role.Role;
import webservice.models.user.User;
import webservice.repository.RecordTypeRepository;
import webservice.repository.RoleRepository;
import webservice.repository.UserRepository;

import java.util.Collections;

@Component
public class OnApplicationStartUp implements ApplicationRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecordTypeRepository recordTypeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!roleRepository.findAll().iterator().hasNext()) {
            preloadRoles();
        }

        if (!userRepository.findAll().iterator().hasNext()) {
            preloadAdmin();
        }

        if (!recordTypeRepository.findAll().iterator().hasNext()) {
            preloadRecordTypes();
        }
    }

    private void preloadRecordTypes() {
        RecordType cc = new RecordType("CARBOHYDRATE");
        RecordType basalInsulin = new RecordType("BASAL INSULIN");
        RecordType prandialInsulin = new RecordType("PRANDIAL INSULIN");
        RecordType glucose = new RecordType("GLUCOSE");

        RecordType[] types = {
                cc,
                glucose,
                basalInsulin,
                prandialInsulin
        };

        for (RecordType t : types) {
            recordTypeRepository.save(t);
        }
    }

    private void preloadAdmin() {
        String password = passwordEncoder.encode(adminPassword);
        User admin = new User(
                adminEmail, password,
                Collections.singletonList(roleRepository.findByName("ADMIN")), true
        );
        userRepository.save(admin);
    }

    private void preloadRoles() {

        Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");

        Role roleUser = new Role();
        roleUser.setName("USER");

        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);
    }
}
