package com.codingchallenge.toyrobot.config;

import com.codingchallenge.toyrobot.controller.RobotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "MISSING ROBOT")
    @ExceptionHandler(RobotNotFoundException.class)
    public void handleException(RobotNotFoundException e) {
    }
}
