package world;

import util.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Satellite extends Entity {
    public Satellite parent;
    public Set<Satellite> children;
    public Coordinate velocity;
    public double mass;
    public double orbitalRadius;
    public double orbitalVelocity;
    public double trueAnomaly;

    public Satellite(Satellite parent, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        this.mass = mass;
        this.orbitalRadius = orbitalRadius;
        this.orbitalVelocity = orbitalVelocity;
        this.trueAnomaly = trueAnomaly;

        this.parent = parent;
        this.children = new HashSet<>();
        if (parent != null) {
            parent.addChild(this);
        }
    }
    public void setVelocity(double xVelocity, double yVelocity) {
        this.velocity = new Coordinate(xVelocity, yVelocity);
    }
    public Coordinate getVelocity() {
        return velocity;
    }
    public void addChild(Satellite satellite) {
        children.add(satellite);
    }
    public Set<Satellite> getChildren() {
        return children;
    }
    public List<Satellite> flattenPlanetTree(Satellite root) {
        List<Satellite> resultList = new ArrayList<>();
        flattenPlanetTree(root, resultList);
        return resultList;
    }
    private void flattenPlanetTree(Satellite current, List<Satellite> resultList) {
        resultList.add(current);
        for (Satellite child : current.getChildren()) {
            flattenPlanetTree(child, resultList);
        }
    }
}
