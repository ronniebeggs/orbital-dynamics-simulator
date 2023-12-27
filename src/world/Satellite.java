package world;

public class Satellite extends Entity {
    public Satellite parent;
    public String name;
    public double mass;
    public double xVelocity;
    public double yVelocity;
    public double orbitalRadius;
    public double orbitalVelocity;
    public double trueAnomaly;

    public Satellite(Satellite parent, double mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(x, y);
        this.mass = mass;
        this.parent = parent;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.orbitalRadius = orbitalRadius;
        this.orbitalVelocity = orbitalVelocity;
        this.trueAnomaly = trueAnomaly;
    }

}
