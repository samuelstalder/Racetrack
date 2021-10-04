package ch.zhaw.pm2.racetrack.strategy;

import ch.zhaw.pm2.racetrack.PositionVector;

/**
 * Returns a (0,0) Direction vector object.
 *
 * @return (0,0) Direction object
 */
public class NoMovement implements MoveStrategy {

    @Override
    public PositionVector.Direction nextMove() {
        return PositionVector.Direction.NONE;
    }

}
