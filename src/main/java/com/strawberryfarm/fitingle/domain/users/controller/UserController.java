package com.strawberryfarm.fitingle.domain.users.controller;

import com.strawberryfarm.fitingle.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/auth",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signUp() {
        userService.SignUp();
    }

    @PostMapping("/signIn")
    public void signIn() {
        userService.signIn();
    }

    @PostMapping("/signOut")
    public void signOut() {
        userService.signOut();
    }

}
