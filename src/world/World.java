package world;

import util.Coordinate;

import java.util.*;

public class World {
    public double G = 6.67408 * Math.pow(10, -11);
    public Set<Satellite> satellites;
    public Set<Planet> planets;
    public List<Satellite> orderedSatellites;
    public Satellite simulationCenter;
    public Spacecraft spacecraft;
    public Camera camera;
    public World(Satellite center, Spacecraft spacecraft, Camera camera) {
        this.simulationCenter = center;
        this.spacecraft = spacecraft;
        this.camera = camera;
        this.satellites = new HashSet<>();
        this.planets = new HashSet<>();
        initializeWorld();
    }
    /**
     * Initialize world by placing all satellites withing their orbits, and calculating their corresponding orbital velocities.
     * */
    private void initializeWorld() {
        // simulation center will remain unmoving at the center of the simulation
        simulationCenter.setPosition(0, 0);
        simulationCenter.setVelocity(0, 0);
        insertSatellite(simulationCenter);
        setOrderedSatelliteList(simulationCenter);
        // iterate through each child of the center satellite and initialize their movement quantities.
        for (int childIndex = 1; childIndex < orderedSatellites.size(); childIndex++) {
            Satellite child = orderedSatellites.get(childIndex);
            Coordinate parentPosition = child.parent.getPosition();
            child.setPosition(
                    child.orbitalRadius * Math.cos(child.trueAnomaly) + parentPosition.getX(),
                    child.orbitalRadius * Math.sin(child.trueAnomaly) + parentPosition.getY()
            );
            // if an orbital velocity isn't specified (is equal to 0), a stable circular orbit will be initialized
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
    /**
     * Declare satellite as a trackable and renderable entity within the world.
     * */
    public void insertSatellite(Satellite satellite) {
        if (satellite instanceof Planet planet) {
            planets.add(planet);
        }
        satellites.add(satellite);
    }
    /**
     * Return the satellite at the center of the simulation.
     * All orbital calculations occur relative to this satellite.
     * @return satellite acting as the simulation center.
     * */
    public Satellite getSimulationCenter() {
        return simulationCenter;
    }
    /**
     * Flatten the satellite parent tree to establish an ordered list of satellites.
     * Stored as an attribute of the world.
     * */
    public void setOrderedSatelliteList(Satellite simulationCenter) {
        this.orderedSatellites = simulationCenter.flattenSatelliteTree(simulationCenter);
    }
    /**
     * Fetch the ordered list of satellites after it has been set.
     * @return ordered list of satellites.
     * */
    public List<Satellite> getOrderedChildren() {
        return this.orderedSatellites;
    }
    /**
     * Update all planet movement that occurs during a specified `timeStep` interval.
     * Gravity calculations only occur relative to their parent to avoid chaotic simulations.
     * @param timeStep length of the time interval during which movement occurs (seconds).
     * */
    public void updatePlanetMovement(double timeStep) {
        for (int index = 1; index < orderedSatellites.size(); index++) {
            if (orderedSatellites.get(index) instanceof Planet planet) {
                Coordinate planetPosition = planet.getPosition();
                Coordinate parentPosition = planet.parent.getPosition();

                // calculate the resulting acceleration that occurs in each direction
                double distanceToParent = parentPosition.distanceTo(planetPosition);
                double angle = parentPosition.angleBetween(planetPosition);
                double forceGravity = ((G * planet.mass * planet.parent.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
                double xAcceleration = (forceGravity * -Math.cos(angle)) / planet.mass;
                double yAcceleration = (forceGravity * -Math.sin(angle)) / planet.mass;

                // shift the planet's position and velocity during the given timeStep interval
                Coordinate planetVelocity = planet.getVelocity();
                planetVelocity.shiftX(xAcceleration * timeStep);
                planetVelocity.shiftY(yAcceleration * timeStep);
                planetPosition.shiftX(planetVelocity.getX() * timeStep);
                planetPosition.shiftY(planetVelocity.getY() * timeStep);
            }
        }
    }
    /**
     * Update spacecraft movement that occurs during a specified `timeStep` interval.
     * Gravity is calculated between every satellite withing the simulation.
     * @param timeStep length of the time interval during which movement occurs (seconds).
     * */
    public boolean updateSpacecraftMovement(double timeStep) {
        Coordinate spacecraftPosition = spacecraft.getPosition();

        double netXForce = 0;
        double netYForce = 0;

        Planet strongestInfluence = (Planet) simulationCenter;
        double strongestGravity = 0;
        for (Planet planet : planets) {
            Coordinate planetPosition = planet.getPosition();
            // calculate the net force of gravity caused by each planet in the simulation
            double distanceToParent = planetPosition.distanceTo(spacecraftPosition);
            double angle = planetPosition.angleBetween(spacecraftPosition);
            double forceGravity = ((G * spacecraft.mass * planet.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
            netXForce += forceGravity * -Math.cos(angle);
            netYForce += forceGravity * -Math.sin(angle);

            // determine the strongest source of gravity acting upon the spacecraft
            if (forceGravity > strongestGravity) {
                strongestInfluence = planet;
                strongestGravity = forceGravity;
            }
        }
        // shift the spacecraft's position and velocity during the given timeStep interval
        Coordinate spacecraftVelocity = spacecraft.getVelocity();
        spacecraftVelocity.shiftX((netXForce / spacecraft.mass) * timeStep);
        spacecraftVelocity.shiftY((netYForce / spacecraft.mass) * timeStep);
        spacecraftPosition.shiftX(spacecraftVelocity.getX() * timeStep);
        spacecraftPosition.shiftY(spacecraftVelocity.getY() * timeStep);

        // calculate the spacecraft's velocity relative to its strongest influence (parent)
        double relativeVelocity = spacecraftVelocity.distanceTo(strongestInfluence.getVelocity());
        double distanceToStrongest = spacecraft.getPosition().distanceTo(strongestInfluence.getPosition());
        // determine the escape velocity required to escape the strongest influence's gravitational influence
        double escapeVelocity = Math.sqrt(2 * G * strongestInfluence.mass / (distanceToStrongest * 1000)) / 1000;
        boolean recalculateLead = false;
        // determine whether the spacecraft is escaping its parent's gravitational influence
        if (spacecraft.parent != null && relativeVelocity >= escapeVelocity) {
            spacecraft.parent = null;
            recalculateLead = true;
        // determine whether the planet is entering a stable orbit around a new planet
        } else if (spacecraft.parent == null && relativeVelocity < escapeVelocity) {
            spacecraft.parent = strongestInfluence;
            recalculateLead = true;
        }
        return recalculateLead;
    }
    /**
     * Calculate an entire spacecraft lead. Clears previous leads and starts from scratch.
     * @param leadStep length of the time interval during which lead movement occurs (seconds).
     * @param leadLength max number of lead intervals to be calculated.
     * */
    public void calculateFullLead(double leadStep, int leadLength) {
        // clear all previous lead predictions
        for (Satellite satellite : satellites) {
            satellite.getLeadPositions().clear();
            satellite.getLeadVelocities().clear();
            satellite.getLeadPositions().addLast(satellite.getPosition().copyCoordinate());
            satellite.getLeadVelocities().addLast(satellite.getVelocity().copyCoordinate());
        }
        // calculate lead intervals until max lead length reached, or duplicate positions computed
        boolean outsideRadius = false;
        for (int leadIndex = 0; leadIndex < leadLength; leadIndex++) {
            calculateOneLeadInterval(leadStep);
            // duplicate lead positions may be calculated during a stable orbit around a parent satellite
            if (spacecraft.parent != null) {
                Coordinate lastLead = spacecraft.getLeadPositions().getLast();
                Coordinate parentLastLead = spacecraft.parent.getLeadPositions().getLast();
                // calculate distance between the newest lead positions of the spacecraft and its parent
                double futureDistanceToParent = parentLastLead.distanceTo(lastLead);
                double angleBetween = parentLastLead.angleBetween(lastLead);
                /* calculate the distance between the spacecraft and the newest lead position
                adjusted relative to its parent's current position */
                Coordinate parentPosition = spacecraft.parent.getPosition();
                Coordinate futureRelativeToParent = new Coordinate(
                        parentPosition.getX() + futureDistanceToParent * Math.cos(angleBetween),
                        parentPosition.getY() + futureDistanceToParent * Math.sin(angleBetween)
                );
                // if the distance is within a certain threshold, cease future lead calculations
                double newLeadDistance = spacecraft.getPosition().distanceTo(futureRelativeToParent);
                if (!outsideRadius && newLeadDistance >= 1000) {
                    outsideRadius = true;
                }
                if (outsideRadius && newLeadDistance < 1000) {
                    break;
                }
            }
        }
    }
    /**
     * Calculate one interval of planet and spacecraft lead predictions.
     * @param leadStep length of the time interval during which lead movement occurs (seconds).
     * */
    public void calculateOneLeadInterval(double leadStep) {
        calculatePlanetLeadInterval(leadStep);
        calculateCraftLeadInterval(leadStep);
    }
    /**
     * Remove the first lead positions/velocities contained within each lead deque.
     * Should be triggered roughly when the spacecraft passes by.
     * */
    public void removeLeadInterval() {
        for (Satellite satellite : satellites) {
            satellite.getLeadPositions().removeFirst();
            satellite.getLeadVelocities().removeFirst();
        }
    }
    /**
     * Calculate one interval of planet lead predictions.
     * @param leadStep length of the time interval during which lead movement occurs (seconds).
     * */
    public void calculatePlanetLeadInterval(double leadStep) {
        // the satellite at the simulation center should never move
        simulationCenter.getLeadPositions().add(
                new Coordinate(0, 0)
        );
        simulationCenter.getLeadVelocities().add(
                new Coordinate(0, 0)
        );

        for (int index = 1; index < orderedSatellites.size(); index++) {
            if (orderedSatellites.get(index) instanceof Planet planet) {
                Coordinate planetLeadPosition = planet.getLeadPositions().getLast();
                Coordinate parentLeadPosition = planet.parent.getLeadPositions().getLast();
                // calculate the force of gravity relative to their parent planet
                double distanceToParent = parentLeadPosition.distanceTo(planetLeadPosition);
                double angle = parentLeadPosition.angleBetween(planetLeadPosition);
                double forceGravity = ((G * planet.mass * planet.parent.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
                double xAcceleration = (forceGravity * -Math.cos(angle)) / planet.mass;
                double yAcceleration = (forceGravity * -Math.sin(angle)) / planet.mass;

                // calculate new positions and velocities and add them to the corresponding deques
                Coordinate planetVelocity = planet.getLeadVelocities().getLast();
                Coordinate newVelocity = new Coordinate(
                        planetVelocity.getX() + xAcceleration * leadStep,
                        planetVelocity.getY() + yAcceleration * leadStep
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
            // calculate the net force of gravity caused by each planet in the simulation
            double distanceToParent = planetLeadPosition.distanceTo(spacecraftLeadPosition);
            double angle = planetLeadPosition.angleBetween(spacecraftLeadPosition);
            double forceGravity = ((G * spacecraft.mass * planet.mass) / Math.pow(1000 * distanceToParent, 2)) / 1000;
            netXForce += forceGravity * -Math.cos(angle);
            netYForce += forceGravity * -Math.sin(angle);
        }
        // calculate new positions and velocities and add them to the corresponding deques
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
