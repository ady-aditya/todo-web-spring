package com.adi.todo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adi.todo.model.exception.TodoAppException;
import java.util.Date;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:432000000}")  // 5 days in milliseconds
    private long jwtExpiration;


    private final FirebaseService firebaseAuthService;

    public JwtService(FirebaseService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    public Mono<String> checkUserAndGenerateToken(String email, String uid) {
        return validateUserFromForebase(email,uid)
        .map(this::generateToken);

    }

    private Mono<String> validateUserFromForebase(String email, String uid) {
        return firebaseAuthService.getUserByEmail(email)
            .switchIfEmpty(Mono.error(new TodoAppException(HttpStatus.NOT_FOUND,"User not found in Firebase")))
            .flatMap(firebaseUser -> {
                if (!firebaseUser.getUid().equals(uid)) {
                    return Mono.error(new TodoAppException(HttpStatus.FORBIDDEN,"User Auth error"));
                }
                return Mono.just(email);
            });
    }

    private String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(email)
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

