package com.obaju.util;

import com.obaju.model.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserFormValidator implements Validator {
    @Autowired
    EmailValidator emailValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserForm user = (UserForm) target;
        if (user.getName().length() < 3) {
            errors.rejectValue("name", "invalid.name",
                    "Email error");
        }
        if (!emailValidator.valid(user.getEmail())) {
            errors.rejectValue("email", "invalid.email",
                    "Email error");
        }
        if (emailValidator.isEmailExisted(user.getEmail())) {
            errors.rejectValue("email", "existing.email",
                    "Email error");
        }
        if (user.getPassword().length() < 3) {
            errors.rejectValue("password", "invalid.password",
                    "Password error");
        }

    }
}
