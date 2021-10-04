package ch.zhaw.pm2.racetrack.strategy;

import ch.zhaw.pm2.racetrack.PositionVector;
import ch.zhaw.pm2.racetrack.PositionVector.Direction;
import ch.zhaw.pm2.racetrack.PositionVectorHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PathFollower implements MoveStrategy {
    private BufferedReader reader;
    private ArrayList<PositionVector> arrayListPositionVector;
    private ArrayList<Direction> directionArrayList;

    public PathFollower(String trackFile, String carCharacter, PositionVector startPosition) {
        directionArrayList = new ArrayList<>();
        try {
            String followerFileName = trackFile.replace("tracks/", "").replace(".txt", "").replace("tracks\\", "") + "_" + carCharacter + ".txt";

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Follower", followerFileName))));
            String line;
            arrayListPositionVector = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    String[] vectorString = line.split(";");
                    Integer vectorX = Integer.parseInt(vectorString[0]);
                    Integer vectorY = Integer.parseInt(vectorString[1]);
                    arrayListPositionVector.add(new PositionVector(vectorX, vectorY));
                }
            }
            arrayListPositionVector.add(0, startPosition);
        } catch (IOException e) {
            System.out.println("Error: " + " : " + e.getMessage());
        }
        calculateDirections();
    }

    private void calculateDirections() {
        PositionVector velocity = new PositionVector(0,0);
        for (int index = 0; index < arrayListPositionVector.size(); index++) {
            PositionVector currentPosition = arrayListPositionVector.get(index);
            PositionVector nextPosition = new PositionVector(0,0);
            if(index+1 < arrayListPositionVector.size()) {
                nextPosition = arrayListPositionVector.get(index+1);
            } else {
                nextPosition = arrayListPositionVector.get(0);
            }

            // Diffrence between actual Position and next Position
            PositionVector distanceBetweenTwoPoints = PositionVectorHelper.subtract(nextPosition, currentPosition);

            // Substract the diffrence between two Points and the velocity
            PositionVector actualVelocity = PositionVectorHelper.subtract(distanceBetweenTwoPoints, velocity);

            Direction direction = switchDirection(actualVelocity);
            velocity = PositionVectorHelper.add(direction.vector, velocity);
            directionArrayList.add(direction);
        }
    }

    private Direction switchDirection(PositionVector position) {
        if(position.getX() == -1 && position.getY() == 1)
            return Direction.DOWN_LEFT;
        if (position.getX() == 0 && position.getY() == 1)
            return Direction.DOWN;
        if(position.getX() == 1 && position.getY() == 1)
            return Direction.DOWN_RIGHT;
        if (position.getX() == -1 && position.getY() == 0)
            return Direction.LEFT;
        if (position.getX() == 0 && position.getY() == 0)
            return Direction.NONE;
        if (position.getX() == 1 && position.getY() == 0)
            return Direction.RIGHT;
        if (position.getX() == -1 && position.getY() == -1)
            return Direction.UP_LEFT;
        if (position.getX() == 0 && position.getY() == -1)
            return Direction.UP;
        if (position.getX() == 1 && position.getY() == -1)
            return Direction.UP_RIGHT;
        else
            return Direction.NONE;
    }

    @Override
    public PositionVector.Direction nextMove() {
        if(directionArrayList.size() < 1) {
            return Direction.NONE;
        }
        Direction direction = directionArrayList.get(0);
        directionArrayList.remove(0);
        return direction;
    }
}
