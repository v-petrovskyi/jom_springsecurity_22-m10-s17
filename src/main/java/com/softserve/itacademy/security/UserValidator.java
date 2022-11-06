package com.softserve.itacademy.security;

import com.softserve.itacademy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserDetailsServiceImpl userDetailsService;
    @Autowired
    public UserValidator(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        try {
            userDetailsService.loadUserByUsername(user.getUsername());
        } catch (Exception e) {
            return;
        }
        errors.rejectValue("email", "", "User with this email already exists");
    }
}
