package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.InputOutput.MenuAction;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Controls the game flow.
 * View -- Controller -- Model
 *
 * @author Samuel Stalder
 */
public class Controller {

    /**
     * Initializes a track.
     * Asks user for filename. Repeat asking until the track is accepted.
     * Sets track in game.
     */
    private void initTrack(Game game, InputOutput inputOutput) {
        Track track;
        inputOutput.printWelcomeMessage();
        inputOutput.setStartWindow();
        boolean loadingFileSuccessful = false;
        while (!loadingFileSuccessful) {
            try {
                File fileName = inputOutput.printTrackSelectionMenu();
                track = new Track(fileName);
                loadingFileSuccessful = true;
                game.setTrack(track);
                int amountOfNotMovingCars = setCarStrategyType(inputOutput, game, track, fileName);
                while (amountOfNotMovingCars == track.getCarCount()) {
                    inputOutput.printWarning();
                    amountOfNotMovingCars = setCarStrategyType(inputOutput, game, track, fileName);
                }
                inputOutput.clearWindow();
                inputOutput.textTerminal.println(track.toString());
            } catch (FileNotFoundException e) {
                inputOutput.printFileNotFound();
            } catch (InvalidTrackFormatException e) {
                inputOutput.printInvalidTrack();
            }
        }
    }

    /**
     * Asks and Sets StrategyType for every user
     *
     * @return Amount of chosen DO_NOT_MOVE-StrategyType
     */
    private int setCarStrategyType(InputOutput inputOutput, Game game, Track track, File filename) {
        int amountOfNotMovingCars = 0;
        for (int i = 0; i < track.getCarCount(); i++) {
            inputOutput.clearToStartWindow();
            inputOutput.printCurrentPlayer(game.getCarId(game.getCurrentCarIndex()));
            Config.StrategyType carStrategy = inputOutput.printStrategyMenu();
            game.setStrategy(game.getCurrentCarIndex(), carStrategy, filename.toString());
            game.switchToNextActiveCar();
            if (carStrategy == Config.StrategyType.DO_NOT_MOVE) {
                amountOfNotMovingCars++;
            }
        }
        return amountOfNotMovingCars;
    }

    /**
     * Controls the gameflow.
     * Sets directions until the game is over.
     * The direction depends on the chosen strategy-typ.
     * If e winner is found, it starts to.
     *
     * @return true to restart the game; false to end the game;
     */
    private boolean gameRun(Game game, InputOutput inputOutput, MenuFeedback menuFeedback, Track track) {
        while (game.getWinner() == Game.NO_WINNER) {
            inputOutput.printCurrentPlayer(game.getCarId(game.getCurrentCarIndex()));
            Config.StrategyType carStrategy = game.getStrategy(game.getCurrentCarIndex());
            Car car = track.getCar(game.getCurrentCarIndex());
            PositionVector.Direction direction = car.nextMove();
            if (carStrategy == Config.StrategyType.USER) {
                menuFeedback = inputOutput.printMenu();
                MenuAction menuAction = menuFeedback.getMenuAction();
                direction = menuFeedback.getDirection();
                if (menuAction == MenuAction.MOVECAR) {
                    game.doCarTurn(direction);
                    game.switchToNextActiveCar();
                } else if (menuAction == MenuAction.HELP) {
                    inputOutput.clearWindow();
                    inputOutput.printHelp();
                    inputOutput.askToContinue();
                } else if (menuAction == MenuAction.QUIT) {
                    if (inputOutput.askForQuit()) {
                        return false;
                    }
                } else if (menuAction == MenuAction.RESTART) {
                    if (inputOutput.askForRestart()) {
                        inputOutput.clearWindow();
                        return true;
                    }
                }
            } else {
                game.doCarTurn(direction);
                game.switchToNextActiveCar();
            }
            inputOutput.printTrack(track.toString());
            if (!existsMovableCar(game) && game.getWinner() == Game.NO_WINNER) {
                inputOutput.noWinnerPossible();
                return false;
            }
        }
        inputOutput.printTrack(track.toString());
        inputOutput.printWinner(game.getCarId(game.getWinner()));
        if (inputOutput.askForRestart()) {
            return true;
        }
        return false;
    }

    protected boolean existsMovableCar(Game game) {
        Track track = game.getTrack();
        int amount = 0;
        for (int i = 0; i < track.getCarCount(); i++) {
            Car car = track.getCar(i);
            if (!car.isCrashed() && car.getStrategyType() != Config.StrategyType.DO_NOT_MOVE) {
                amount++;
            }
        }
        return amount > 0;
    }

    public static void main(String[] args) {
        InputOutput inputOutput = new InputOutput();
        MenuFeedback menuFeedback = new MenuFeedback();
        Controller controller = new Controller();
        boolean wantToPlay = true;
        while (wantToPlay) {
            Game game = new Game();
            inputOutput.setCleanWindow();
            inputOutput.clearWindow();
            controller.initTrack(game, inputOutput);
            wantToPlay = controller.gameRun(game, inputOutput, menuFeedback, game.getTrack());
            inputOutput.clearWindow();
        }
        inputOutput.closeTerminalWindow();
    }
}
