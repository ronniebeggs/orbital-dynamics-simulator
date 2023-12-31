package world;
import util.PlanetBuilder;

import java.awt.Color;

public class Planet extends Satellite {
    public double radius;
    public Color color;

    public Planet(String name, Planet parent, Color color, double radius, double mass) {
        this(name, parent, color, radius, mass, 0, 0, 0);
    }
    public Planet(String name, Planet parent, Color color, double radius, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, 0, 0, 0, 0, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.radius = radius;
        this.color = color;
    }
}
