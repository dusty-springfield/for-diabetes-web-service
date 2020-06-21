package webservice.controller.all;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import webservice.models.requests.AuthenticationRequest;
import webservice.service.CustomUserDetailsService;
import webservice.service.JwtService;

@Controller
@RequestMapping(path = "/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtService jwtService;

    @PostMapping
    public @ResponseBody
    ResponseEntity<JSONObject> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        JSONObject jsonResponse = new JSONObject();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }catch (BadCredentialsException e){
            jsonResponse.put("status", "Authentication failed.");
            return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtService.generateToken(userDetails);

        jsonResponse.put("jwt", jwt);
        return ResponseEntity.ok(jsonResponse);
    }
}
