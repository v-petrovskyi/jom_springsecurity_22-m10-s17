package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.UserDetailsServiceImpl;
import com.softserve.itacademy.security.UserRegistrationService;
import com.softserve.itacademy.security.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private final UserValidator userValidator;
    private final UserRegistrationService userRegistrationService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public LoginController(UserValidator userValidator, UserRegistrationService userRegistrationService, UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRegistrationService = userRegistrationService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/form-login")
    public String loginPage() {
        return "login-page";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "registration";
    }

    @PostMapping("/registration")
    public String createAccount(@Validated @ModelAttribute("user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        userRegistrationService.register(user);
        return "redirect:/form-login";
    }

}
