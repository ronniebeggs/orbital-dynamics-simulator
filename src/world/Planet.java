package world;
import java.awt.Color;

public class Planet extends Satellite {
    public double radius;
    public Color color;
    public Planet(Satellite parent, double radius, Color color, int mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, x, y, xVelocity, yVelocity, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.radius = radius;
        this.color = color;
    }

//    public Planet(Satellite parent, int mass, double orbitalRadius, Color color, double radius) {
//        double xPosition = 0;
//        double yPosition = 0;
//        double xVelocity = 0;
//        double yVelocity = 0;
//        double orbitalVelocity = 0;
//        double trueAnomaly = 0;
//        this(parent, mass, xPosition, yPosition, xVelocity, yVelocity, orbitalRadius, orbitalVelocity, trueAnomaly);
//    }
}
