package com.adi.todo.model.api;

import java.util.List;

import com.adi.todo.model.entity.Task;

import lombok.Data;

@Data
public class DayTasks {
    String day;
    List<Task> tasks;
}
