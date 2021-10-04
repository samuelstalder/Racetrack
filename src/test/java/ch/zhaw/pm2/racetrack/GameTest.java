package ch.zhaw.pm2.racetrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private InputOutput inputOutput = new InputOutput();
    private Config config = new Config();
    private Game game = new Game();
    private Track track;

    @BeforeEach
    protected void initializeValidTrack() {
        game = new Game();
        try {
            track = new Track(new File("src/test/resources/trackForGameTest.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidTrackFormatException e) {
            e.printStackTrace();
        }
        game.setTrack(track);
    }

    private void initializeTrack(File file) {
        game = new Game();
        try {
            track = new Track(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidTrackFormatException e) {
            e.printStackTrace();
        }
        game.setTrack(track);
    }

    @Test
    protected void shouldCalculatePathTest() {
        PositionVector p1 = new PositionVector(0, 0);
        PositionVector p2 = new PositionVector(7, 4);
        List<PositionVector> posList = new ArrayList<PositionVector>();
        posList.add(new PositionVector(0, 0));
        posList.add(new PositionVector(1, 1));
        posList.add(new PositionVector(2, 1));
        posList.add(new PositionVector(3, 2));
        posList.add(new PositionVector(4, 2));
        posList.add(new PositionVector(5, 3));
        posList.add(new PositionVector(6, 3));
        posList.add(new PositionVector(7, 4));

        List<PositionVector> actualPosList = game.calculatePath(p1, p2);

        assertEquals(posList.get(0), actualPosList.get(0));
        assertEquals(posList.get(1), actualPosList.get(1));
        assertEquals(posList.get(2), actualPosList.get(2));
        assertEquals(posList.get(3), actualPosList.get(3));
        assertEquals(posList.get(4), actualPosList.get(4));
        assertEquals(posList.get(5), actualPosList.get(5));
        assertEquals(posList.get(6), actualPosList.get(6));
        assertEquals(posList.get(7), actualPosList.get(7));
    }

    @Test
    protected void shouldInitializeTrack() throws FileNotFoundException, InvalidTrackFormatException {

    }

    @Test
    protected void shouldSwitchToNextCar() {
        //First car in rotation
        assertEquals(0, game.getCurrentCarIndex());

        game.switchToNextActiveCar();
        assertEquals(1, game.getCurrentCarIndex());

        game.switchToNextActiveCar();
        assertEquals(2, game.getCurrentCarIndex());

        //Last car in rotation
        game.switchToNextActiveCar();
        assertEquals(3, game.getCurrentCarIndex());
        //One complete loop
        game.switchToNextActiveCar();
        assertEquals(0, game.getCurrentCarIndex());
    }


    @Test
    protected void shouldSetCrashedAfterMovingIntoAnotherCarToTrue() {
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.DOWN);
        assertTrue(game.getTrack().getCar(1).getCarCrashStatus());
    }

    @Test
    protected void shouldReturnSpaceTypeCARAfterCrash() {
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.DOWN);
        assertEquals(Config.SpaceType.CAR, game.getTrack().getSpaceType(new PositionVector(26, 23)));
    }

    @Test
    protected void shouldReturnCharacterOfRemainingCarOnTrackAfterCrash() {
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.DOWN);
        assertEquals('d', game.getTrack().getTrackParser().getTrackCharacters().get(game.getTrack().getIndexOnTrackFromPositionVector(new PositionVector(26, 23))));
    }

    @Test
    protected void shouldSetCrashedAfterMovingIntoWALLToTrue() {
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.UP);
        assertTrue(game.getTrack().getCar(1).getCarCrashStatus());
    }

    @Test
    protected void shouldReturnSamePositionIndexAfterGettingBlockedByFinishLine() {
        game.doCarTurn(PositionVector.Direction.LEFT);
        PositionVector positionCarZero = (game.getTrack().getCar(0).getPosition());
        game.doCarTurn(PositionVector.Direction.LEFT);
        assertEquals(1409, game.getTrack().getIndexOnTrackFromPositionVector(positionCarZero));
    }

    @Test
    protected void returnsTrueIfCarWillCrash() {
        PositionVector roadPoint = new PositionVector(30, 1);
        PositionVector offRoadPoint = new PositionVector(30, 0);
        PositionVector otherCarPoint = new PositionVector(24, 25);
        assertFalse(game.willCarCrash(0, roadPoint));
        assertTrue(game.willCarCrash(0, offRoadPoint));
        assertTrue(game.willCarCrash(0, otherCarPoint));
    }

    @Test
    protected void shouldChangePositionAfterCarTurn() {
        initializeTrack(new File("tracks/quarter-mile.txt"));
        game.setStrategy(0, Config.StrategyType.USER, "");
        game.setStrategy(1, Config.StrategyType.USER, "");
        track = game.getTrack();
        Car carA = track.getCar(0);
        Car carB = track.getCar(1);

        assertEquals(56, carA.getPosition().getX());
        assertEquals(56, carB.getPosition().getX());

        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();

        assertEquals(55, carA.getPosition().getX());
        assertEquals(55, carB.getPosition().getX());

        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();

        assertEquals(53, carA.getPosition().getX());
        assertEquals(53, carB.getPosition().getX());

        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.LEFT);
        game.switchToNextActiveCar();

        assertEquals(50, carA.getPosition().getX());
        assertEquals(50, carB.getPosition().getX());

    }

    @Test
    protected void shouldBlockBackDriving() {
        initializeTrack(new File("tracks/challenge.txt"));
        game.setStrategy(0, Config.StrategyType.USER, "");
        game.setStrategy(1, Config.StrategyType.USER, "");
        track = game.getTrack();
        Car carA = track.getCar(0);
        Car carB = track.getCar(1);

        assertEquals(24, carA.getPosition().getX());
        assertEquals(24, carB.getPosition().getX());

        for (int i = 0; i <= 10; i++) {
            game.doCarTurn(PositionVector.Direction.LEFT);
            game.switchToNextActiveCar();
            game.doCarTurn(PositionVector.Direction.LEFT);
            game.switchToNextActiveCar();
            assertEquals(23, carA.getPosition().getX());
            assertEquals(23, carB.getPosition().getX());
        }
    }

    @Test
    protected void shouldReturnWinnerAfterCarCrash() {
        initializeTrack(new File("tracks/challenge.txt"));
        game.setStrategy(0, Config.StrategyType.USER, "");
        game.setStrategy(1, Config.StrategyType.USER, "");
        track = game.getTrack();
        Car carA = track.getCar(0);
        Car carB = track.getCar(1);

        assertEquals(Game.NO_WINNER, game.getWinner());
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();

        assertEquals(Game.NO_WINNER, game.getWinner());
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();

        assertEquals(Game.NO_WINNER, game.getWinner());
        game.doCarTurn(PositionVector.Direction.UP);
        game.switchToNextActiveCar();

        assertEquals(1, game.getWinner());
    }

    @Test
    protected void shouldReturnWinnerAfterReachingFinishLine() {
        initializeTrack(new File("tracks/quarter-mile.txt"));
        game.setStrategy(0, Config.StrategyType.USER, "");
        game.setStrategy(1, Config.StrategyType.USER, "");
        track = game.getTrack();
        Car carA = track.getCar(0);
        Car carB = track.getCar(1);

        for (int i = 0; i <= 9; i++) {
            assertEquals(Game.NO_WINNER, game.getWinner());
            game.doCarTurn(PositionVector.Direction.LEFT);
            game.switchToNextActiveCar();
            game.doCarTurn(PositionVector.Direction.LEFT);
            game.switchToNextActiveCar();
        }
        game.doCarTurn(PositionVector.Direction.LEFT);
        assertEquals(1, game.getWinner());
    }

    @Test
    protected  void shouldReturnWinnerAfterCarCrashWith4Cars() {
        String file = "src/test/resources/trackForGameTest.txt";
        try {
            game.setTrack(new Track(new File(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidTrackFormatException e) {
            e.printStackTrace();
        }

        track = game.getTrack();
        for (int i = 0; i < 4; i++) {
            Car car = track.getCar(i);
            game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.USER, file);
            game.switchToNextActiveCar();
        }
        //  > a b   (a crash into b)
        //  > c d   (c crash into d)
        //a: 0, b: 1, c: 2, d: 3
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.NONE);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.NONE);
        game.switchToNextActiveCar();
        assertFalse(track.getCar(0).isCrashed());
        assertFalse(track.getCar(2).isCrashed());
        assertFalse(track.getCar(1).isCrashed());
        assertFalse(track.getCar(3).isCrashed());
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.NONE);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.NONE);
        game.switchToNextActiveCar();
        Car carA = track.getCar(0);
        Car carB = track.getCar(1);
        Car carC = track.getCar(2);
        Car carD = track.getCar(3);
        assertTrue(track.getCar(0).isCrashed());
        assertTrue(track.getCar(2).isCrashed());
        assertFalse(track.getCar(1).isCrashed());
        assertFalse(track.getCar(3).isCrashed());
        //  >   b   (crash b into d)
        //  >   d
        //a: 0, b: 1, c: 2, d: 3
        assertEquals(Game.NO_WINNER, game.getWinner());
        game.doCarTurn(PositionVector.Direction.DOWN);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.NONE);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.DOWN);
        assertTrue(track.getCar(1).isCrashed());
        assertFalse(track.getCar(3).isCrashed());
        assertEquals(3, game.getWinner());
    }
}

