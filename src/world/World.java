package world;

import util.Coordinate;
import java.util.*;

public class World {
    public double G = 6.67408 * Math.pow(10, -11);
    public Set<Satellite> satellites;
    public Set<Planet> planets;
    public Satellite simulationCenter;
    public List<Satellite> orderedChildren;
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
    public void initializeWorld(Satellite center) {
        this.simulationCenter = center;
        insertSatellite(simulationCenter);

        setOrderedChildren();
        orderedChildren = getOrderedChildren();
        for (Satellite child : orderedChildren) {

            child.xPosition = child.orbitalRadius * Math.cos(child.trueAnomaly) + child.parent.xPosition;
            child.yPosition = child.orbitalRadius * Math.sin(child.trueAnomaly) + child.parent.yPosition;

            if (child.orbitalVelocity == 0) {
                child.orbitalVelocity = Math.sqrt((G * child.parent.mass) / (1000 * child.orbitalRadius)) / 1000;
            }
            child.xVelocity = child.orbitalVelocity * Math.cos(child.trueAnomaly + Math.PI / 2) + child.parent.xVelocity;
            child.yVelocity = child.orbitalVelocity * Math.sin(child.trueAnomaly + Math.PI / 2) + child.parent.yVelocity;

            insertSatellite(child);
        }
    }
    public void updatePlanetMovement(double timeStep) {
        for (Satellite satellite : getOrderedChildren()) {
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

        spacecraft.xVelocity += (netXForce / spacecraft.mass) * timeStep;
        spacecraft.yVelocity += (netYForce / spacecraft.mass) * timeStep;

        spacecraft.xPosition += spacecraft.xVelocity * timeStep;
        spacecraft.yPosition += spacecraft.yVelocity * timeStep;
    }

    public List<Satellite> getOrderedChildren() {
        return this.orderedChildren;
    }
    private void setOrderedChildren() {
        List<Satellite> resultList = new ArrayList<>();
        setOrderedChildren(simulationCenter, resultList);
        this.orderedChildren = resultList;
    }
    private void setOrderedChildren(Satellite current, List<Satellite> resultList) {
        for (Satellite child : current.getChildren()) {
            resultList.add(child);
            setOrderedChildren(child, resultList);
        }
    }
}
