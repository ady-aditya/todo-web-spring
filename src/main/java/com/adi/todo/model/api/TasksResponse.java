package com.adi.todo.model.api;

import java.util.List;

import lombok.Data;

@Data
public class TasksResponse {
    List<DayTasks> daysTasks;
}
