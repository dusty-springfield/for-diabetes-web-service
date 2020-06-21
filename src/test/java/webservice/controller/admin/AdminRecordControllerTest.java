package webservice.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import webservice.models.record.Record;
import webservice.models.record.RecordDto;
import webservice.models.record.RecordType;
import webservice.models.record.RecordTypeInfo;
import webservice.repository.RecordRepository;
import webservice.repository.RecordTypeRepository;
import webservice.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminRecordControllerTest {
    @Autowired
    private RecordTypeRepository recordTypeRepository;

    @Autowired
    private AdminRecordController adminRecordController;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllRecordTypes() {
        for (RecordTypeInfo r : adminRecordController.getAllRecordTypes().getBody()) {
            Optional<RecordType> optional = recordTypeRepository.findById(r.getId());
            assertTrue(optional.isPresent());
            RecordType recordType = optional.get();
            assertEquals(r.getType(), recordType.getType());
            assertEquals(r.getId(), recordType.getId());
        }
    }

    @Test
    void getRecord() {
        for (RecordDto r : adminRecordController.getAllRecords().getBody()) {
            Optional<Record> optionalRecord = recordRepository.findById(r.getId());
            assertTrue(optionalRecord.isPresent());
            Record record = optionalRecord.get();
            assertEquals(record.getId(), r.getId());
            assertEquals(record.getDate(), r.getDate());
            assertEquals(record.getValue(), r.getValue());
        }
    }
}