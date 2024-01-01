package world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Planet extends Satellite {
    public String name;
    public Color color;
    public double radius;
    public Planet(String name, Color color, double radius, double mass) {
        this(name, null, color, radius, mass, 0, 0, 0);
    }
    public Planet(String name, Planet parent, Color color, double radius, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.name = name;
        this.color = color;
        this.radius = radius;
    }
}
