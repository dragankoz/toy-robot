package com.codingchallenge.toyrobot.config;

import com.codingchallenge.toyrobot.controller.RobotNotFoundException;
import com.codingchallenge.toyrobot.controller.UnknownCommandException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {
    public static final String MISSING_ROBOT_MESSAGE = "MISSING ROBOT";

    @ExceptionHandler(RobotNotFoundException.class)
    public ResponseEntity<Object> handleException(RobotNotFoundException e) {
        return new ResponseEntity<>(e.getLocationDTO(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnknownCommandException.class)
    public ResponseEntity<Object> handleUnknownCommandException(UnknownCommandException e) {
        return new ResponseEntity<>(e.getInfos(), HttpStatus.NOT_ACCEPTABLE);
    }

}
