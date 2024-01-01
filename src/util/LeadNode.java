package util;

public class LeadNode {
    public Coordinate position;
    public Coordinate velocity;
    public LeadNode(Coordinate position, Coordinate velocity) {
        this.position = position;
        this.velocity = velocity;
    }
    public Coordinate getPosition() {
        return position;
    }
    public Coordinate getVelocity() {
        return velocity;
    }
}

