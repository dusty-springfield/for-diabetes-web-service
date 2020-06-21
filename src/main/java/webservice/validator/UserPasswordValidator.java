package webservice.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webservice.models.requests.ChangePasswordRequest;

@Service
public class UserPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePasswordRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            errors.reject("object", "null");
        }

        ChangePasswordRequest request = (ChangePasswordRequest) o;

        if (request.getPassword().length() < 8) {
            errors.reject("password", "value too short");
        }
    }
}
