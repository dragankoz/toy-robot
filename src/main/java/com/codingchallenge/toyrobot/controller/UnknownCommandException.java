package com.codingchallenge.toyrobot.controller;

import lombok.Getter;

import java.util.List;

@Getter
public class UnknownCommandException extends RuntimeException {
    private List<String> infos;

    public UnknownCommandException( List<String> infos) {
        this.infos = infos;
    }
}
