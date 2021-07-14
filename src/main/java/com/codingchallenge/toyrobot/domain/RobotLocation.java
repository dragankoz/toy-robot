package com.codingchallenge.toyrobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RobotLocation {
    private int x;
    private int y;
    private DirectionEnum direction;
}
