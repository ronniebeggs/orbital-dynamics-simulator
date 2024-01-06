package world;

import util.Coordinate;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A `Satellite` is an entity with mass capable of orbital motion around an object.
 * The `Satellite` class extends the `Entity` class, and is the parent of the `Planet` and `Spacecraft` classes.
 * */
public class Satellite extends Entity {
    public Satellite parent; // object which the satellite orbits around
    public Color color; // primary color representing the satellite (color of its lead positions)
    public Set<Satellite> children; // all satellites which orbit around itself
    public Coordinate velocity; // velocity vector
    public double mass; // mass of the satellite
    public double orbitalRadius; // initial orbital radius around its parent satellite
    public double orbitalVelocity; // initial orbital (tangential) velocity relative to its parent satellite
    public double trueAnomaly; // angle representing the initial placement of the satellite within its orbit
    public Deque<Coordinate> leadPositions; // deque containing predicted lead positions
    public Deque<Coordinate> leadVelocities; // deque containing predicted lead velocities

    public Satellite(Satellite parent, Color color, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        this.mass = mass;
        this.orbitalRadius = orbitalRadius;
        this.orbitalVelocity = orbitalVelocity;
        this.trueAnomaly = trueAnomaly;
        this.leadPositions = new ArrayDeque<>();
        this.leadVelocities = new ArrayDeque<>();

        this.parent = parent;
        this.color = color;
        this.children = new HashSet<>();
        // cause parent to track satellite as one of its children
        if (parent != null) {
            parent.addChild(this);
        }
    }
    /**
     * Set the velocity of the satellite by instantiating a new `Coordinate`.
     * @param xVelocity satellite's velocity along the x-axis.
     * @param yVelocity satellite's velocity along the y-axis.
     * */
    public void setVelocity(double xVelocity, double yVelocity) {
        this.velocity = new Coordinate(xVelocity, yVelocity);
    }
    /**
     * @return the satellite's current velocity `Coordinate`.
     * */
    public Coordinate getVelocity() {
        return velocity;
    }
    /**
     * Track a specific `Satellite` as a child of the target.
     * @param satellite child `Satellite`.
     * */
    public void addChild(Satellite satellite) {
        children.add(satellite);
    }
    /**
     * @return an unordered collection of the target satellite's children.
     * */
    public Set<Satellite> getChildren() {
        return children;
    }
    /**
     * Return an in-order traversal over the hierarchy of satellites, with the target satellite as the root.
     * @param root `Satellite` at the root of the satellite hierarchy.
     * @return in-order traversal stored in a list.
     * */
    public List<Satellite> flattenSatelliteTree(Satellite root) {
        List<Satellite> resultList = new ArrayList<>();
        flattenSatelliteTree(root, resultList);
        return resultList;
    }
    private void flattenSatelliteTree(Satellite current, List<Satellite> resultList) {
        resultList.add(current);
        for (Satellite child : current.getChildren()) {
            flattenSatelliteTree(child, resultList);
        }
    }
    /**
     * @return deque containing all lead position `Coordinate`s.
     * */
    public Deque<Coordinate> getLeadPositions() {
        return leadPositions;
    }
    /**
     * @return deque containing all lead velocity `Coordinate`s.
     * */
    public Deque<Coordinate> getLeadVelocities() {
        return leadVelocities;
    }
}
