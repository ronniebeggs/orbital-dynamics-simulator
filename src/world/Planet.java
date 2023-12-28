package world;
import java.awt.Color;

public class Planet extends Satellite {
    public double radius;
    public Color color;
    public Planet(String name, Color color, double radius, double mass) {
        this(name, color, null, radius, mass, 0, 0);
    }
    public Planet(String name, Color color, Planet parent, double radius, double mass, double orbitalRadius, double trueAnomaly) {
        super(parent, mass, 0, 0, 0, 0, orbitalRadius, 0, trueAnomaly);
        this.radius = radius;
        this.color = color;
    }
}
