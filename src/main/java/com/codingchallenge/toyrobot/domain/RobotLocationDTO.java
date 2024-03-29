package com.codingchallenge.toyrobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RobotLocationDTO implements IRobotLocation {
    private int x;
    private int y;
    private DirectionEnum direction;
}
