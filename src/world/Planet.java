package world;
import util.PlanetBuilder;

import java.awt.Color;

public class Planet extends Satellite {
    public double radius;
    public Color color;

//    public Planet(PlanetBuilder builder, Planet parent, double xPosition, double yPosition, double xVelocity, double yVelocity) {
//        this(builder.name, parent, builder.color, builder.radius, builder.mass, xPosition, yPosition, xVelocity, yVelocity);
//    }
    public Planet(String name, Planet parent, Color color, double radius, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, mass, 0, 0, 0, 0, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.radius = radius;
        this.color = color;
    }
}
