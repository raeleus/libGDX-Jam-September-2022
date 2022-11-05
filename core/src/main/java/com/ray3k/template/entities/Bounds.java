package com.ray3k.template.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import static com.ray3k.template.Core.*;

public class Bounds extends Entity {
    public float[] points;
    
    public Bounds(float[] points) {
        this.points = points;
    }
    
    @Override
    public void create() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
    
        body = world.createBody(bodyDef);
        body.setUserData(this);
    
        PolygonShape box = new PolygonShape();
        box.set(points);
    
        var fixture = body.createFixture(box, .5f);
        fixture.setFriction(0);
        box.dispose();
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
    public void beginContact(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
}
