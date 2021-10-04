package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.PositionVector.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarTest {
    private Car carA;
    private Car carB;
    private Car carC;
    private PositionVector zeroPosition;

    @BeforeEach
    public void initializeCar() {
        carA = new Car('A', 0,0);
        carB = new Car('B', 0, 0);
        carC = new Car('C',0, 0);
    }

    @Test
    public void carHasID() {
        assertEquals(carA.getID(), 'A');
        assertEquals(carB.getID(), 'B');
        assertEquals(carC.getID(), 'C');
    }

    @Test
    public void carHasPosition() {
        assertEquals(new PositionVector(0, 0), carA.getPosition());
        assertEquals(new PositionVector(0, 0), carB.getPosition());
        assertEquals(new PositionVector(0, 0), carC.getPosition());

        PositionVector beforeSpeed = carA.getVelocity();
        carA.accelerate(Direction.RIGHT);
        System.out.printf("Acceleration after go to right %s\n", carA.getVelocity());
        carA.move(carA.nextPosition());

        assertEquals(carA.getPosition(), PositionVectorHelper.add(beforeSpeed, Direction.RIGHT.vector));
        System.out.printf("Actual Position %s\n", carA.getPosition());

        beforeSpeed = carA.getVelocity();
        carA.accelerate(Direction.LEFT);

        carA.move(carA.nextPosition());
        assertEquals(carA.getVelocity(), PositionVectorHelper.add(beforeSpeed, Direction.LEFT.vector));
        System.out.printf("Speed: %s\n", carA.getVelocity());
        System.out.printf("Actual Position %s\n", carA.getPosition());


        carA.accelerate(Direction.DOWN_LEFT);

        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.DOWN_RIGHT);
        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.UP_RIGHT);
        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.UP_LEFT);

        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.UP);
        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.DOWN);
        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());

        carA.accelerate(Direction.NONE);
        carA.move(carA.nextPosition());
        System.out.printf("Speed: %s\n", carA.getVelocity());
    }

    @Test
    public void carHasVelocity() {

    }

    @Test
    public void carIsCrashed() {
        carA.crash(new PositionVector(0,0));
        assertTrue(carA.isCrashed());
    }

    @Test
    public void carCanMove() {
        carA.accelerate(Direction.UP);
        carA.move(carA.nextPosition());
        assertEquals(new PositionVector(0,-1),carA.getPosition());
    }

    @Test
    public void carHasAccelerate() {

    }
}
