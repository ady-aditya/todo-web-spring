package com.adi.todo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adi.todo.model.api.AddTaskRequest;
import com.adi.todo.model.api.DayTasks;
import com.adi.todo.model.api.TasksResponse;
import com.adi.todo.model.api.UpdateTaskRequest;
import com.adi.todo.model.entity.Day;
import com.adi.todo.model.entity.Task;
import com.adi.todo.model.exception.TodoAppException;
import com.adi.todo.service.repository.DayRepository;
import com.adi.todo.service.repository.TaskRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    DayRepository dayRepository;

    public Mono<String> addTask(AddTaskRequest request, String userEmail) {
        String generatedTaskId = generateTaskId(request.getDay(), userEmail);

        return dayRepository.findByUserEmailAndDate(userEmail, request.getDay())
                .switchIfEmpty(
                        // Create new day if not found
                        Mono.defer(() -> {
                            Day newDay = new Day();
                            newDay.setUserEmail(userEmail);
                            newDay.setDate(request.getDay());
                            newDay.setCreatedDate(LocalDate.now());
                            return dayRepository.save(newDay);
                        }))
                .flatMap(day -> {
                    // Create new task with day id
                    Task newTask = new Task();
                    newTask.setTaskid(generatedTaskId);
                    newTask.setName(request.getName());
                    newTask.setDayid(day.getId());
                    newTask.setCompleted(false);
                    newTask.setUserEmail(userEmail);
                    return taskRepository.save(newTask)
                            .thenReturn("Task created successfully");
                });
    }

    private String generateTaskId(String day, String email) {
        try {
            String input = day + email + System.currentTimeMillis() + new Random().nextInt();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating task ID", e);
        }
    }

    public Mono<String> updateTask(UpdateTaskRequest request, String userEmail) {
        return taskRepository.findByTaskid(request.getTaskId())
                .collectList()
                .flatMap(existingTasks -> {
                    if (existingTasks.isEmpty()) {
                        return Mono.error(new RuntimeException("Task not found"));
                    }
                    Task existingTask = existingTasks.get(0);
                    if (!existingTask.getUserEmail().equals(userEmail)) {
                        return Mono.error(new RuntimeException("Unauthorized to update this task"));
                    }
                    if (request.getName() != null && !request.getName().trim().isEmpty()) {
                        existingTask.setName(request.getName());
                    }
                    existingTask.setCompleted(request.isCompleted());
                    return taskRepository.save(existingTask)
                            .thenReturn("Task updated successfully");
                });
    }

    private Mono<List<Task>> fetchTasksForDay(String day, String userEmail) {
        return dayRepository.findByUserEmailAndDate(userEmail, day)
                .flatMap(dayEntity -> taskRepository.findByDayid(dayEntity.getId())
                        .collectList()
                        .map(tasks -> {
                            if (tasks.isEmpty()) {
                                throw new TodoAppException(HttpStatus.NOT_FOUND, "no tasks for the day found");
                            }
                            return tasks;
                        }))
                .switchIfEmpty(Mono.error(new TodoAppException(HttpStatus.NOT_FOUND, "no tasks for the day found")));
    }

    public Mono<TasksResponse> fetchTasks(String day, int numberOfDays, String userEmail) {
        if (day != null) {
            return fetchTasksForDay(day, userEmail)
                    .map(result -> {
                        TasksResponse response = new TasksResponse();
                        DayTasks dayTasks = new DayTasks();
                        dayTasks.setDay(day);
                        dayTasks.setTasks(result);
                        response.setDaysTasks(List.of(dayTasks));
                        return response;
                    })
                    .switchIfEmpty(
                            Mono.error(new TodoAppException(HttpStatus.NOT_FOUND, "no tasks for the day found")));
        } else {
            return dayRepository.findByUserEmailOrderByCreatedDateDescLimit(userEmail, numberOfDays)
                    .flatMap(dayEntity -> {
                        log.info("dayEntity: {}", dayEntity);
                        return taskRepository.findByDayid(dayEntity.getId())
                                .collectList()
                                .map(tasks -> {
                                    DayTasks dayTasks = new DayTasks();
                                    dayTasks.setDay(dayEntity.getDate());
                                    dayTasks.setTasks(tasks);
                                    return dayTasks;
                                });
                    })
                    .switchIfEmpty(Mono.error(new TodoAppException(HttpStatus.NOT_FOUND, "no tasks for days found")))
                    .collectList()
                    .map(daysTasks -> {
                        TasksResponse response = new TasksResponse();
                        response.setDaysTasks(daysTasks);
                        return response;
                    });
            // return dayRepository.findByUserEmailOrderByCreatedDateDescLimit(userEmail,
            // numberOfDays)
            // .flatMap(dayEntity -> taskRepository.findByDayid(dayEntity.getId())
            // .collectList()
            // .map(tasks -> Tuples.of(dayEntity.getDate(), tasks)))
            // .collectMap(tuple -> tuple.getT1(), tuple -> tuple.getT2(),
            // LinkedHashMap::new)
            // .map(map -> {
            // TasksResponse response = new TasksResponse();
            // response.setTasks(map);
            // return response;
            // });
        }
    }

}
