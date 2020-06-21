package webservice.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webservice.models.requests.ChangeEmailRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserEmailValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ChangeEmailRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            errors.reject("object", "null");
        }

        ChangeEmailRequest request = (ChangeEmailRequest) o;

        Pattern p = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher m = p.matcher(request.getEmail());
        if (!m.matches()) {
            errors.reject("email", "value invalid");
        }
    }
}
