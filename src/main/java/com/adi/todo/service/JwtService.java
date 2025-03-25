package com.adi.todo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adi.todo.model.entity.User;
import com.adi.todo.model.exception.TodoAppException;
import com.adi.todo.service.repository.UserRepository;
import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import com.adi.todo.service.FirebaseAuthService;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:432000000}")  // 5 days in milliseconds
    private long jwtExpiration;

    @Autowired
    private UserRepository userRepository;

    private final FirebaseAuthService firebaseAuthService;

    public JwtService(UserRepository userRepository, FirebaseAuthService firebaseAuthService) {
        this.userRepository = userRepository;
        this.firebaseAuthService = firebaseAuthService;
    }

    public Mono<String> checkUserAndGenerateToken(String email) {
        return userRepository.findByEmail(email)
            .switchIfEmpty(checkFirebaseAndCreateUser(email))
            .map(this::generateToken);
    }

    private Mono<User> checkFirebaseAndCreateUser(String email) {
        return firebaseAuthService.getUserByEmail(email)
            .switchIfEmpty(Mono.error(new TodoAppException(HttpStatus.NOT_FOUND,"User not found in Firebase")))
            .flatMap(firebaseUser -> {
                User newUser = new User();
                newUser.setEmail(email);
                // Set other user properties from firebaseUser if needed
                return userRepository.save(newUser);
            });
    }

    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    public String validateTokenAndGetEmail(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new TodoAppException(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
        }
    }
}

