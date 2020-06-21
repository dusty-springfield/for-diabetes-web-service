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
import webservice.models.record.Record;
import webservice.models.record.RecordDto;
import webservice.models.record.RecordType;
import webservice.models.record.RecordTypeInfo;
import webservice.models.user.User;
import webservice.repository.RecordRepository;
import webservice.repository.RecordTypeRepository;
import webservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/admin/record")
public class AdminRecordController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RecordTypeRepository recordTypeRepository;

    @Autowired
    RecordRepository recordRepository;

    private Logger logger = LoggerFactory.getLogger(AdminRecordController.class);

    @GetMapping("/type")
    public @ResponseBody
    ResponseEntity<List<RecordTypeInfo>> getAllRecordTypes() {
        logger.info("Admin requested all record types");

        List<RecordTypeInfo> res = new ArrayList<>();
        for (RecordType rt : recordTypeRepository.findAll()) {
            res.add(modelMapper.map(rt, RecordTypeInfo.class));
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/type/{id}")
    public @ResponseBody
    ResponseEntity getRecordType(@PathVariable("id") int id, @PathVariable("id") RecordType rt) {
        logger.info("Admin requested record type with ID = " + id);

        if (rt == null) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "Record type with ID = " + id + " was not found.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(modelMapper.map(rt, RecordTypeInfo.class), HttpStatus.OK);
    }

    @GetMapping()
    public @ResponseBody
    ResponseEntity<List<RecordDto>> getAllRecords() {
        logger.info("Admin requested all records");

        List<RecordDto> res = new ArrayList<>();
        for (Record r : recordRepository.findAll()) {
            RecordDto rd = modelMapper.map(r, RecordDto.class);
            rd.setRecordTypeId(r.getRecordType().getId());
            res.add(rd);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity getRecord(@PathVariable("id") Long id, @PathVariable("id") Record record) {
        logger.info("Admin requested record with ID = " + id);

        if (record == null) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "Record with Id = " + id + " was not found.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);

        }
        RecordDto rd = modelMapper.map(record, RecordDto.class);
        rd.setRecordTypeId(record.getRecordType().getId());
        return new ResponseEntity<>(rd, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public @ResponseBody
    ResponseEntity getAllUserRecords(@PathVariable("id") Long id, @PathVariable("id") User user) {
        logger.info("Admin requested all records of user with ID = " + id);

        if (user == null) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "User with Id = " + id + " was not found");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        List<RecordDto> records = new ArrayList<>();
        for (Record r : recordRepository.findByUser(user)) {
            RecordDto rd = modelMapper.map(r, RecordDto.class);
            rd.setRecordTypeId(r.getRecordType().getId());
            records.add(rd);
        }
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
}
