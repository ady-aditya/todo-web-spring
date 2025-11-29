package com.adi.todo.service.validation;

import java.util.List;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adi.todo.model.api.AddTaskRequest;
import com.adi.todo.model.api.UpdateTaskRequest;
import com.adi.todo.model.exception.TodoAppException;

@Service
public class TaskValidator {

    public List<String> addTaskReqValidation(AddTaskRequest request, String email) {
        List<String> validationErrors = new ArrayList<>();

        if (email == null || email.trim().isEmpty()) {
            validationErrors.add("User email is required");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            validationErrors.add("Invalid email format");
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

    public List<String> updateTaskReqValidation(UpdateTaskRequest request, String email) {
        List<String> validationErrors = new ArrayList<>();

        if (request.getTaskId() == null || request.getTaskId().trim().isEmpty()) {
            validationErrors.add("Task ID is required");
        }

        return validationErrors;
    }

    public void validateDay(String day) {
        try {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(day);
        } catch (java.text.ParseException e) {
            throw new TodoAppException(HttpStatus.BAD_REQUEST, "Day must be a valid date in format yyyy-MM-dd");
        }
    }
}
