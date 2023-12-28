package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.*;

public class PlanetBuilder {
    public String name;
    public PlanetBuilder parent;
    public Color color;
    public double radius;
    public double mass;
    public double orbitalRadius;
    public double trueAnamoly;
    public Set<PlanetBuilder> children;
    public PlanetBuilder(String name, Color color, double radius, double mass) {
        this(name, null, color, radius, mass, 0, 0);
    }
    public PlanetBuilder(String name, PlanetBuilder parent, Color color, double radius, double mass, double orbitalRadius, double trueAnamoly) {
        this.name = name;
        this.parent = parent;
        this.color = color;
        this.radius = radius;
        this.mass = mass;
        this.orbitalRadius = orbitalRadius;
        this.trueAnamoly = trueAnamoly;
        this.children = new HashSet<>();
        if (parent != null) {
            parent.addChild(this);
        }
    }
    public void addChild(PlanetBuilder child) {
        children.add(child);
    }
    public Set<PlanetBuilder> getChildren() {
        return children;
    }
    public List<PlanetBuilder> orderedChildrenList() {
        List<PlanetBuilder> resultList = new ArrayList<>();
        orderedChildrenList(this, resultList);
        return resultList;
    }
    private void orderedChildrenList(PlanetBuilder current, List<PlanetBuilder> resultList) {
        if (current.getChildren().size() == 0) {
            return;
        }
        resultList.add(current);
        for (PlanetBuilder child : current.getChildren()) {
            orderedChildrenList(child, resultList);
        }
    }
}
