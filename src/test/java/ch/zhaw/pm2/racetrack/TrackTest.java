package ch.zhaw.pm2.racetrack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

public class TrackTest {
    private Track validTestTrack;

    @BeforeEach
    void initializeValidTrack() throws FileNotFoundException, InvalidTrackFormatException {
        File trackFile = new File("src/test/resources/trackForTest.txt");
        validTestTrack = new Track(trackFile);
    }

    @AfterEach
    void tearDown() {
        validTestTrack = null;
    }

    @Test
    void shouldReturnCorrectPositionVectorFromList() {
        assertEquals(new PositionVector(24, 0), validTestTrack.getTrackPositionVectors().get(24));
        assertEquals(new PositionVector(7, 1), validTestTrack.getTrackPositionVectors().get(32));
        assertEquals(new PositionVector(4, 4), validTestTrack.getTrackPositionVectors().get(104));
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        assertEquals("#########################\n" +
                        "### B      @ ############\n" +
                        "######v    >#######   ###\n" +
                        "######^   A   ###########\n" +
                        "#####<      v<^>=     ###\n" +
                        "#########################",
            validTestTrack.toString());
    }

    @Test
    void shouldReturnCarInCorrectInitializingOrder() {
        assertEquals('B', validTestTrack.getCarId(0));
        assertEquals('@', validTestTrack.getCarId(1));
        assertEquals('A', validTestTrack.getCarId(2));
        assertEquals('=', validTestTrack.getCarId(3));
        assertEquals(4, validTestTrack.getCarCount());
    }

    @Test
    void shouldUpdateTheCarToTheCorrectPosition() {
        PositionVector newPosition = new PositionVector(11, 4);
        PositionVector oldPosition = new PositionVector(11, 3);
        //Move the car on Track
        validTestTrack.updateCarOnTrack(newPosition,2);
        //Check if new position if from correct SpaceType and check if correct character is displayed
        assertEquals(Config.SpaceType.CAR, validTestTrack.getSpaceType(newPosition));
        assertEquals('A', validTestTrack.getTrackParser().getTrackCharacters().get(validTestTrack.getIndexOnTrackFromPositionVector(newPosition)));
        //Check if old position is from correct SpaceType and check if correct character is displayed
        assertEquals(Config.SpaceType.TRACK, validTestTrack.getSpaceType(oldPosition));
        assertEquals(' ', validTestTrack.getTrackParser().getTrackCharacters().get(validTestTrack.getIndexOnTrackFromPositionVector(oldPosition)));
    }

    @Test
    void shouldDisplayCorrectCarAfterCollisionWithAnotherCar() {
        PositionVector newPosition = new PositionVector(4, 1);
        PositionVector oldPosition = new PositionVector(11, 3);
        //Move the car on Track
        validTestTrack.updateCarOnTrack(newPosition,2);
        //Check if new position if from correct SpaceType and check if correct character is displayed
        assertEquals(Config.SpaceType.CAR, validTestTrack.getSpaceType(newPosition));
        assertEquals('B', validTestTrack.getTrackParser().getTrackCharacters().get(validTestTrack.getIndexOnTrackFromPositionVector(newPosition)));
        //Check if old position is from correct SpaceType and check if correct character is displayed
        assertEquals(Config.SpaceType.TRACK, validTestTrack.getSpaceType(oldPosition));
        assertEquals(' ', validTestTrack.getTrackParser().getTrackCharacters().get(validTestTrack.getIndexOnTrackFromPositionVector(oldPosition)));
    }

    @Test
    void shouldThrowInvalidTrackFormatExceptionForANotRectangleTrack() {
        InvalidTrackFormatException invalidTrackFormatException = assertThrows(InvalidTrackFormatException.class, () -> {
            File trackFile = new File("src/test/resources/trackForTest_notARectangle.txt");
            new Track(trackFile);
        });

        String expectedMessage = "This Track-File is invalid - the Track isn't a rectangle!";
        String actualMessage = invalidTrackFormatException.toString();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowInvalidTrackFormatExceptionForANotByWallsSurroundedTrack() {
        InvalidTrackFormatException invalidTrackFormatException = assertThrows(InvalidTrackFormatException.class, () -> {
            File trackFile = new File("src/test/resources/trackForTest_NotSurroundedByWalls.txt");
            new Track(trackFile);
        });

        String expectedMessage = "This Track-File is invalid - Track is not surrounded by WALLS.";
        String actualMessage = invalidTrackFormatException.toString();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowInvalidTrackFormatExceptionForATrackWithTooManyCars() {
        InvalidTrackFormatException invalidTrackFormatException = assertThrows(InvalidTrackFormatException.class, () -> {
            File trackFile = new File("src/test/resources/trackForTest_tooManyCars.txt");
            new Track(trackFile);
        });

        String expectedMessage = "This Track-File is invalid - there are too many cars on the track.";
        String actualMessage = invalidTrackFormatException.toString();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowInvalidTrackFormatExceptionForATrackWithATooLowGridHeight() {
        InvalidTrackFormatException invalidTrackFormatException = assertThrows(InvalidTrackFormatException.class, () -> {
            File trackFile = new File("src/test/resources/trackForTest_gridHeightIsZero.txt");
            new Track(trackFile);
        });

        String expectedMessage = "This Track-File is invalid - the grid height is zero!";
        String actualMessage = invalidTrackFormatException.toString();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

