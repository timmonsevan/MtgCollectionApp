package com.MTGCollectionApp.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * controller for login page
 */
@Controller
public class MTGCollectionLoginController {

    @GetMapping("/loginPage")
    public String showLoginPage() {

        return "Forms/LoginForm";
    }
}
