package com.codingchallenge.toyrobot.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandReport implements ICommand {
    private CommandEnum command = CommandEnum.REPORT;
}
