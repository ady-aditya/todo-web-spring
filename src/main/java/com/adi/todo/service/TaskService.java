package com.adi.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adi.todo.model.api.AddTaskRequest;
import com.adi.todo.model.entity.Day;
import com.adi.todo.model.entity.Task;
import com.adi.todo.service.repository.DayRepository;
import com.adi.todo.service.repository.TaskRepository;

import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    DayRepository dayRepository;

    public Mono<String> addTask(AddTaskRequest request, String userEmail) {

        return taskRepository.findByTaskid(request.getTaskId()) 
            .collectList()
            .flatMap(existingTasks -> {
                if (!existingTasks.isEmpty()) {
                    // Update existing task's name only
                    Task existingTask = existingTasks.get(0);
                    existingTask.setName(request.getName());
                    existingTask.setCompleted(request.isCompleted());
                    return taskRepository.save(existingTask)
                            .thenReturn("Task updated successfully");
                } else {
                    // Check if day exists
                    return dayRepository.findByUserEmailAndDate(userEmail, request.getDay())
                        .switchIfEmpty(
                            // Create new day if not found
                            Mono.defer(() -> {
                                Day newDay = new Day();
                                newDay.setUserEmail(userEmail);
                                newDay.setDate(request.getDay());
                                newDay.setCreatedDate(LocalDate.now());
                                return dayRepository.save(newDay);
                            })
                        )
                        .flatMap(day -> {
                            // Create new task with day id
                            Task newTask = new Task();
                            newTask.setTaskid(request.getTaskId());
                            newTask.setName(request.getName());
                            newTask.setDayid(day.getId());
                            newTask.setCompleted(false);
                            newTask.setUserEmail(userEmail);
                            return taskRepository.save(newTask)
                                    .thenReturn("Task created successfully");
                        });
                }
            });


    }

    public Mono<String> updateTask(AddTaskRequest request, String userEmail) {
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
                if(request.getName() != null && !request.getName().trim().isEmpty()) {
                    existingTask.setName(request.getName());
                }
                existingTask.setCompleted(request.isCompleted());
                return taskRepository.save(existingTask)
                    .thenReturn("Task updated successfully");
            });
    }


       
}
