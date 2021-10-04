package ch.zhaw.pm2.racetrack;

/**
 * Exception type for wrong track format
 */
public class InvalidTrackFormatException extends Exception {

    private String error;

    public InvalidTrackFormatException(String error) {
        this.error = error;
    }

    public String toString() {
        return error;
    }
}
