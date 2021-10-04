package ch.zhaw.pm2.racetrack.strategy;

import ch.zhaw.pm2.racetrack.PositionVector.Direction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Returns a Direction vector object parsed from a specific track follower-file containing predefined movement vectors.
 *
 * @return Direction object
 */
public class FileMovement implements MoveStrategy {
    public BufferedReader reader;
    private ArrayList<Direction> arrayListMove;

    public FileMovement(String trackFile, String carCharacter) {
        try {
            String followerFileName = trackFile.replace("tracks/", "").replace(".txt", "").replace("tracks\\", "") + "_" + carCharacter + ".txt";

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("FileMovement", followerFileName))));
            String line;
            arrayListMove = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    arrayListMove.add(Direction.valueOf(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + " : " + e.getMessage());
        }
    }

    @Override
    public Direction nextMove() {
        Direction nextDirection = arrayListMove.get(0);
        arrayListMove.remove(0);
        return nextDirection;
    }
}
