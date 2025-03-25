package com.adi.todo.service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.adi.todo.model.entity.Day;
import reactor.core.publisher.Mono;

public interface DayRepository extends ReactiveCrudRepository<Day, Long> {
    Mono<Day> findByUserEmailAndDate(String userEmail, String date);
}
