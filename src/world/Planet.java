package world;

import java.awt.Color;

public class Planet extends Satellite {
    public String name; // planet name label
    public double radius; // radius of the planet (km)
    public Planet(String name, Color color, double radius, double mass) {
        this(name, null, color, radius, mass, 0, 0, 0);
    }
    public Planet(String name, Planet parent, Color color, double radius, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, color, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.name = name;
        this.radius = radius;
    }
}
