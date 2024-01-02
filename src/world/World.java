package world;

import util.Coordinate;
import util.LeadNode;

import java.util.*;

public class World {
    public double G = 6.67408 * Math.pow(10, -11);
    public Set<Satellite> satellites;
    public Set<Planet> planets;
    public Satellite simulationCenter;
    public List<Satellite> orderedChildren;
    public Spacecraft spacecraft;
    public World(Satellite center, Spacecraft spacecraft) {
        this.simulationCenter = center;
        this.spacecraft = spacecraft;
        this.satellites = new HashSet<>();
        this.planets = new HashSet<>();
        initializeWorld();
    }
    public void insertSatellite(Satellite satellite) {
        if (satellite instanceof Planet planet) {
            planets.add(planet);
        }
        satellites.add(satellite);
    }
    public Satellite getSimulationCenter() {
        return simulationCenter;
    }
    public Set<Satellite> getSatellites() {
        return satellites;
    }
    public Set<Planet> getPlanets() {
        return planets;
    }
    private void initializeWorld() {
        simulationCenter.setPosition(0, 0);
        simulationCenter.setVelocity(0, 0);
        insertSatellite(simulationCenter);
        setChildrenList(simulationCenter);
        for (int childIndex = 1; childIndex < orderedChildren.size(); childIndex++) {
            Satellite child = orderedChildren.get(childIndex);

            Coordinate parentPosition = child.parent.getPosition();
            child.setPosition(
                    child.orbitalRadius * Math.cos(child.trueAnomaly) + parentPosition.getX(),
                    child.orbitalRadius * Math.sin(child.trueAnomaly) + parentPosition.getY()
            );

            if (child.orbitalVelocity == 0) {
                child.orbitalVelocity = Math.sqrt((G * child.parent.mass) / (1000 * child.orbitalRadius)) / 1000;
            }

            Coordinate parentVelocity = child.parent.getVelocity();
            child.setVelocity(
                    child.orbitalVelocity * Math.cos(child.trueAnomaly + Math.PI / 2) + parentVelocity.getX(),
                    child.orbitalVelocity * Math.sin(child.trueAnomaly + Math.PI / 2) + parentVelocity.getY()
            );

            insertSatellite(child);
        }
    }
    public void updatePlanetMovement(double timeStep) {

        for (int index = 1; index < orderedChildren.size(); index++) {
            Satellite satellite = orderedChildren.get(index);

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

                Coordinate planetVelocity = planet.getVelocity();
                planetVelocity.shiftX(xAcceleration * timeStep);
                planetVelocity.shiftY(yAcceleration * timeStep);

                planetPosition.shiftX(planetVelocity.getX() * timeStep);
                planetPosition.shiftY(planetVelocity.getY() * timeStep);
            }
        }
    }

    public void updateSpacecraftMovement(double timeStep) {
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

        Coordinate spacecraftVelocity = spacecraft.getVelocity();
        spacecraftVelocity.shiftX((netXForce / spacecraft.mass) * timeStep);
        spacecraftVelocity.shiftY((netYForce / spacecraft.mass) * timeStep);

        spacecraftPosition.shiftX(spacecraftVelocity.getX() * timeStep);
        spacecraftPosition.shiftY(spacecraftVelocity.getY() * timeStep);
    }

    public List<Satellite> getOrderedChildren() {
        return this.orderedChildren;
    }
    public void setChildrenList(Satellite simulationCenter) {
        this.orderedChildren = simulationCenter.flattenPlanetTree(simulationCenter);
    }
    public void calculateFullLead(double leadStep, int leadLength) {
        for (Satellite satellite : satellites) {
            satellite.getLeadPositions().clear();
            satellite.getLeadVelocities().clear();
            satellite.getLeadPositions().addLast(satellite.getPosition());
            satellite.getLeadVelocities().addLast(satellite.getVelocity());
        }

        for (int leadIndex = 0; leadIndex < leadLength; leadIndex++) {
            calculateOneLeadInterval(leadStep);
        }
    }
    public void calculateOneLeadInterval(double leadStep) {
        calculatePlanetLeadInterval(leadStep);
        calculateCraftLeadInterval(leadStep);
    }
    public void removeLeadInterval() {
        for (Satellite satellite : satellites) {
            satellite.getLeadPositions().removeFirst();
            satellite.getLeadVelocities().removeFirst();
        }
    }
    public void calculatePlanetLeadInterval(double leadStep) {
        simulationCenter.getLeadPositions().add(
                new Coordinate(0, 0)
        );
        simulationCenter.getLeadVelocities().add(
                new Coordinate(0, 0)
        );

        for (int index = 1; index < orderedChildren.size(); index++) {
            if (orderedChildren.get(index) instanceof Planet planet) {

                Coordinate planetLeadPosition = planet.getLeadPositions().getLast();
                Coordinate parentLeadPosition = planet.parent.getLeadPositions().getLast();

                double deltaX = planetLeadPosition.getX() - parentLeadPosition.getX();
                double deltaY = planetLeadPosition.getY() - parentLeadPosition.getY();
                double distanceToParent = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
                double forceGravity = ((G * planet.mass * planet.parent.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
                double angle = Math.atan2(deltaY, deltaX);

                double xAcceleration = (forceGravity * -Math.cos(angle)) / planet.mass;
                double yAcceleration = (forceGravity * -Math.sin(angle)) / planet.mass;

                Coordinate planetVelocity = planet.getLeadVelocities().getLast();
                Coordinate newVelocity = new Coordinate(
                        planetVelocity.getX() + xAcceleration * leadStep,
                        planetVelocity.getY() + xAcceleration * leadStep
                );

                Coordinate newPosition = new Coordinate(
                        planetLeadPosition.getX() + newVelocity.getX() * leadStep,
                        planetLeadPosition.getY() + newVelocity.getY() * leadStep
                );

                planet.getLeadVelocities().addLast(newVelocity);
                planet.getLeadPositions().addLast(newPosition);
            }
        }
    }
    public void calculateCraftLeadInterval(double leadStep) {
        Coordinate spacecraftLeadPosition = spacecraft.getLeadPositions().getLast();

        double netXForce = 0;
        double netYForce = 0;

        for (Planet planet : planets) {
            Coordinate planetLeadPosition = planet.getLeadPositions().getLast();
            double deltaX = spacecraftLeadPosition.getX() - planetLeadPosition.getX();
            double deltaY = spacecraftLeadPosition.getY() - planetLeadPosition.getY();
            double distanceToParent = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            double forceGravity = ((G * spacecraft.mass * planet.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
            double angle = Math.atan2(deltaY, deltaX);

            netXForce += forceGravity * -Math.cos(angle);
            netYForce += forceGravity * -Math.sin(angle);
        }

        Coordinate spacecraftLeadVelocity = spacecraft.getLeadVelocities().getLast();
        Coordinate newVelocity = new Coordinate(
                spacecraftLeadVelocity.getX() + (netXForce / spacecraft.mass) * leadStep,
                spacecraftLeadVelocity.getY() + (netYForce / spacecraft.mass) * leadStep
        );

        Coordinate newPosition = new Coordinate(
                spacecraftLeadPosition.getX() + newVelocity.getX() * leadStep,
                spacecraftLeadPosition.getY() + newVelocity.getY() * leadStep
        );

        spacecraft.getLeadVelocities().addLast(newVelocity);
        spacecraft.getLeadPositions().addLast(newPosition);
    }
}
