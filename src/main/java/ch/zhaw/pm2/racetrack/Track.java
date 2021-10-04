package ch.zhaw.pm2.racetrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the racetrack board.
 *
 * <p>The racetrack board consists of a rectangular grid of 'width' columns and 'height' rows.
 * The zero point of the grid is at the top left. The x-axis points to the right and the y-axis points downwards.</p>
 * <p>Positions on the track grid are specified using {@link PositionVector} objects. These are vectors containing an
 * x/y coordinate pair, pointing from the zero-point (top-left) to the addressed space in the grid.</p>
 *
 * <p>Each position in the grid represents a space which can hold an enum object of type {@link Config.SpaceType}.<br>
 * Possible Space types are:
 * <ul>
 *  <li>WALL : road boundary or off track space</li>
 *  <li>TRACK: road or open track space</li>
 *  <li>FINISH_LEFT, FINISH_RIGHT, FINISH_UP, FINISH_DOWN :  finish line spaces which have to be crossed
 *      in the indicated direction to winn the race.</li>
 * </ul>
 * <p>Beside the board the track contains the list of cars, with their current state (position, velocity, crashed,...)</p>
 *
 * <p>At initialization the track grid data is read from the given track file. The track data must be a
 * rectangular block of text. Empty lines at the start are ignored. Processing stops at the first empty line
 * following a non-empty line, or at the end of the file.</p>
 * <p>Characters in the line represent SpaceTypes. The mapping of the Characters is as follows:
 * <ul>
 *   <li>WALL : '#'</li>
 *   <li>TRACK: ' '</li>
 *   <li>FINISH_LEFT : '&lt;'</li>
 *   <li>FINISH_RIGHT: '&gt;'</li>
 *   <li>FINISH_UP   : '^;'</li>
 *   <li>FINISH_DOWN: 'v'</li>
 *   <li>Any other character indicates the starting position of a car.<br>
 *       The character acts as the id for the car and must be unique.<br>
 *       There are 1 to {@link Config#MAX_CARS} allowed. </li>
 * </ul>
 * </p>
 * <p>All lines must have the same length, used to initialize the grid width).
 * Beginning empty lines are skipped.
 * The the tracks ends with the first empty line or the file end.<br>
 * An {@link InvalidTrackFormatException} is thrown, if
 * <ul>
 *   <li>not all track lines have the same length</li>
 *   <li>the file contains no track lines (grid height is 0)</li>
 *   <li>the file contains more than {@link Config#MAX_CARS} cars</li>
 * </ul>
 *
 * <p>The Track can return a String representing the current state of the race (including car positons)</p>
 */

/**
 * @author David Mihajlovic
 */
public class Track {

    private TrackParser trackParser;
    private List<Car> cars;
    private List<Config.SpaceType> trackSpaceTypes;
    private final Integer width;
    private final Integer height;

    /**
     * Initialize a Track from the given track file.
     *
     * @param trackFile Reference to a file containing the track data
     * @throws FileNotFoundException       if the given track file could not be found
     * @throws InvalidTrackFormatException if the track file contains invalid data (no tracklines, no
     */
    public Track(File trackFile) throws FileNotFoundException, InvalidTrackFormatException {
        cars = new ArrayList<>();
        trackSpaceTypes = new ArrayList<>();
        trackParser = new TrackParser(trackFile);
        width = trackParser.getWidth();
        height = trackParser.getHeight();
        addCarAndSpaceTypeToArrayList();
        checkForTooManyCarsOnTrack();
    }

    public TrackParser getTrackParser() {
        return trackParser;
    }

    public Car getCar(int carNumber) {
        return cars.get(carNumber);
    }

    public int getCarCount() {
        return cars.size();
    }

    public char getCarId(int carNumber) {
        return cars.get(carNumber).getID();
    }

    public PositionVector getCarPos(int carNumber) {
        return cars.get(carNumber).getPosition();
    }

    public PositionVector getCarVelocity(int carNumber) {
        return cars.get(carNumber).getVelocity();
    }

    public boolean getCarCrashStatus(int carNumber) {
        return cars.get(carNumber).getCarCrashStatus();
    }

    public List<PositionVector> getTrackPositionVectors() {
        return trackParser.getTrackPositionVector();
    }

    private PositionVector getPositionVectorFromIndex(int index) {
        return trackParser.getTrackPositionVector().get(index);
    }

    public Integer getIndexOnTrackFromPositionVector (PositionVector position) {
        return getTrackPositionVectors().indexOf(position);
    }

    /**
     * Returns the {@link Config.SpaceType} from a given {@link PositionVector}
     *
     * @param positionVector position to retrieve {@link Config.SpaceType} from
     * @return {@link Config.SpaceType}
     */
    public Config.SpaceType getSpaceType(PositionVector positionVector) {
        int index = trackParser.getTrackPositionVector().indexOf(positionVector);
        return trackSpaceTypes.get(index);
    }

    /**
     * Updates the position of the car on the track
     *
     * @param newPosition the position to be updated with the Car-character and SpaceType.CAR
     * @param carIndex    the car for which the update needs to be executed
     */
    public void updateCarOnTrack(PositionVector newPosition, int carIndex) {
        Character track = ' ';
        //Get the list indices of the to be modified points
        int indexOldPosition = getIndexOnTrackFromPositionVector(cars.get(carIndex).getPosition());
        int indexNewPosition = getIndexOnTrackFromPositionVector(newPosition);
        //set old position to a space " " and SpaceType.TRACK (--> if crash with another Car)
        if (getSpaceType(newPosition).equals(Config.SpaceType.CAR)) {
            trackParser.getTrackCharacters().set(indexOldPosition, track);
            trackSpaceTypes.set(indexOldPosition, Config.SpaceType.TRACK);
        } else {
            //set new position to the CarID and SpaceType.CAR (--> No Crash)
            trackParser.getTrackCharacters().set(indexOldPosition, track);
            trackSpaceTypes.set(indexOldPosition, Config.SpaceType.TRACK);
            trackParser.getTrackCharacters().set(indexNewPosition, getCarId(carIndex));
            trackSpaceTypes.set(indexNewPosition, Config.SpaceType.CAR);
        }
    }

    /**
     * Returns a String representation of the given track
     *
     * @return string of the given track
     */
    @Override
    public String toString() {
        StringBuilder track = new StringBuilder();
        int lengthOfRow = width - 1;
        for (int lengthOfTrack = 0; lengthOfTrack < trackParser.getTrackCharacters().size(); lengthOfTrack++) {
            if (lengthOfTrack == lengthOfRow && lengthOfRow != trackParser.getTrackCharacters().size() - 1) {
                track.append(trackParser.getTrackCharacters().get(lengthOfTrack)).append("\n");
                lengthOfRow += width;
            } else {
                track.append(trackParser.getTrackCharacters().get(lengthOfTrack));
            }
        }
        return track.toString();
    }

    private void addCarAndSpaceTypeToArrayList() throws InvalidTrackFormatException {
        for (Character character : trackParser.getTrackCharacters()) {
            if (isCar(character)) {
                trackSpaceTypes.add(Config.SpaceType.CAR);
                initializeAndAddValidCarToList(character, getPositionVectorFromIndex(trackParser.getTrackCharacters().indexOf(character)));
            } else {
                trackSpaceTypes.add(trackParser.getSpaceTypeOfCharacter(character));
            }
        }
    }

    private Boolean isCar(Character character) {
        for (Config.SpaceType space : Config.SpaceType.values()) {
            if (space.getChar() == character) {
                return false;
            }
        }
        return true;
    }

    private void initializeAndAddValidCarToList(Character character, PositionVector position) throws InvalidTrackFormatException {
        Car car = new Car(character, position.getX(), position.getY());
        checkForIdenticalCarsOnTrack(character);
        cars.add(car);
    }

    private void checkForTooManyCarsOnTrack() throws InvalidTrackFormatException {
        if (!(cars.size() <= Config.MAX_CARS)) {
            throw new InvalidTrackFormatException("This Track-File is invalid - there are too many cars on the track.");
        }
    }

    private void checkForIdenticalCarsOnTrack(Character character) throws InvalidTrackFormatException {
        for (Car carInList : cars) {
            if (carInList.getID().equals(character)) {
                throw new InvalidTrackFormatException("This Track-File is invalid - there are identical cars on the track!");
            }
        }
    }


}
