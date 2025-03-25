package com.adi.todo.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import java.time.LocalDate;

@Table
@Data
public class Day {
    @Id
    private Long id;
    private String userEmail;
    private String date;
    private LocalDate createdDate;
}
