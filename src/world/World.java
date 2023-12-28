package world;

import util.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class World {
    public double G = 6.67408 * Math.pow(10, -11);
    public Set<Satellite> satellites;
    public Set<Planet> planets;
    public Satellite simulationCenter;
    public World() {
        this.satellites = new HashSet<>();
        this.planets = new HashSet<>();
    }
    public void insertSatellite(Satellite satellite) {
        if (satellite instanceof Planet planet) {
            planets.add(planet);
        }
        satellites.add(satellite);
    }
    public Set<Satellite> fetchSatellites() {
        return satellites;
    }
    public Set<Planet> fetchPlanets() {
        return planets;
    }
    public void initializeWorld(Satellite simulationCenter) {
        this.simulationCenter = simulationCenter;
        insertSatellite(simulationCenter);

        simulationCenter.xPosition = 0;
        simulationCenter.yPosition = 0;
        simulationCenter.xVelocity = 0;
        simulationCenter.yVelocity = 0;

        for (Satellite satellite : simulationCenter.orderedChildrenList()) {
            insertSatellite(satellite);

            satellite.xPosition = satellite.orbitalRadius * Math.cos(satellite.trueAnomaly) + satellite.parent.xPosition;
            satellite.yPosition = satellite.orbitalRadius * Math.sin(satellite.trueAnomaly) + satellite.parent.yPosition;

            if (satellite.orbitalVelocity == 0) {
                satellite.orbitalVelocity = Math.sqrt((G * satellite.parent.mass) / (1000 * satellite.orbitalRadius)) / 1000;
            }
            satellite.xVelocity = satellite.orbitalVelocity * Math.cos(satellite.trueAnomaly + Math.PI / 2) + satellite.parent.xVelocity;
            satellite.yVelocity = satellite.orbitalVelocity * Math.sin(satellite.trueAnomaly + Math.PI / 2) + satellite.parent.yVelocity;
        }
    }
    public void updatePlanetMovement(double timeStep) {
        for (Satellite satellite : simulationCenter.orderedChildrenList()) {
            if (satellite instanceof Planet planet) {
                Coordinate planetPosition = planet.getPosition();
                Coordinate parentPosition = planet.parent.getPosition();
                double deltaX = planetPosition.getX() - parentPosition.getX();
                double deltaY = planetPosition.getY() - parentPosition.getY();
                double distanceToParent = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
                double forceGravity = ((G * planet.mass * planet.parent.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
                double angle = Math.atan2(deltaY, deltaX);

                double xAcceleration = (forceGravity * -Math.cos(angle)) / planet.mass;
                double yAcceleration = (forceGravity * -Math.sin(angle)) / planet.mass;

                planet.xVelocity += xAcceleration * timeStep;
                planet.yVelocity += yAcceleration * timeStep;

                planet.xPosition += planet.xVelocity * timeStep;
                planet.yPosition += planet.yVelocity * timeStep;
            }
        }
    }

    public void updateSpacecraftMovement(double timeStep, Spacecraft spacecraft) {
        Coordinate spacecraftPosition = spacecraft.getPosition();

        double netXForce = 0;
        double netYForce = 0;

        for (Planet planet : planets) {
            Coordinate planetPosition = planet.getPosition();
            double deltaX = spacecraftPosition.getX() - planetPosition.getX();
            double deltaY = spacecraftPosition.getY() - planetPosition.getY();
            double distanceToParent = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            double forceGravity = ((G * spacecraft.mass * planet.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
            double angle = Math.atan2(deltaY, deltaX);

            netXForce += forceGravity * -Math.cos(angle);
            netYForce += forceGravity * -Math.sin(angle);
        }

//        System.out.println(netXForce + ", " + netYForce);

        spacecraft.xVelocity += (netXForce / spacecraft.mass) * timeStep;
        spacecraft.yVelocity += (netYForce / spacecraft.mass) * timeStep;

        spacecraft.xPosition += spacecraft.xVelocity * timeStep;
        spacecraft.yPosition += spacecraft.yVelocity * timeStep;

//         System.out.println(spacecraft.xPosition + ", " + spacecraft.yPosition + ", " + spacecraft.xVelocity + ", " + spacecraft.yVelocity + ", ");
    }
}
