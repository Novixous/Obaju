package com.obaju.util;

import com.obaju.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component //annotation
public class EmailValidator {
    @Autowired
    UserService userService;

    public boolean valid(String email) {
        //Apache Common Validator
        return email.matches("\\w+@\\w+[.]\\w+");
    }

    public boolean isEmailExisted(String email) {
        if (userService.getUserEmail(email) != null) return true;
        return false;
    }
}
