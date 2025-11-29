package com.adi.todo.model.api;

import lombok.Data;

@Data
public class UpdateTaskRequest {
    private String taskId;
    private String name;
    private boolean completed;
}
