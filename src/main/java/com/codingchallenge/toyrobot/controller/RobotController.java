package com.codingchallenge.toyrobot.controller;

import com.codingchallenge.toyrobot.config.ApplicationExceptionHandler;
import com.codingchallenge.toyrobot.domain.CommandPlace;
import com.codingchallenge.toyrobot.domain.DirectionEnum;
import com.codingchallenge.toyrobot.domain.IRobotLocation;
import com.codingchallenge.toyrobot.domain.RobotLocation;
import com.codingchallenge.toyrobot.service.RobotCommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(RobotController.API)
@Api(tags = {"robot"}, description = "REST api's for a stateful toy robot")
public class RobotController {

    static final String API = "/rest/robot";

    private final RobotCommandService robotCommandService;

    public RobotController(RobotCommandService robotCommandService) {
        this.robotCommandService = robotCommandService;
    }

    @ApiOperation(value = "Place the robot on the grid at xy co-ordinates and orientation")
    @PostMapping(path = "/place/x/{x}/y/{y}/direction/{direction}", produces = APPLICATION_JSON_VALUE)
    public void placeRobot(@PathVariable("x") int x,
                           @PathVariable("y") int y,
                           @PathVariable("direction") DirectionEnum direction) {
        robotCommandService.placeRobot(RobotCommandService.getStateFulRobotLocation(), new CommandPlace(x, y, direction));
    }

    @ApiOperation(value = "Move the robot forward one square in the current orientation direction")
    @PutMapping(path = "/move", produces = APPLICATION_JSON_VALUE)
    public void moveForward() {
        robotCommandService.moveForward(RobotCommandService.getStateFulRobotLocation());
    }

    @ApiOperation(value = "Rotate the robot right on the grid")
    @PutMapping(path = "/left", produces = APPLICATION_JSON_VALUE)
    public void turnLeft() {
        robotCommandService.turnLeft(RobotCommandService.getStateFulRobotLocation());
    }

    @ApiOperation(value = "Rotate the robot right on the grid")
    @PutMapping(path = "/right", produces = APPLICATION_JSON_VALUE)
    public void turnRight() {
        robotCommandService.turnRight(RobotCommandService.getStateFulRobotLocation());
    }

    @ApiOperation(value = "Report on the current location and orientation of the robot")
    @GetMapping(path = "/report", produces = APPLICATION_JSON_VALUE)
    @ApiResponse(code = 404, message = ApplicationExceptionHandler.MISSING_ROBOT_MESSAGE)
    public IRobotLocation reportLocation() {
        RobotLocation robotLocation = robotCommandService.reportLocation(RobotCommandService.getStateFulRobotLocation());
        IRobotLocation robotLocationDTO = robotCommandService.convertLocationToDTO(robotLocation);
        if (robotLocation == null) {
            throw new RobotNotFoundException(robotLocationDTO);
        }
        return robotLocationDTO;
    }

    @ApiOperation(value = "Delete/remove the robot from the grid")
    @DeleteMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRobot() {
        robotCommandService.deleteRobot(RobotCommandService.getStateFulRobotLocation());
    }


}
