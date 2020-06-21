package webservice.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webservice.models.requests.ChangeEmailRequest;
import webservice.models.requests.ChangePasswordRequest;
import webservice.models.user.User;

@Service
public class UserValidator implements Validator {
    @Autowired
    UserEmailValidator emailValidator;

    @Autowired
    UserPasswordValidator passwordValidator;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            errors.reject("object", "null");
        }
        User u = (User) o;
        ChangePasswordRequest password = new ChangePasswordRequest(u.getPassword());
        ChangeEmailRequest email = new ChangeEmailRequest(u.getEmail());

        DataBinder passwordBinder = new DataBinder(password);
        passwordBinder.addValidators(passwordValidator);
        passwordBinder.validate();

        DataBinder emailBinder = new DataBinder(email);
        emailBinder.addValidators(emailValidator);
        emailBinder.validate();

        if (emailBinder.getBindingResult().hasErrors()) {
            errors.reject("email", "validation failed");
        }

        if (passwordBinder.getBindingResult().hasErrors()) {
            errors.reject("password", "validation failed");
        }
    }
}
