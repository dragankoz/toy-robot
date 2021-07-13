package com.codingchallenge.toyrobot.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandPlace implements ICommand {
    private CommandEnum command = CommandEnum.PLACE;
    private int x;
    private int y;
    private DirectionEnum direction;

    public CommandPlace(int x, int y, DirectionEnum direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
