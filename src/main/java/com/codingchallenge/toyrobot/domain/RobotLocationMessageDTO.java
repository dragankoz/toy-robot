package com.codingchallenge.toyrobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RobotLocationMessageDTO implements IRobotLocation {
    private String message;
}
