package ch.zhaw.pm2.racetrack.strategy;

import ch.zhaw.pm2.racetrack.PositionVector;

/**
 * Returns a default Direction vector object. This move strategy implies a user input.
 *
 * @return default Direction object
 */
public class UserMovement implements MoveStrategy {

    @Override
    public PositionVector.Direction nextMove() {
        return PositionVector.Direction.NONE;
    };
}
