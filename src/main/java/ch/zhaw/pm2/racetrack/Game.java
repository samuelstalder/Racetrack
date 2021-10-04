package ch.zhaw.pm2.racetrack;


import java.util.ArrayList;
import java.util.List;

import static ch.zhaw.pm2.racetrack.PositionVector.Direction;

/**
 * Game controller class, performing all actions to modify the game state.
 * It contains the logic to move the cars, detect if they are crashed
 * and if we have a winner.
 *
 * @author Samuel Stalder
 */
public class Game {
    public static final int NO_WINNER = -1;
    private int currentCarIndex = 0;
    private Track track;
    private int winner = NO_WINNER;

    /**
     * Return the index of the current active car.
     * Car indexes are zero-based, so the first car is 0, and the last car is getCarCount() - 1.
     *
     * @return The zero-based number of the current car
     */
    public int getCurrentCarIndex() {
        return currentCarIndex;
    }

    /**
     * Get the id of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return A char containing the id of the car
     */
    public char getCarId(int carIndex) {
        return track.getCarId(carIndex);
    }

    /**
     * Get the position of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current position
     */
    public PositionVector getCarPosition(int carIndex) {
        return track.getCar(carIndex).getPosition();
    }

    /**
     * Get the velocity of the specified car.
     *
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current velocity
     */
    public PositionVector getCarVelocity(int carIndex) {
        return track.getCar(carIndex).getVelocity();
    }

    /**
     * Return the winner of the game. If the game is still in progress, returns NO_WINNER.
     *
     * @return The winning car's index (zero-based, see getCurrentCar()), or NO_WINNER if the game is still in progress
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Execute the next turn for the current active car.
     * <p>This method changes the current car's velocity and checks on the path to the next position,
     * if it crashes (car state to crashed) or passes the finish line in the right direction (set winner state).</p>
     * <p>The steps are as follows</p>
     * <ol>
     *   <li>Accelerate the current car</li>
     *   <li>Calculate the path from current (start) to next (end) position
     *       (see {@link Game#calculatePath(PositionVector, PositionVector)})</li>
     *   <li>Verify for each step what space type it hits:
     *      <ul>
     *          <li>TRACK: check for collision with other car (crashed &amp; don't continue), otherwise do nothing</li>
     *          <li>WALL: car did collide with the wall - crashed &amp; don't continue</li>
     *          <li>FINISH_*: car hits the finish line - wins only if it crosses the line in the correct direction</li>
     *      </ul>
     *   </li>
     *   <li>If the car crashed or wins, set its position to the crash/win coordinates</li>
     *   <li>If the car crashed, also detect if there is only one car remaining, remaining car is the winner</li>
     *   <li>Otherwise move the car to the end position</li>
     * </ol>
     * <p>The calling method must check the winner state and decide how to go on. If the winner is different
     * than {@link Game#NO_WINNER}, or the current car is already marked as crashed the method returns immediately.</p>
     *
     * @param acceleration A Direction containing the current cars acceleration vector (-1,0,1) in x and y direction
     *                     for this turn
     */
    public void doCarTurn(Direction acceleration) {
        Car car = track.getCar(currentCarIndex);
        car.accelerate(acceleration);
        PositionVector startPosition = car.getPosition();
        PositionVector endPosition = car.nextPosition();
        List<PositionVector> positions = calculatePath(startPosition, endPosition);
        boolean crashed = false;
        for (int i = 1; i <= positions.size() - 1; i++) {
            if (willCarCrash(currentCarIndex, positions.get(i))) {
                endPosition = positions.get(i);
                crashed = true;
                break;
            } else if (isBlockingBackDriving(startPosition, positions.get(i))) {
                endPosition = positions.get(i - 1);
                break;
            } else if (willCarReachFinishLine(positions.get(i))) {
                endPosition = positions.get(i);
                winner = currentCarIndex;
                break;
            }
        }
        if (startPosition.getX() != endPosition.getX() || startPosition.getY() != endPosition.getY()) {
            track.updateCarOnTrack(endPosition, currentCarIndex);
        }
        if (crashed) {
            car.crash(endPosition);
            winner = checkWinnerAfterCrash();
        } else {
            car.move(endPosition);
        }
    }

    private int checkWinnerAfterCrash() {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i <= track.getCarCount() - 1; i++) {
            if (!track.getCar(i).isCrashed()) {
                indexList.add(i);
            }
        }
        return (indexList.size() == 1) ? indexList.get(0) : NO_WINNER;
    }

    private boolean isBlockingBackDriving(PositionVector startPosition, PositionVector currentPosition) {
        boolean shouldBeBlocked = false;
        Config.SpaceType spaceType = track.getSpaceType(currentPosition);
        if (spaceType == Config.SpaceType.FINISH_LEFT) {
            if (currentPosition.getX() > startPosition.getX()) {
                shouldBeBlocked = true;
            }
        } else if (spaceType == Config.SpaceType.FINISH_RIGHT) {
            if (currentPosition.getX() < startPosition.getX()) {
                shouldBeBlocked = true;
            }
        } else if (spaceType == Config.SpaceType.FINISH_DOWN) {
            if (currentPosition.getX() > startPosition.getX()) {
                shouldBeBlocked = true;
            }
        } else if (spaceType == Config.SpaceType.FINISH_UP) {
            if (currentPosition.getX() < startPosition.getX()) {
                shouldBeBlocked = true;
            }
        }
        return shouldBeBlocked;
    }

    /**
     * Switches to the next car who is still in the game. Skips crashed cars.
     */
    public void switchToNextActiveCar() {
        if (winner == NO_WINNER) {
            boolean foundActiveCar = false;
            int nextCarIndex = currentCarIndex;
            while (!foundActiveCar) {
                if (nextCarIndex == track.getCarCount() - 1) {
                    nextCarIndex = 0;
                } else {
                    nextCarIndex++;
                }
                if (!track.getCarCrashStatus(nextCarIndex)) {
                    foundActiveCar = true;
                }
            }
            currentCarIndex = nextCarIndex;
        }
    }


    /**
     * Returns all of the grid positions in the path between two positions, for use in determining line of sight.
     * Determine the 'pixels/positions' on a raster/grid using Bresenham's line algorithm.
     * (https://de.wikipedia.org/wiki/Bresenham-Algorithmus)
     * Basic steps are
     * - Detect which axis of the distance vector is longer (faster movement)
     * - for each pixel on the 'faster' axis calculate the position on the 'slower' axis.
     * Direction of the movement has to correctly considered
     *
     * @param startPosition Starting position as a PositionVector
     * @param endPosition   Ending position as a PositionVector
     * @return Intervening grid positions as a List of PositionVector's, including the starting and ending positions.
     */
    public List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition) {
        List<PositionVector> posList = new ArrayList<>();
        int diffX = endPosition.getX() - startPosition.getX();
        int diffY = endPosition.getY() - startPosition.getY();
        int distX = Math.abs(diffX);
        int distY = Math.abs(diffY);
        int dirX = Integer.signum(diffX);
        int dirY = Integer.signum(diffY);
        int parallelStepX, parallelStepY;
        int diagonalStepX, diagonalStepY;
        int distanceSlowAxis, distanceFastAxis;
        if (distX > distY) {
            parallelStepX = dirX;
            parallelStepY = 0;
            diagonalStepX = dirX;
            diagonalStepY = dirY;
            distanceSlowAxis = distY;
            distanceFastAxis = distX;
        } else {
            parallelStepX = 0;
            parallelStepY = dirY;
            diagonalStepX = dirX;
            diagonalStepY = dirY;
            distanceSlowAxis = distX;
            distanceFastAxis = distY;
        }
        int x = startPosition.getX();
        int y = startPosition.getY();
        int error = distanceFastAxis / 2;
        posList.add(new PositionVector(startPosition.getX(), startPosition.getY()));
        for (int step = 0; step < distanceFastAxis; step++) {
            error -= distanceSlowAxis;
            if (error < 0) {
                error += distanceFastAxis;
                x += diagonalStepX;
                y += diagonalStepY;
            } else {
                x += parallelStepX;
                y += parallelStepY;
            }
            posList.add(new PositionVector(x, y));
        }
        return posList;
    }

    /**
     * Does indicate if a car would have a crash with a WALL space or another car at the given position.
     *
     * @param carIndex The zero-based carIndex number
     * @param position A PositionVector of the possible crash position
     * @return A boolean indicator if the car would crash with a WALL or another car.
     */
    public boolean willCarCrash(int carIndex, PositionVector position) {
        Config.SpaceType spaceType = track.getSpaceType(position);
        return spaceType == Config.SpaceType.CAR ||
            spaceType == Config.SpaceType.WALL;
    }

    private boolean willCarReachFinishLine(PositionVector currentPosition) {
        Config.SpaceType spaceType = track.getSpaceType(currentPosition);
        return spaceType == Config.SpaceType.FINISH_DOWN ||
            spaceType == Config.SpaceType.FINISH_LEFT ||
            spaceType == Config.SpaceType.FINISH_RIGHT ||
            spaceType == Config.SpaceType.FINISH_UP;
    }

    public void setStrategy(int carID, Config.StrategyType strategyType, String pathName) {
        Car car = track.getCar(carID);
        car.setStrategyType(strategyType, pathName);
    }

    public Config.StrategyType getStrategy(int carID) {
        Car car = track.getCar(carID);
        return car.getStrategyType();
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
