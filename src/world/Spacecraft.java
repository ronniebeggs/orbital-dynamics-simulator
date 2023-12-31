package world;

public class Spacecraft extends Satellite {
    public double shipSize;
    public double direction;
    public double relativeXVelocity;
    public double relativeYVelocity;
    public Spacecraft(Satellite parent, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.shipSize = 1000;
    }
}
