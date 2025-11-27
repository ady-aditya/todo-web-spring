package com.adi.todo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adi.todo.service.JwtService;
import com.adi.todo.model.api.LoginRequest;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginRequest request) {
        return jwtService.checkUserAndGenerateToken(request.getEmail(),request.getUid());
    }   
}
