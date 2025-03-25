package com.adi.todo.service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.adi.todo.model.entity.Task;

import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {
    Flux<Task> findByTaskid(String taskid);
}
