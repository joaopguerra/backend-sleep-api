package com.noom.interview.fullstack.sleep.controllers;

import com.noom.interview.fullstack.sleep.exceptions.SleepException;
import com.noom.interview.fullstack.sleep.exceptions.StandardError;
import com.noom.interview.fullstack.sleep.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(SleepException.class)
    public ResponseEntity<StandardError> sleepNotFound(SleepException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError error = new StandardError(Instant.now(), status.value(), status.name(), e.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<StandardError> userNotFound(UserException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError error = new StandardError(Instant.now(), status.value(), status.name(), e.getMessage());
        return ResponseEntity.status(status).body(error);
    }
}
