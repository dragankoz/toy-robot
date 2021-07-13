package com.codingchallenge.toyrobot.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandMove implements ICommand {
    private CommandEnum command = CommandEnum.MOVE;
}
