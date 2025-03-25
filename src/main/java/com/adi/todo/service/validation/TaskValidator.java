package com.adi.todo.service.validation;

import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

import com.adi.todo.model.api.AddTaskRequest;


@Service
public class TaskValidator {
    
    public List<String> addTaskReqValidation(AddTaskRequest request, String email) {
        List<String> validationErrors = new ArrayList<>();

        if (email == null || email.trim().isEmpty()) {
            validationErrors.add("User email is required");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            validationErrors.add("Invalid email format");
        }

        if (request.getTaskId() == null || request.getTaskId().trim().isEmpty()) {
            validationErrors.add("Task ID is required");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            validationErrors.add("Task name is required");
        }

        if (request.getDay() == null) {
            validationErrors.add("Day is required");
        }
        if (request.getDay() != null) {
            try {
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                dateFormat.parse(request.getDay());
            } catch (java.text.ParseException e) {
                validationErrors.add("Day must be a valid date in format YYYY-MM-DD");
            }
        }

        return validationErrors;
    }

    public List<String> updateTaskReqValidation(AddTaskRequest request, String email) {
        List<String> validationErrors = new ArrayList<>();

        if (request.getTaskId() == null || request.getTaskId().trim().isEmpty()) {
            validationErrors.add("Task ID is required");
        }

        return validationErrors;
    }
}
