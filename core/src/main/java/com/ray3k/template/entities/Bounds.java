package com.ray3k.template.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;

public class Bounds extends Entity {
    public float[] points;
    
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
            fixture.setFriction(0);
    
            var data = new BoundsData();
            temp1.set(nextX, nextY);
            temp2.set(points[i], points[i + 1]);
            if (clockwise) {
                temp2.sub(temp1);
                data.angle = temp2.angleDeg();
            } else {
                temp1.sub(temp2);
                data.angle = temp1.angleDeg();
            }
            
            fixture.setUserData(data);
            edgeShape.dispose();
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
    }
}
