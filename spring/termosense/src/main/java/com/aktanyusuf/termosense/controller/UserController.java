package com.aktanyusuf.termosense.controller;

import com.aktanyusuf.termosense.model.User;
import com.aktanyusuf.termosense.service.UserService;
import com.aktanyusuf.termosense.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "false");
            response.put("message", "User already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User newUser = userService.registerUser(user);
        String token = sessionManager.createSession(newUser.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("status", "true");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginDetails) {
        String email = loginDetails.get("email");
        String password = loginDetails.get("password");

        User user = userService.findByEmailAndPassword(email, password);

        if (user != null) {
            String token = sessionManager.createSession(email);
            Map<String, String> response = new HashMap<>();
            response.put("status", "true");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "false");
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
