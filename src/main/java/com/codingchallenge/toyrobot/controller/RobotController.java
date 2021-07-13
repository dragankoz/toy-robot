package com.codingchallenge.toyrobot.controller;

import com.codingchallenge.toyrobot.domain.CommandPlace;
import com.codingchallenge.toyrobot.domain.DirectionEnum;
import com.codingchallenge.toyrobot.domain.RobotLocationDTO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(RobotController.API)
@Api(tags = {"robot"})
public class RobotController {

    static final String API = "/rest/robot";

    private final RobotCommandService robotService;

    public RobotController(RobotCommandService robotService) {
        this.robotService = robotService;
    }

    @ApiOperation(value = "Execute a string of batch ci robot commands",
            notes = "Example: PLACE 0,0,NORTH MOVE LEFT MOVE RIGHT REPORT",
            response = RobotLocationDTO.class,
            responseContainer = "List")
    @PostMapping(path = "/batch-cli", consumes = TEXT_PLAIN_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<RobotLocationDTO> batchCommandsForRobot(@RequestBody String batchCommands) {
        return robotService.executeBatchCommandsForRobot(batchCommands);
    }

    @ApiOperation(value = "Place the robot on the grid at xy co-ordinates and orientation")
    @PostMapping(path = "/place/x/{x}/y/{y}/direction/{direction}", produces = APPLICATION_JSON_VALUE)
    public void placeRobot(@PathVariable("x") int x,
                           @PathVariable("y") int y,
                           @PathVariable("direction") DirectionEnum direction) {
        robotService.placeRobot(new CommandPlace(x, y, direction));
    }

    @ApiOperation(value = "Move the robot forward one square in the current orientation direction")
    @PutMapping(path = "/move", produces = APPLICATION_JSON_VALUE)
    public void moveForward() {
        robotService.moveForward();
    }

    @ApiOperation(value = "Rotate the robot right on the grid")
    @PutMapping(path = "/left", produces = APPLICATION_JSON_VALUE)
    public void turnLeft() {
        robotService.turnLeft();
    }

    @ApiOperation(value = "Rotate the robot right on the grid")
    @PutMapping(path = "/right", produces = APPLICATION_JSON_VALUE)
    public void turnRight() {
        robotService.turnRight();
    }

    @ApiOperation(value = "Report on the current location and orientation of the robot")
    @GetMapping(path = "/report", produces = APPLICATION_JSON_VALUE)
    @ApiResponse(code = 404, message = "ROBOT MISSING")
    public RobotLocationDTO reportLocation() {
        RobotLocationDTO retDTO = robotService.reportLocation();
        if (retDTO == null) {
            throw new RobotNotFoundException();
        }
        return retDTO;
    }

    @ApiOperation(value = "Delete/remove the robot from the grid")
    @DeleteMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRobot() {
        robotService.deleteRobot();
    }


}
