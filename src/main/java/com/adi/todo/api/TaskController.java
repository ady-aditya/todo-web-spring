package com.adi.todo.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.adi.todo.model.api.AddTaskRequest;
import com.adi.todo.model.api.TasksResponse;
import com.adi.todo.model.api.UpdateTaskRequest;
import com.adi.todo.service.TaskService;
import com.adi.todo.service.validation.TaskValidator;
import reactor.core.publisher.Mono;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskValidator taskValidator;

    @Autowired
    private TaskService taskService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("addTask")
    public Mono<ResponseEntity<String>> addTask(@RequestBody AddTaskRequest request, ServerWebExchange exchange) {
        String userEmail = (String) exchange.getAttributes().get("email");
        List<String> validationErrors = taskValidator.addTaskReqValidation(request, userEmail);
        if (!validationErrors.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(String.join(", ", validationErrors)));
        }

        return taskService.addTask(request, userEmail)
                .map(result -> ResponseEntity.ok(result))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(500).body(error.getMessage())));

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("updateTask")
    public Mono<ResponseEntity<String>> updateTask(@RequestBody UpdateTaskRequest request, ServerWebExchange exchange) {
        String userEmail = (String) exchange.getAttributes().get("email");
        List<String> validationErrors = taskValidator.updateTaskReqValidation(request, userEmail);
        if (!validationErrors.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(String.join(", ", validationErrors)));
        }

        return taskService.updateTask(request, userEmail)
                .map(result -> ResponseEntity.ok(result))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(500).body(error.getMessage())));

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("fetchTasks")
    public Mono<ResponseEntity<TasksResponse>> fetchTasks(
            @RequestHeader(name = "day", required = false) String day,
            @RequestHeader(name = "numberOfDays", defaultValue = "100") int numberOfDays,
            ServerWebExchange exchange) {
        String userEmail = (String) exchange.getAttributes().get("email");
        if (day != null)
            taskValidator.validateDay(day);
        return taskService.fetchTasks(day, numberOfDays, userEmail)
                .map(result -> ResponseEntity.ok(result));
    }

}
