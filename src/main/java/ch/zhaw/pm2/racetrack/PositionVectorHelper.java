package ch.zhaw.pm2.racetrack;

public class PositionVectorHelper {

    /**
     * Adds two PositionVectors (e.g. car position and velocity vector or two velocity vectors).
     * @param vectorA A position or velocity vector
     * @param vectorB A position or velocity vector
     * @return A new PositionVector holding the result of the addition. If both
     * arguments are positions (not velocity), the result is mathematically
     * correct but meaningless.
     */
    public static PositionVector add(final PositionVector vectorA, final PositionVector vectorB) {
        return new PositionVector(vectorA.getX() + vectorB.getX(), vectorA.getY() + vectorB.getY());
    }

    /**
     * Subtracts two PositionVectors (e.g. car position and velocity vector or two velocity vectors).
     * @param vectorA A position or velocity vector
     * @param vectorB A position or velocity vector
     * @return A new PositionVector holding the result of the addition. If both
     * arguments are positions (not velocity), the result is mathematically
     * correct but meaningless.
     */
    public static PositionVector subtract(final PositionVector vectorA, final PositionVector vectorB) {
        return new PositionVector(vectorA.getX() - vectorB.getX(), vectorA.getY() - vectorB.getY());
    }

    /**
     * Calculates the scalar product (Skalarprodukt) of two 2D vectors. The scalar product
     * multiplies the lengths of the parallel components of the vectors.
     * @param vectorA A position or velocity vector
     * @param vectorB A position or velocity vector
     * @return The scalar product (vectorA * vectorB). Since vectorA and
     * vectorB are PositionVectors, which hold only integer coordinates,
     * the resulting scalar product is an integer.
     */
    public static int scalarProduct(final PositionVector vectorA, final PositionVector vectorB) {
        return (vectorA.getY() * vectorB.getY()) + (vectorA.getX() * vectorB.getX());
    }

}
