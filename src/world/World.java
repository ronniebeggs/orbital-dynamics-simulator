package world;

import util.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class World {
//    public static class PlanetTree {
//        public Planet planet;
//        public Set<PlanetTree> children;
//        public PlanetTree(Planet planet) {
//            this.planet = planet;
//            this.children = new HashSet<>();
//        }
//        public List<Satellite> childrenList() {
//            List<Satellite> resultList = new ArrayList<>();
//            childrenList(this, resultList);
//            return resultList;
//        }
//        public void childrenList(PlanetTree current, List<Satellite> resultList) {
//            resultList.add(current.planet);
//            if (current.children.size() == 0) {
//                return;
//            }
//            for (PlanetTree node : current.children) {
//                childrenList(node, resultList);
//            }
//        }
//        public void addPlanet(Planet planet) {
//            children.contains()
//        }
//    }
    public double G = 6.67408 * Math.pow(10, -11);
    public Set<Planet> planets;
    public Set<Entity> entities;
//    public PlanetTree planetTree;

    public World() {
        this.entities = new HashSet<>();
        this.planets = new HashSet<>();
    }

    public void insertEntity(Entity entity) {
        if (entity instanceof Planet planet) {
            planets.add(planet);
        }
        entities.add(entity);
    }
    public Set<Entity> fetchEntities() {
        return entities;
    }
    public Set<Planet> fetchPlanets() {
        return planets;
    }
    public void updatePlanetMovement(double timeStep) {
        for (Planet planet : planets) {
            if (planet.parent == null) {
                planet.xPosition = 0;
                planet.yPosition = 0;
                continue;
            }

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
}
