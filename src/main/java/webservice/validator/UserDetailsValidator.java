package webservice.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webservice.models.user.UserDetails;

@Service
public class UserDetailsValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return UserDetails.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            errors.reject("object", "null");
        }

        UserDetails u = (UserDetails) o;

        if (u.getName().isEmpty()) {
            errors.reject("name", "value is empty");
        }

        if (u.getSurname().isEmpty()) {
            errors.reject("surname", "value is empty");
        }

        if (u.getWeight() < 0) {
            errors.reject("weight", "negative value");
        }

        if (u.getYearOfBirth() < 0) {
            errors.reject("year of birth", "negative value");
        }

    }
}
