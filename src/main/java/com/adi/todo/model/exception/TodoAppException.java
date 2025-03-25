package com.adi.todo.model.exception;

import org.springframework.http.HttpStatus;

public class TodoAppException extends RuntimeException {

    HttpStatus status;
    String errorMessage;
    public TodoAppException(HttpStatus status,String message) {
        super(message);
        this.status = status;
        this.errorMessage = message;
    }   

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }       
}
