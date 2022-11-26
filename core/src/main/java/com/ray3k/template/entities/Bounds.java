package com.ray3k.template.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;

public class Bounds extends Entity {
    public float[] points;
    public int edgeCount;
    
    public Bounds(float[] points) {
        this.points = points;
    }
    
    public static Vector2 temp1 = new Vector2();
    public static Vector2 temp2 = new Vector2();
    
    @Override
    public void create() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
    
        body = world.createBody(bodyDef);
        body.setUserData(this);
        
        boolean clockwise = Utils.isClockwise(points);
        Fixture previousFixture = null;
        Fixture firstFixture = null;
        for (int i = 0; i + 1 < points.length; i += 2) {
            EdgeShape edgeShape = new EdgeShape();
    
            float nextX, nextY;
            if (i + 3 < points.length) {
                nextX = points[i + 2];
                nextY = points[i + 3];
            } else {
                nextX = points[0];
                nextY = points[1];
            }
            edgeShape.set(points[i], points[i + 1], nextX, nextY);
    
            if (i - 2 >= 0) {
                edgeShape.setVertex0(points[i - 2], points[i - 1]);
            } else {
                edgeShape.setVertex0(points[points.length - 2], points[points.length - 1]);
            }
            
            if (i + 5 < points.length)
                edgeShape.setVertex3(points[i + 4], points[i + 5]);
            else
                edgeShape.setVertex3(points[i + 4 - points.length], points[i + 5 - points.length]);
    
            var fixture = body.createFixture(edgeShape, .5f);
            edgeCount ++;
            fixture.setFriction(0);
            fixture.getFilterData().categoryBits = CATEGORY_BOUNDS;
    
            var data = new BoundsData();
            data.fixture = fixture;
            temp1.set(nextX, nextY);
            temp2.set(points[i], points[i + 1]);
            if (!clockwise) {
                temp2.sub(temp1);
                data.angle = (temp2.angleDeg() + 90) % 360;
            } else {
                temp1.sub(temp2);
                data.angle = (temp1.angleDeg() + 90) % 360;
            }
            data.previousFixture = previousFixture;
            if (previousFixture != null) ((BoundsData) previousFixture.getUserData()).nextFixture = fixture;
            if (i == points.length - 2) {
                data.nextFixture = firstFixture;
                ((BoundsData)firstFixture.getUserData()).previousFixture = fixture;
            }
            
            fixture.setUserData(data);
            previousFixture = fixture;
            if (i == 0) firstFixture = fixture;
            edgeShape.dispose();
        }
        
        if (!clockwise) {
            for (var fixture : body.getFixtureList()) {
                var boundsData = (BoundsData) fixture.getUserData();
                var temp = boundsData.previousFixture;
                boundsData.previousFixture = boundsData.nextFixture;
                boundsData.nextFixture = temp;
            }
        }
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
    
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
    
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
    
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
    
    }
    
    public static class BoundsData {
        public float angle;
        public Fixture previousFixture;
        public Fixture nextFixture;
        public Fixture fixture;
        
        public int distanceToFixture(Fixture otherFixture) {
            if (fixture == otherFixture) return 0;
            var bounds = (Bounds) fixture.getBody().getUserData();
            if (otherFixture.getBody().getUserData() != bounds) return Integer.MAX_VALUE;
            var distance = 1;
            var fix = nextFixture;
            while (fix != otherFixture) {
                distance++;
                fix = ((BoundsData)fix.getUserData()).nextFixture;
            }
    
            return Math.min(bounds.edgeCount - distance, distance);
        }
        
        public boolean checkFixturesBetween(Fixture otherFixture, CompareBoundsFixtures compare) {
            var bounds = (Bounds) fixture.getBody().getUserData();
            if (otherFixture.getBody().getUserData() != bounds) return false;
            
            if (fixture == otherFixture || nextFixture == otherFixture || previousFixture == otherFixture) return true;
            
            var accepted = true;
            var distanceNext = 1;
            var nextFix = nextFixture;
            while (nextFix != otherFixture) {
                if (!compare.accept(nextFix)) {
                    accepted = false;
                    break;
                }
                nextFix = ((BoundsData)nextFix.getUserData()).nextFixture;
                distanceNext++;
            }
            
            if (!accepted) {
                accepted = true;
                var distancePrevious = 1;
                var prevFix = previousFixture;
                while (prevFix != otherFixture) {
                    if (!compare.accept(prevFix) || distancePrevious > distanceNext) {
                        accepted = false;
                        break;
                    }
                    prevFix = ((BoundsData) prevFix.getUserData()).previousFixture;
                    distancePrevious++;
                }
            }
            return accepted;
        }
    }
    
    public static interface CompareBoundsFixtures {
        public boolean accept(Fixture fixture);
    }
}
