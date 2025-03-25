package com.adi.todo.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Table
@Data
public class Task {
    @Id
    private Long id;
    private String taskid;
    private String name;
    private Long dayid;
    @Column("user_email")
    private String userEmail;
    private boolean completed;
}
