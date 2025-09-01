package com.example.PollApp.controller;

import com.example.PollApp.domain.User;
import com.example.PollApp.service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private PollManager pollManager;

    @PostMapping
    public void createUser(@RequestBody User user) {
        pollManager.addUser(user.getId(), user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return pollManager.getAllUsers();
    }
}
