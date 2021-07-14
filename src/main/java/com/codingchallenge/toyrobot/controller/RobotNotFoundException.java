package com.codingchallenge.toyrobot.controller;

import com.codingchallenge.toyrobot.domain.IRobotLocation;
import lombok.Getter;

@Getter
public class RobotNotFoundException extends RuntimeException {
    private IRobotLocation locationDTO;

    public RobotNotFoundException(IRobotLocation locationDTO) {
        this.locationDTO = locationDTO;
    }
}
