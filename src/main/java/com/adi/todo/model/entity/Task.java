package com.adi.todo.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Table
@Data
public class Task {
    @Id
    @JsonIgnore
    private Long id;
    private String taskid;
    private String name;
    @JsonIgnore
    private Long dayid;
    @Column("user_email")
    private String userEmail;
    private boolean completed;
}
