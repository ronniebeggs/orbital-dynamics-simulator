package world;

public class Spacecraft extends Satellite {
    public double shipSize;
    public double direction;
    public double relativeXVelocity;
    public double relativeYVelocity;
    public Spacecraft(Satellite parent, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        this(parent, mass, 0, 0, 0, 0, orbitalRadius, orbitalVelocity, trueAnomaly);
    }
    public Spacecraft(Satellite parent, double mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, x, y, xVelocity, yVelocity, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.shipSize = 1000;
    }
}
