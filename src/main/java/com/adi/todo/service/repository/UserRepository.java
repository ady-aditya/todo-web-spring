package com.adi.todo.service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.adi.todo.model.entity.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String email);
}
