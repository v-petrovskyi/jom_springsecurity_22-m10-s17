package com.softserve.itacademy.controller;

import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.security.UserDetailsServiceImpl;
import com.softserve.itacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public HomeController(UserService userService, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping({"/", "home"})
    public String home(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "home";
    }
}
