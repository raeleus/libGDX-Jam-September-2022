package com.ray3k.template.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

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
        
        var chainShape = new ChainShape();
        chainShape.createLoop(points);
    
        var fixture = body.createFixture(chainShape, .5f);
        fixture.setFriction(0);
        chainShape.dispose();
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
