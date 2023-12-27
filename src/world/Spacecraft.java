package world;

public class Spacecraft extends Satellite {
    public double shipSize;
    public double direction;
    public double relativeXVelocity;
    public double relativeYVelocity;
    public Spacecraft(Satellite parent, int mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, x, y, xVelocity, yVelocity, orbitalRadius, orbitalVelocity, trueAnomaly);
    }
}
