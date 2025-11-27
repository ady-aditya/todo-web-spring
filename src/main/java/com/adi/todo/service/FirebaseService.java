package com.adi.todo.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import java.util.List;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FirebaseService {
    
    public Mono<UserRecord> getUserByEmail(String email) {
        return Mono.fromCallable(() -> {
            try {
                return FirebaseAuth.getInstance().getUserByEmail(email);
            } catch (FirebaseAuthException e) {
                return null;
            }
        });
    }

    public Mono<ListUsersPage> getAllUsers() {
        return Mono.fromCallable(() -> {
            try {
                return FirebaseAuth.getInstance().listUsers(null);
            } catch (FirebaseAuthException e) {
                return null;
            }
        });
    }
} 