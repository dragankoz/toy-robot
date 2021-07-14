package com.codingchallenge.toyrobot.controller;

import com.codingchallenge.toyrobot.domain.IRobotLocation;
import com.codingchallenge.toyrobot.domain.RobotLocationDTO;
import com.codingchallenge.toyrobot.service.RobotCommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(RobotCliController.API)
@Api(tags = {"robot-cli"}, description = "REST api's for a stateless toy robot")
public class RobotCliController {

    static final String API = "/rest/robot/cli";

    private final RobotCommandService robotService;

    public RobotCliController(RobotCommandService robotService) {
        this.robotService = robotService;
    }

    @ApiOperation(value = "Execute a batch of cli commands on a state-less robot",
            notes = "Example: PLACE 0,0,NORTH MOVE LEFT MOVE RIGHT REPORT",
            response = RobotLocationDTO.class,
            responseContainer = "List")
    @PostMapping(path = "/batch", consumes = TEXT_PLAIN_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<IRobotLocation> batchCommandsForRobot(@RequestBody String batchCommands) {
        return robotService.executeBatchCommandsForRobot(batchCommands);
    }

}
