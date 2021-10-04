package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.Config.StrategyType;
import ch.zhaw.pm2.racetrack.PositionVector.Direction;
import ch.zhaw.pm2.racetrack.strategy.FileMovement;
import ch.zhaw.pm2.racetrack.strategy.MoveStrategy;
import ch.zhaw.pm2.racetrack.strategy.NoMovement;
import ch.zhaw.pm2.racetrack.strategy.PathFollower;
import ch.zhaw.pm2.racetrack.strategy.UserMovement;

/**
 * Class representing a car on the racetrack.
 * Uses {@link PositionVector} to store current position on the track grid and current velocity vector.
 * Each car has an identifier character which represents the car on the race track board.
 * Also keeps the state, if the car is crashed (not active anymore). The state can not be changed back to not crashed.
 * The velocity is changed by providing an acceleration vector.
 * The car is able to calculate the endpoint of its next position and on request moves to it.
 */
public class Car {
    private char id;
    private PositionVector position;
    private PositionVector velocity;
    private boolean isCrashed;
    private StrategyType strategyType;
    private MoveStrategy moveStrategy;

    public Car(char id, int positionX, int positionY) {
        this.id = id;
        position = new PositionVector(positionX, positionY);
        velocity = new PositionVector(0, 0);
        isCrashed = false;
    }

    public Character getID() {
        return id;
    }

    public PositionVector getPosition() {
        return position;
    }

    public PositionVector getVelocity() {
        return velocity;
    }

    public boolean getCarCrashStatus() {
        return isCrashed;
    }

    /**
     * Sets strategy for car
     *
     * @param strategyType
     * @param pathName
     */
    public void setStrategyType(StrategyType strategyType, String pathName) {
        this.strategyType = strategyType;
        if (strategyType == StrategyType.USER) {
            this.moveStrategy = new UserMovement();
        } else if (strategyType == StrategyType.DO_NOT_MOVE) {
            this.moveStrategy = new NoMovement();
        } else if (strategyType == StrategyType.MOVE_LIST) {
            this.moveStrategy = new FileMovement(pathName, Character.toString(this.getID()));
        } else if (strategyType == StrategyType.PATH_FOLLOWER) {
            this.moveStrategy = new PathFollower(pathName, Character.toString(this.getID()), this.position);
        }
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    /**
     * This Method calculate the new Position and give it back.
     *
     * @return The new PositionVector
     */
    public PositionVector nextPosition() {
        return PositionVectorHelper.add(position, velocity);
    }

    /**
     * This Method set the new speed with the direction
     *
     * @param direction The new Direction
     */
    public void accelerate(Direction direction) {
        velocity = PositionVectorHelper.add(direction.vector, velocity);
    }

    /**
     * Moves the Player to the next Position
     */
    public void move(PositionVector nextPosition) {
        position = nextPosition;
    }

    /**
     * Method for a Car which will crashed
     */
    public void crash(PositionVector nextPosition) {
        position = nextPosition;
        isCrashed = true;
    }

    /**
     * Set the data field crashed to true.
     */
    public boolean isCrashed() {
        return isCrashed;
    }

    public Direction nextMove() {
        return moveStrategy.nextMove();
    }
}
