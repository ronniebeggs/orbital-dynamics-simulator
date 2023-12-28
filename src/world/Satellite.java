package world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Satellite extends Entity {
    public Satellite parent;
    public Set<Satellite> children;
    public String name;
    public double mass;
    public double xVelocity;
    public double yVelocity;
    public double orbitalRadius;
    public double orbitalVelocity;
    public double trueAnomaly;

    public Satellite(Satellite parent, double mass, double x, double y, double xVelocity, double yVelocity, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(x, y);
        this.mass = mass;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.orbitalRadius = orbitalRadius;
        this.orbitalVelocity = orbitalVelocity;
        this.trueAnomaly = trueAnomaly;

        this.parent = parent;
        this.children = new HashSet<>();
        if (parent != null) {
            parent.addChild(this);
        }
    }
    public void addChild(Satellite satellite) {
        children.add(satellite);
    }
    public Set<Satellite> getChildren() {
        return children;
    }
//    public List<Satellite> orderedChildrenList() {
//        List<Satellite> resultList = new ArrayList<>();
//        orderedChildrenList(this, resultList);
//        return resultList;
//    }
//    private void orderedChildrenList(Satellite current, List<Satellite> resultList) {
//        if (current.getChildren().size() == 0) {
//            return;
//        }
//        for (Satellite child : current.getChildren()) {
//            resultList.add(child);
//            orderedChildrenList(child, resultList);
//        }
//    }
}
