package ch.zhaw.pm2.racetrack;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ControllerTest {

    private InputOutput inputOutput = new InputOutput();
    private Game game = new Game();
    private Track track;
    Controller controller = new Controller();

    @Test
    protected void shouldSimulateGameWithStrategyTypeUser() {
        Controller controller = new Controller();
        String file = "tracks/challenge.txt";
        try {
            game.setTrack(new Track(new File(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidTrackFormatException e) {
            e.printStackTrace();
        }
        track = game.getTrack();
        for (int i = 0; i < track.getCarCount(); i++) {
            game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.USER, file);
            game.switchToNextActiveCar();
        }

        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();

        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.switchToNextActiveCar();

        game.doCarTurn(PositionVector.Direction.UP_RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.UP_RIGHT);
        game.switchToNextActiveCar();

        game.doCarTurn(PositionVector.Direction.UP_RIGHT);
        game.switchToNextActiveCar();
        game.doCarTurn(PositionVector.Direction.UP_RIGHT);
        game.switchToNextActiveCar();

    }

    @Test
    protected void shouldFinishTrackIfNobodyCanMove() {
        String file = "src/test/resources/trackForGameTest.txt";
        try {
            game.setTrack(new Track(new File(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidTrackFormatException e) {
            e.printStackTrace();
        }
        track = game.getTrack();
        game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.DO_NOT_MOVE, file);
        game.switchToNextActiveCar();
        game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.DO_NOT_MOVE, file);
        game.switchToNextActiveCar();
        game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.DO_NOT_MOVE, file);
        game.switchToNextActiveCar();
        game.setStrategy(game.getCurrentCarIndex(), Config.StrategyType.USER, file);

        track = game.getTrack();
        Car carD = track.getCar(3);

        assertFalse(carD.isCrashed());
        assertTrue(controller.existsMovableCar(game));
        assertEquals(Game.NO_WINNER, game.getWinner());
        for (int i = 0; i < 6; i++) {
            game.doCarTurn(PositionVector.Direction.RIGHT);
        }
        game.doCarTurn(PositionVector.Direction.RIGHT);
        assertTrue(carD.isCrashed());

        assertFalse(controller.existsMovableCar(game));
        assertEquals(Game.NO_WINNER, game.getWinner());
    }
}
