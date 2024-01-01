package world;

import util.Coordinate;

public class Spacecraft extends Satellite {
    public double shipSize;
    public double direction;
    public double relativeXVelocity;
    public double relativeYVelocity;
    public Spacecraft(Satellite parent, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.shipSize = 1000;
    }
    public void engageThrust(int thrustDirection, double percentIncrease) {
        Coordinate velocity = getVelocity();
        double straightVelocity = Math.sqrt(
                Math.pow(velocity.getX(), 2) + Math.pow(velocity.getY(), 2)
        );
        double thrustMagnitude = thrustDirection * straightVelocity * percentIncrease;
        double craftDirection = Math.atan2(velocity.getY(), velocity.getX());

        velocity.shiftX(thrustMagnitude * Math.cos(craftDirection));
        velocity.shiftY(thrustMagnitude * Math.sin(craftDirection));
    }
}
