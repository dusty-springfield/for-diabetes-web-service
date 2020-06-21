package webservice.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import webservice.models.record.Record;
import webservice.models.user.User;
import webservice.repository.RecordRepository;
import webservice.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Test
    void getAllUserRecords() {
        for (User u : userRepository.findAll()) {
            for (Record r : u.getRecords()) {
                Optional<Record> optionalRecord = recordRepository.findById(r.getId());
                assertTrue(optionalRecord.isPresent());
                assertEquals(r.getUser(), optionalRecord.get().getUser());
            }
        }
    }
}