package com.adi.todo.service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;

import com.adi.todo.model.entity.Day;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DayRepository extends ReactiveCrudRepository<Day, Long> {
    Mono<Day> findByUserEmailAndDate(String userEmail, String date);

    Flux<Day> findByUserEmailOrderByCreatedDateDesc(String userEmail);

    // Flux<Day> findByUserEmailOrderByCreatedDateDesc(String userEmail, Pageable
    // pageable);
    @Query(value = "SELECT * FROM day WHERE user_email = :userEmail ORDER BY created_date DESC LIMIT :limit")
    Flux<Day> findByUserEmailOrderByCreatedDateDescLimit(String userEmail, int limit);

}
