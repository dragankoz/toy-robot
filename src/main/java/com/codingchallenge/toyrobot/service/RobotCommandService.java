package com.codingchallenge.toyrobot.service;

import com.codingchallenge.toyrobot.domain.CommandEnum;
import com.codingchallenge.toyrobot.domain.CommandLeft;
import com.codingchallenge.toyrobot.domain.CommandMove;
import com.codingchallenge.toyrobot.domain.CommandPlace;
import com.codingchallenge.toyrobot.domain.CommandReport;
import com.codingchallenge.toyrobot.domain.CommandRight;
import com.codingchallenge.toyrobot.domain.DirectionEnum;
import com.codingchallenge.toyrobot.domain.ICommand;
import com.codingchallenge.toyrobot.domain.RobotLocationDTO;
import com.codingchallenge.toyrobot.domain.TurnEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RobotCommandService {

    // Robot grid size
    private static final int MIN_Y = 0;
    private static final int MIN_X = 0;
    private static final int MAX_Y = 4;
    private static final int MAX_X = 4;

    // Logging template messages
    private static final String TURN_COMMAND_MESSAGE = "Robot turning [{}][currentLocation={}]";
    private static final String MOVE_COMMAND_MESSAGE = "Robot moving [{}][currentLocation={}]";
    private static final String MOVE_COMMAND_IGNORED_MESSAGE = "Move command ignored [currentLocation={}]";

    // Robot location - Use atomic reference to keep this thread safe
    private static final AtomicReference<RobotLocationDTO> robotLocation = new AtomicReference<>();

    /**
     * Place robot on the grid facing a direction
     *
     * @param place
     */
    public void placeRobot(CommandPlace place) {
        if (place.getX() >= MIN_X && place.getX() <= MAX_X && place.getY() >= MIN_Y && place.getY() <= MAX_Y) {
            RobotLocationDTO location = new RobotLocationDTO(place.getX(), place.getY(), place.getDirection());
            robotLocation.set(location);
            log.info("New robot location [currentLocation={}", location);
        }
    }

    /**
     * Move robot forward in the direction its facing by one square
     */
    public void moveForward() {
        robotLocation.getAndUpdate(location -> {
            if (location != null) {
                switch (location.getDirection()) {
                    case NORTH:
                        if (location.getY() + 1 <= MAX_Y) {
                            location.setY(location.getY() + 1);
                            log.info(MOVE_COMMAND_MESSAGE, location.getDirection(), location);
                        } else {
                            log.warn(MOVE_COMMAND_IGNORED_MESSAGE, location);
                        }
                        break;
                    case SOUTH:
                        if (location.getY() - 1 >= MIN_Y) {
                            location.setY(location.getY() - 1);
                            log.info(MOVE_COMMAND_MESSAGE, location.getDirection(), location);
                        } else {
                            log.warn(MOVE_COMMAND_IGNORED_MESSAGE, location);
                        }
                        break;
                    case EAST:
                        if (location.getX() + 1 <= MAX_X) {
                            location.setX(location.getX() + 1);
                            log.info(MOVE_COMMAND_MESSAGE, location.getDirection(), location);
                        } else {
                            log.warn(MOVE_COMMAND_IGNORED_MESSAGE, location);
                        }
                        break;
                    case WEST:
                        if (location.getX() - 1 >= MIN_X) {
                            location.setX(location.getX() - 1);
                            log.info(MOVE_COMMAND_MESSAGE, location.getDirection(), location);
                        } else {
                            log.warn(MOVE_COMMAND_IGNORED_MESSAGE, location);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported robot direction");
                }
            }
            return location;
        });
    }

    /**
     * Turn robot left
     */
    public void turnLeft() {
        turn(TurnEnum.LEFT);
    }

    /**
     * Turn robot right
     */
    public void turnRight() {
        turn(TurnEnum.RIGHT);
    }

    /**
     * Return robots current location
     */
    public RobotLocationDTO reportLocation() {
        return robotLocation.get();
    }

    /**
     * Remove the robot from the grid
     */
    public void deleteRobot() {
        robotLocation.set(null);
        log.info("Robot deleted");
    }

    /**
     * Execute a list of robot commands atomically
     *
     * @param batchCommands
     * @return
     */
    public List<RobotLocationDTO> executeBatchCommandsForRobot(String batchCommands) {
        return parseCommands(batchCommands).stream()
                .map(cmd -> {
                    RobotLocationDTO robotLocationDTO = null;
                    CommandEnum cmdType = cmd.getCommand();
                    switch (cmdType) {
                        case PLACE:
                            placeRobot((CommandPlace) cmd);
                            break;
                        case MOVE:
                            moveForward();
                            break;
                        case LEFT:
                            turnLeft();
                            break;
                        case RIGHT:
                            turnRight();
                            break;
                        case REPORT:
                            robotLocationDTO = reportLocation();
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported robot command");
                    }
                    return robotLocationDTO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * General turn method which handles any direction
     *
     * @param turnDirection
     */
    private void turn(TurnEnum turnDirection) {
        robotLocation.getAndUpdate(rl -> {
            if (rl != null) {
                switch (rl.getDirection()) {
                    case NORTH:
                        if (turnDirection == TurnEnum.LEFT) {
                            rl.setDirection(DirectionEnum.WEST);
                        } else if (turnDirection == TurnEnum.RIGHT) {
                            rl.setDirection(DirectionEnum.EAST);
                        }
                        log.info(TURN_COMMAND_MESSAGE, turnDirection, rl);
                        break;
                    case SOUTH:
                        if (turnDirection == TurnEnum.LEFT) {
                            rl.setDirection(DirectionEnum.EAST);
                        } else if (turnDirection == TurnEnum.RIGHT) {
                            rl.setDirection(DirectionEnum.WEST);
                        }
                        log.info(TURN_COMMAND_MESSAGE, turnDirection, rl);
                        break;
                    case EAST:
                        if (turnDirection == TurnEnum.LEFT) {
                            rl.setDirection(DirectionEnum.NORTH);
                        } else if (turnDirection == TurnEnum.RIGHT) {
                            rl.setDirection(DirectionEnum.SOUTH);
                        }
                        log.info(TURN_COMMAND_MESSAGE, turnDirection, rl);
                        break;
                    case WEST:
                        if (turnDirection == TurnEnum.LEFT) {
                            rl.setDirection(DirectionEnum.SOUTH);
                        } else if (turnDirection == TurnEnum.RIGHT) {
                            rl.setDirection(DirectionEnum.NORTH);
                        }
                        log.info(TURN_COMMAND_MESSAGE, turnDirection, rl);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown robot direction");
                }
            }
            return rl;
        });
    }

    /**
     * Parse a batch of commands that have been passed in
     *
     * @param batchCliCommands
     * @return
     */
    private List<ICommand> parseCommands(String batchCliCommands) {
        List<ICommand> commandRet = new ArrayList<>();
        if (StringUtils.hasText(batchCliCommands)) {
            // Cleanup whitespace so we can parse nicely
            batchCliCommands =  batchCliCommands.replaceAll("\\s*,\\s*", ",").replaceAll("\\s+", " ");
            log.info("batchCliCommands={}", batchCliCommands);
            StringTokenizer st = new StringTokenizer(batchCliCommands);
            while (st.hasMoreTokens()) {
                String cmd = st.nextToken().trim().toUpperCase();
                if (CommandEnum.PLACE.name().equals(cmd)) {
                    // Intialize and loop around for the next arg
                    CommandPlace commandHolder = new CommandPlace(-1, -1, null);
                    if (st.hasMoreTokens()) {
                        setPlacementParameters(st.nextToken(), commandHolder);
                    } else {
                        throw new IllegalArgumentException(String.format("%s requires placement arguments", CommandEnum.PLACE));
                    }
                    commandRet.add(commandHolder);
                } else if (CommandEnum.MOVE.name().equals(cmd)) {
                    CommandMove commandHolder = new CommandMove();
                    commandRet.add(commandHolder);
                } else if (CommandEnum.LEFT.name().equals(cmd)) {
                    CommandLeft commandHolder = new CommandLeft();
                    commandRet.add(commandHolder);
                } else if (CommandEnum.RIGHT.name().equals(cmd)) {
                    CommandRight commandHolder = new CommandRight();
                    commandRet.add(commandHolder);
                } else if (CommandEnum.REPORT.name().equals(cmd)) {
                    CommandReport commandHolder = new CommandReport();
                    commandRet.add(commandHolder);
                } else {
                    throw new IllegalArgumentException(String.format("Unsupported command [%s]", cmd));
                }
            }
        }
        return commandRet;
    }

    /**
     * Parses and sets the placement parameters following a PLACE command
     *
     * @param placement
     * @param commandHolder
     */
    private void setPlacementParameters(String placement, CommandPlace commandHolder) {
        placement = placement.trim().toUpperCase();
        // Parse the placements
        StringTokenizer placementSt = new StringTokenizer(placement, ",");
        if (placementSt.countTokens() != 3) {
            throw new IllegalArgumentException(String.format("Invalid placement arguments [%s]", placement));
        }
        String placementValue = null;
        try {
            placementValue = placementSt.nextToken();
            int x = Integer.parseInt(placementValue);
            placementValue = placementSt.nextToken();
            int y = Integer.parseInt(placementValue);
            placementValue = placementSt.nextToken().toUpperCase();
            DirectionEnum direction = DirectionEnum.valueOf(placementValue);
            commandHolder.setX(x);
            commandHolder.setY(y);
            commandHolder.setDirection(direction);
        } catch (Exception parseEx) {
            throw new IllegalArgumentException(String.format("Invalid placement value [%s]", placementValue));
        }

    }

}
