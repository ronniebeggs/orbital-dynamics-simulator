package world;

import util.Coordinate;

import java.awt.*;

public class Spacecraft extends Satellite {
    public double shipSize; // size to be displayed on the screen
    public Spacecraft(Satellite parent, Color color, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, color, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.shipSize = 1000;
    }
    /**
     * Engage the Spacecraft's thrust in a specific direction.
     * @param thrustDirection direction of thrust (1: forward, -1: backward)
     * @param percentIncrease percent change in the velocity in the specified direction.
     * */
    public void engageThrust(int thrustDirection, double percentIncrease) {
        Coordinate velocity = getVelocity();
        double straightVelocity = Math.sqrt(
                Math.pow(velocity.getX(), 2) + Math.pow(velocity.getY(), 2)
        );
        double thrustMagnitude = thrustDirection * straightVelocity * percentIncrease;
        double craftDirection = Math.atan2(velocity.getY(), velocity.getX());
        // apply thrust by incrementing the spacecraft's velocity by a calculated amount
        velocity.shiftX(thrustMagnitude * Math.cos(craftDirection));
        velocity.shiftY(thrustMagnitude * Math.sin(craftDirection));
    }
    /** @return distance between the spacecraft and the first lead position in the deque. */
    public double distanceToFirstLead() {
        Coordinate nextLeadPosition = getLeadPositions().getFirst();
        return nextLeadPosition.distanceTo(getPosition());
    }
}
