package webservice.controller.all;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping
    public @ResponseBody
    ResponseEntity<JSONObject> registerUser(){
        JSONObject response = new JSONObject();
        response.put("status", "Server is runnig");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
