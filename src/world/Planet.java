package world;
import java.awt.Color;

public class Planet extends Satellite {
    public double radius;
    public Color color;
    public Planet(Color color, double radius, double mass) {
        this(null, radius, color, mass, 0, 0, 0, 0, 0, 0, 0);
    }
    public Planet(Planet parent, double radius, Color color, double mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, x, y, xVelocity, yVelocity, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.radius = radius;
        this.color = color;
    }
}
