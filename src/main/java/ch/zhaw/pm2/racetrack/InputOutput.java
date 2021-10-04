package ch.zhaw.pm2.racetrack;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.io.File;

/**
 * InputOutput class, manages input and output of any sort.
 *
 * @author Adrian Eyholzer
 */
public class InputOutput {

    TextIO textIO = TextIoFactory.getTextIO();
    TextTerminal<?> textTerminal = textIO.getTextTerminal();
    Config config = new Config();

    /**
     * Prints welcome message and a stylized racecar.
     */
    public void printWelcomeMessage() {
        textTerminal.println("\n___Welcome to Racetrack!___The best game from professional programmers");
    }

    /**
     * Prints the the winner announcement.
     */
    public void printWinner(char winner) {
        textTerminal.println("Player " + winner + " has won! Congratulations!");
    }

    /**
     * Prints a message with the current player.
     */
    public void printCurrentPlayer(char winner) {
        textTerminal.println("Player " + winner + "'s turn:");
    }

    /**
     * Prints the actions menu.
     *
     * @return value of selected menu action as MenuAction Object
     */
    public MenuFeedback printMenu() {

        MenuFeedback returnValues = new MenuFeedback();
        String choice = askForDirectionOrAction();


        if (choice.equals("j")) {
            returnValues.setMenuAction(MenuAction.HELP);

        } else if (choice.equals("k")) {
            returnValues.setMenuAction(MenuAction.RESTART);

        } else if (choice.equals("l")) {
            returnValues.setMenuAction(MenuAction.QUIT);
        } else {

            returnValues.setDirection(getDirectionFromEnum(choice));
            returnValues.setMenuAction(MenuAction.MOVECAR);

        }
        return returnValues;
    }

    private String askForDirectionOrAction() {
        String regexString = "[qweasdyxcjkl]";

        String selectedDirection = textIO.newStringInputReader().withPattern(regexString)
            .read("Select a vector by pushing the according key:\n\n" +
                "q|w|e\n" +
                "-----\n" +
                "a|s|d\n" +
                "-----\n" +
                "y|x|c\n" +
                "\n" +
                "j for help, k to restart, l to quit");
        return selectedDirection;
    }

    private PositionVector.Direction getDirectionFromEnum(String selectedDirection) {

        switch (selectedDirection) {
            default:
                return PositionVector.Direction.NONE;
            case "q":
                return PositionVector.Direction.UP_LEFT;
            case "w":
                return PositionVector.Direction.UP;
            case "e":
                return PositionVector.Direction.UP_RIGHT;
            case "a":
                return PositionVector.Direction.LEFT;
            case "s":
                return PositionVector.Direction.NONE;
            case "d":
                return PositionVector.Direction.RIGHT;
            case "y":
                return PositionVector.Direction.DOWN_LEFT;
            case "x":
                return PositionVector.Direction.DOWN;
            case "c":
                return PositionVector.Direction.DOWN_RIGHT;
        }
    }

    /**
     * Prints the strategy menu.
     *
     * @return value of selected menu action as StrategyType Object
     */
    public Config.StrategyType printStrategyMenu() {

        Config.StrategyType menu = textIO.newEnumInputReader(Config.StrategyType.class)
            .read("Select a move strategy for your car:");
        switch (menu) {
            default:
                throw new IllegalStateException("\nInternal error found - Command not implemented.\n");
            case DO_NOT_MOVE:
                return Config.StrategyType.DO_NOT_MOVE;
            case USER:
                return Config.StrategyType.USER;
            case MOVE_LIST:
                return Config.StrategyType.MOVE_LIST;
            case PATH_FOLLOWER:
                return Config.StrategyType.PATH_FOLLOWER;
        }
    }

    /**
     * This enum class contains all possible menu actions
     */
    public enum MenuAction {
        MOVECAR, HELP, RESTART, QUIT
    }

    /**
     * Prints the momentary state of the game field
     */
    public void printTrack(String trackString) {
        clearWindow();
        textTerminal.println(trackString);
    }

    /**
     * Shows help text, to explain the game rules and functionality
     */
    public void printHelp() {
        textTerminal.println("HELP\n" +
            "To move your car you need to select a direction vector. Please note, that additionally to your selected vector for that round, your car will also move with the vector from the previous round. With the grid below you can easily select the next direction vector for your car.\n" +
            "\nq|w|e\n" +
            "-----\n" +
            "a|s|d\n" +
            "-----\n" +
            "y|x|c\n\n" +
            "Example: First round (d) (0|0) + (1|0) to the right\n" +
            "         Second round (d) (1|0) + (1|0) => (2|0) to the right\n" +
            "         Third round (e) (2|0) + (1|1) => (3|1)\n" +
            "So in the 3rd round your car will move three characters to the right and one upwards.\n" +
            "ATTENTION: s = (0|0), means the car keeps its previous vector for the current round.\n");
    }

    /**
     * Prints a warning for the selected movement strategy, to inform that no movement is possible.
     */
    public void printWarning() {
        textTerminal.println("No movement possible. Please reselect strategy.");
    }

    /**
     * Prints no winner.
     */
    public void noWinnerPossible() { textTerminal.println("No moving car left. Can't detect winner."); }
    /**
     * Prints a message that a file could not be found.
     */
    public void printFileNotFound() {
        textTerminal.println("Can't find file.");
    }

    /**
     * Prints a message to inform the user that the selected track is invalid.
     */
    public void printInvalidTrack() {
        textTerminal.println("Track is not valid.");
    }

    /**
     * Asks the user if he wants to start a new game
     *
     * @return restartWanted true when a new game needs to be started
     */
    public boolean askForRestart() {
        return textIO.newBooleanInputReader().withFalseInput("n").withTrueInput("y").read("Do you want to play another game?");
    }

    /**
     * Asks the user if he wants to stop the game
     *
     * @return true when the game should be stopped
     */
    public boolean askForQuit() {
        return textIO.newBooleanInputReader().withFalseInput("n").withTrueInput("y").read("Do you really want to quit?");
    }

    /**
     * Asks the user if he wants to continue the game
     *
     * @return true when the user wants to continue with this
     */
    public void askToContinue() {
        String regexString = "[j]";
        textIO.newStringInputReader().withPattern(regexString).read("press j to continue");
    }

    /**
     * Searches the tracks directory for .txt files and returns them as list of File objects
     *
     * @param config contains among others the pathname of the track files
     * @return a list of files that are in the tracks directory as File object
     */
    private File[] getTrackFilenames(Config config) {
        final File currentDir = config.getTrackDirectory();
        final String extension = ".txt";
        return currentDir.listFiles((File pathname) -> pathname.getName().endsWith(extension));
    }

    /**
     * Prints a menu to choose from all possible tracks residing in the tracks directory
     *
     * @return filename of selected Track as File object
     */
    public File printTrackSelectionMenu() {
        File[] files = getTrackFilenames(config);
        textTerminal.println("Please select a track!");

        for (int i = 0; i < files.length; i++) {
            textTerminal.printf("%d. %s\n", i + 1, files[i].toString().replace("tracks/", ""));
        }

        int selectedTrack = textIO.newIntInputReader().withMinVal(1).withMaxVal(files.length).read("To select a track, please enter its number:") - 1;
        textTerminal.printf(files[selectedTrack].toString().replace("/", "").replace(".txt", ".txt\n"));
        return new File(files[selectedTrack].toString());
    }

    /**
     * Setter for the empty window bookmark.
     */
    public void setCleanWindow() {
        textTerminal.setBookmark("clean");
    }

    /**
     * Setter for the start window bookmark.
     */
    public void setStartWindow() {
        textTerminal.setBookmark("start");
    }

    public void clearWindow() {
        textTerminal.resetToBookmark("clean");
    }

    public void clearToStartWindow() {
        textTerminal.resetToBookmark("start");
    }

    public void closeTerminalWindow() {
        textTerminal.dispose();
    }



}
