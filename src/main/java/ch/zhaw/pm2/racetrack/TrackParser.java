package ch.zhaw.pm2.racetrack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackParser {

    private Integer width;
    private Integer height;
    private ArrayList<String> trackLines;
    private List<Character> trackCharacters;
    private List<PositionVector> trackPositionVectors;

    public TrackParser(File trackFile) throws FileNotFoundException, InvalidTrackFormatException {
        trackLines = new ArrayList<>();
        trackCharacters = new ArrayList<>();
        trackPositionVectors = new ArrayList<>();

        readLineByLineIntoArray(initializeReader(trackFile));
        checkIfTrackIsRectangle();

        addCharacterFromLineToArrayList();
        checkIfTrackIsSurroundedByWALLS(getCharactersAroundTrack());

        addPositionVectorToArrayList();
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public List<Character> getTrackCharacters() {
        return trackCharacters;
    }

    public ArrayList<String> getRowArrayList() {
        return trackLines;
    }

    public List<PositionVector> getTrackPositionVector() {
        return trackPositionVectors;
    }

    private ArrayList<String> getColumnArrayList() {
        return generateColumnList();
    }

    private ArrayList<String> generateColumnList() {
        String[] columnStringArray;
        ArrayList<String> columnStringArrayList = new ArrayList<>();
        for (int amountOfLines = 0; amountOfLines < height; amountOfLines++) {
            if (amountOfLines == 0) {
                columnStringArray = trackLines.get(amountOfLines).split("");
                Collections.addAll(columnStringArrayList, columnStringArray);
            } else {
                columnStringArray = trackLines.get(amountOfLines).split("");
                for (int amountOfCharacters = 0; amountOfCharacters < (columnStringArray.length); amountOfCharacters++) {
                    String toAppend = columnStringArray[amountOfCharacters];
                    columnStringArrayList.set(amountOfCharacters, columnStringArrayList.get(amountOfCharacters).concat(toAppend));
                }
            }
        }
        return columnStringArrayList;
    }

    private ArrayList<Integer> getArrayWithLengths(ArrayList<String> stringsToGetLengthOf) throws InvalidTrackFormatException {
        ArrayList<Integer> lengthOfStrings = new ArrayList<>();
        for (String line : stringsToGetLengthOf) {
            lengthOfStrings.add(line.length());
        }
        checkIfEnoughLinesOnTrack(lengthOfStrings);
        return lengthOfStrings;
    }

    private BufferedReader initializeReader(File trackFile) throws FileNotFoundException {
        BufferedReader reader;
        if (trackFile.exists()) {
            FileInputStream fileStream = new FileInputStream(trackFile);
            InputStreamReader input = new InputStreamReader(fileStream);
            reader = new BufferedReader(input);
        } else {
            throw new FileNotFoundException();
        }
        return reader;
    }

    private void readLineByLineIntoArray(BufferedReader reader) throws InvalidTrackFormatException {
        try {
            String line;
            trackLines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                width = line.length();
                if (width > 1) {
                    trackLines.add(line);
                }
            }
            height = trackLines.size();
            reader.close();
        } catch (IOException e) {
            throw new InvalidTrackFormatException("Something went wrong - File couldn't be read.");
        }
    }

    private Boolean compareCharAmountInString(List<Integer> lengthOfStrings) {
        Integer length = lengthOfStrings.get(0);
        for (int current : lengthOfStrings) {
            if (!(length.equals(current))) {
                return false;
            }
        }
        return true;
    }

    private void addCharacterFromLineToArrayList() {
        for (String line : trackLines) {
            for (String character : line.split("")) {
                trackCharacters.add(character.charAt(0));
            }
        }
    }

    private ArrayList<Character> getCharactersAroundTrack() {
        ArrayList<Character> charactersAroundTrack = new ArrayList<>();
        for (int i = 0; i < trackCharacters.size(); i++) {
            if (i < width || i % width == 0 || i % width == 1 || (trackCharacters.size() - width) <= i) {
                charactersAroundTrack.add(trackCharacters.get(i));
            }
        }
        return charactersAroundTrack;
    }

    public Config.SpaceType getSpaceTypeOfCharacter(Character character) {
        switch (character) {
            case ('#'):
                return Config.SpaceType.WALL;
            case (' '):
                return Config.SpaceType.TRACK;
            case ('^'):
                return Config.SpaceType.FINISH_UP;
            case ('v'):
                return Config.SpaceType.FINISH_DOWN;
            case ('<'):
                return Config.SpaceType.FINISH_LEFT;
            case ('>'):
                return Config.SpaceType.FINISH_RIGHT;
            default:
                throw new RuntimeException("Invalid character was given as parameter!");
        }
    }

    private void addPositionVectorToArrayList() {
        for (int currentColumn = 0; currentColumn < height; currentColumn++) {
            for (int currentRow = 0; currentRow < width; currentRow++) {
                trackPositionVectors.add(new PositionVector(currentRow, currentColumn));
            }
        }
        trackPositionVectors = Collections.unmodifiableList(trackPositionVectors);
    }

    private void checkIfTrackIsRectangle() throws InvalidTrackFormatException {
        Boolean rows = compareCharAmountInString(getArrayWithLengths(getRowArrayList()));
        Boolean columns = compareCharAmountInString(getArrayWithLengths(getColumnArrayList()));
        if (!(rows && columns)) {
            throw new InvalidTrackFormatException("This Track-File is invalid - the Track isn't a rectangle!");
        }
    }

    private void checkIfEnoughLinesOnTrack(ArrayList<Integer> lengthOfStrings) throws InvalidTrackFormatException {
        int MIN_GRID_HEIGHT = 3;
        if (lengthOfStrings.size() < MIN_GRID_HEIGHT) {
            throw new InvalidTrackFormatException("This Track-File is invalid - the grid height is zero!");
        }
    }

    private void checkIfTrackIsSurroundedByWALLS(ArrayList<Character> charactersAroundTrack) throws InvalidTrackFormatException {
        boolean surrounded;
        for (Character character : charactersAroundTrack) {
            surrounded = (getSpaceTypeOfCharacter(character).equals(Config.SpaceType.WALL));
            if (!surrounded) {
                throw new InvalidTrackFormatException("This Track-File is invalid - Track is not surrounded by WALLS.");
            }
        }
    }
}
