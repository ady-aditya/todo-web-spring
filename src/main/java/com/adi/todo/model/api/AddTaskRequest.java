package com.adi.todo.model.api;

import lombok.Data;

@Data
public class AddTaskRequest {
    private String taskId;
    private String name;
    private String day;
    private boolean completed;
}

