package com.adi.todo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.adi.todo.model.exception.TodoAppException;

@RestControllerAdvice
public class ExcpetionController {

    @ExceptionHandler(TodoAppException.class)
    public ResponseEntity<String> handleTodoAppException(TodoAppException ex) {
        return new ResponseEntity<>(ex.getErrorMessage(), ex.getStatus());
    }
}
