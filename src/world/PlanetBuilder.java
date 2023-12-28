package world;

import java.awt.*;

public class PlanetBuilder {
    public String name;
    public PlanetBuilder parent;
    public Color color;
    public double radius;
    public double mass;
    public double orbitalRadius;
    public double trueAnamoly;
    public PlanetBuilder(String name, PlanetBuilder parent, Color color, double radius, double mass, double orbitalRadius, double trueAnamoly) {
        this.name = name;
        this.parent = parent;
        this.color = color;
        this.radius = radius;
        this.mass = mass;
        this.orbitalRadius = orbitalRadius;
        this.trueAnamoly = trueAnamoly;
    }
}
