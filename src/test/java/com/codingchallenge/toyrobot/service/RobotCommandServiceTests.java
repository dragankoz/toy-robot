package com.codingchallenge.toyrobot.service;

import com.codingchallenge.toyrobot.config.ApplicationExceptionHandler;
import com.codingchallenge.toyrobot.controller.UnknownCommandException;
import com.codingchallenge.toyrobot.domain.CommandPlace;
import com.codingchallenge.toyrobot.domain.DirectionEnum;
import com.codingchallenge.toyrobot.domain.IRobotLocation;
import com.codingchallenge.toyrobot.domain.RobotLocation;
import com.codingchallenge.toyrobot.domain.RobotLocationDTO;
import com.codingchallenge.toyrobot.domain.RobotLocationMessageDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RobotCommandServiceTests {

    @Autowired
    private RobotCommandService robotCommandService;

    @Test
    void placeRobotTest() {
        AtomicReference<RobotLocation> stateFulRobotLocation = new AtomicReference<>();
        robotCommandService.placeRobot(stateFulRobotLocation, new CommandPlace(4, 3, DirectionEnum.SOUTH));
        assertLocation(stateFulRobotLocation, 4, 3, DirectionEnum.SOUTH);
    }

    @Test
    void placeRobotOutsideRangeTest() {
        AtomicReference<RobotLocation> stateFulRobotLocation = new AtomicReference<>();
        robotCommandService.placeRobot(stateFulRobotLocation, new CommandPlace(5, -1, DirectionEnum.NORTH));
        assertNull(robotCommandService.reportLocation(stateFulRobotLocation));
        robotCommandService.placeRobot(stateFulRobotLocation, new CommandPlace(-2, 10, DirectionEnum.NORTH));
        assertNull(robotCommandService.reportLocation(stateFulRobotLocation));
    }

    @Test
    void reportNotExistsDTOTest() {
        AtomicReference<RobotLocation> stateFulRobotLocation = new AtomicReference<>();
        RobotLocation robotLocation = robotCommandService.reportLocation(stateFulRobotLocation);
        IRobotLocation location = robotCommandService.convertLocationToDTO(robotLocation);
        assertTrue(location instanceof RobotLocationMessageDTO);
        assertEquals(ApplicationExceptionHandler.MISSING_ROBOT_MESSAGE, ((RobotLocationMessageDTO) location).getMessage());
    }

    @Test
    void generalCommandsTest() {
        AtomicReference<RobotLocation> stateFulRobotLocation = new AtomicReference<>();
        robotCommandService.placeRobot(stateFulRobotLocation, new CommandPlace(4, 3, DirectionEnum.NORTH));
        robotCommandService.moveForward(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 4, 4, DirectionEnum.NORTH);
        robotCommandService.turnLeft(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 4, 4, DirectionEnum.WEST);
        robotCommandService.moveForward(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 3, 4, DirectionEnum.WEST);
        robotCommandService.turnLeft(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 3, 4, DirectionEnum.SOUTH);
        robotCommandService.moveForward(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 3, 3, DirectionEnum.SOUTH);
    }

    @Test
    void outsideGridTest() {
        AtomicReference<RobotLocation> stateFulRobotLocation = new AtomicReference<>();
        robotCommandService.placeRobot(stateFulRobotLocation, new CommandPlace(0, 0, DirectionEnum.SOUTH));
        robotCommandService.moveForward(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 0, 0, DirectionEnum.SOUTH);
        robotCommandService.turnRight(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 0, 0, DirectionEnum.WEST);
        robotCommandService.moveForward(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 0, 0, DirectionEnum.WEST);
        robotCommandService.turnRight(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 0, 0, DirectionEnum.NORTH);
        moveForwardSteps(stateFulRobotLocation, RobotCommandService.MAX_X + 1);
        assertLocation(stateFulRobotLocation, 0, 4, DirectionEnum.NORTH);
        robotCommandService.turnRight(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 0, 4, DirectionEnum.EAST);
        moveForwardSteps(stateFulRobotLocation, RobotCommandService.MAX_X + 1);
        assertLocation(stateFulRobotLocation, 4, 4, DirectionEnum.EAST);
        robotCommandService.turnRight(stateFulRobotLocation);
        assertLocation(stateFulRobotLocation, 4, 4, DirectionEnum.SOUTH);
        moveForwardSteps(stateFulRobotLocation, RobotCommandService.MAX_X + 1);
        assertLocation(stateFulRobotLocation, 4, 0, DirectionEnum.SOUTH);
    }

    @Test
    void batchCliParseTest() {
        List<IRobotLocation> locations = robotCommandService.executeBatchCommandsForRobot("PLACE 0,0,NORTH MOVE RIGHT MOVE RIGHT MOVE REPORT");
        assertEquals(1, locations.size());
        RobotLocationDTO rDTO = (RobotLocationDTO)locations.get(0);
        assertEquals(1, rDTO.getX());
        assertEquals(0, rDTO.getY());
        assertEquals(DirectionEnum.SOUTH, rDTO.getDirection());
    }


    @Test
    void batchCliParseErrorTest() {
        Assertions.assertThrows(UnknownCommandException.class, () ->
                robotCommandService.executeBatchCommandsForRobot("PLACE 0,0,NORTH WOT?")
        );
    }


    private void assertLocation(AtomicReference<RobotLocation> stateFulRobotLocation, int x, int y, DirectionEnum direction) {
        RobotLocation robotLocation = robotCommandService.reportLocation(stateFulRobotLocation);
        assertEquals(x, robotLocation.getX());
        assertEquals(y, robotLocation.getY());
        assertEquals(direction, robotLocation.getDirection());
    }

    private void moveForwardSteps(AtomicReference<RobotLocation> robotLocationInstance, int steps) {
        for (int cnt = 0; cnt <= steps; ++cnt) {
            robotCommandService.moveForward(robotLocationInstance);
        }
    }

}
