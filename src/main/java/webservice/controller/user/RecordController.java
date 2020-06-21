package webservice.controller.user;

import net.minidev.json.JSONObject;
import org.apache.tika.parser.txt.CharsetDetector;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import webservice.models.record.*;
import webservice.models.user.User;
import webservice.repository.RecordRepository;
import webservice.repository.RecordTypeRepository;
import webservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/user/record")
public class RecordController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecordRepository recordRepository;

    @Autowired
    RecordTypeRepository recordTypeRepository;

    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(RecordController.class);

    /*
     * GET
     */
    @GetMapping()
    public @ResponseBody
    ResponseEntity<List<RecordDto>> getAllUserRecords(Authentication authentication) {
        User u = userRepository.findByEmail(authentication.getName());
        List<RecordDto> records = new ArrayList<>();
        for (Record r : u.getRecords()) {
            RecordDto dto = modelMapper.map(r, RecordDto.class);
            dto.setRecordTypeId(r.getRecordType().getId());
            records.add(dto);
        }
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    /*
     * POST
     */
    @PostMapping()
    public @ResponseBody
    ResponseEntity<JSONObject> postRecord(Authentication authentication, @RequestBody RecordPostRequest recordPostRequest) {
        logger.info("Description : " + recordPostRequest.getDescription());
        CharsetDetector detector = new CharsetDetector();
        detector.setText(recordPostRequest.getDescription().getBytes());
        logger.info(detector.detect().getName());  // <- return the result, you can check by .getName() method
        JSONObject jsonResponse = new JSONObject();
        Optional<RecordType> optional = recordTypeRepository.findById(recordPostRequest.getRecordTypeId());
        if (!optional.isPresent()) {
            jsonResponse.put("status", "Record type with ID = " + recordPostRequest.getRecordTypeId() + " was not found ");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        RecordType type = optional.get();
        User u = userRepository.findByEmail(authentication.getName());
        Record record = new Record(u, type, recordPostRequest.getValue(), recordPostRequest.getDate(), recordPostRequest.getDescription());
        recordRepository.save(record);
        jsonResponse.put("status", "Record added");
        jsonResponse.put("id", record.getId());
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    /*
     * PUT
     */
    @PutMapping("/{id}")
    public @ResponseBody
    ResponseEntity<JSONObject> putRecord(
            Authentication authentication, @PathVariable("id") Long id,
            @PathVariable("id") Record record, @RequestBody UpdateRecordRequest updateRecordRequest
    ) {
        JSONObject jsonResponse = new JSONObject();
        User u = userRepository.findByEmail(authentication.getName());
        if (record == null || !record.getUser().equals(u)) {
            jsonResponse.put("status", "Record with ID = " + id + " was not found for this user.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        record.setValue(updateRecordRequest.getValue());
        record.setDate(updateRecordRequest.getDate());
        record.setDescription(updateRecordRequest.getDescription());
        recordRepository.save(record);
        jsonResponse.put("status", "Record value updated.");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    /*
     * DELETE
     */

    @DeleteMapping
    public @ResponseBody
    ResponseEntity<JSONObject> deleteAllUserRecords(Authentication authentication) {
        JSONObject jsonResponse = new JSONObject();
        User u = userRepository.findByEmail(authentication.getName());
        recordRepository.deleteAll(u.getRecords());
        jsonResponse.put("status", "Ok");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public @ResponseBody
    ResponseEntity<JSONObject> deleteUserRecord(
            Authentication authentication, @PathVariable("id") Record record,
            @PathVariable("id") long id
    ) {
        JSONObject jsonResponse = new JSONObject();
        User u = userRepository.findByEmail(authentication.getName());
        if (record == null || !record.getUser().equals(u)) {
            jsonResponse.put("status", "Record with ID = " + id + " was not found for this user.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }
        recordRepository.delete(record);
        jsonResponse.put("status", "Ok");
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

}
