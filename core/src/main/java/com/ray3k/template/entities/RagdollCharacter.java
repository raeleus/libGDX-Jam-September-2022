package com.ray3k.template.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;

import static com.ray3k.template.Core.*;

public class RagdollCharacter extends Entity {
    private final static Vector2 temp1 = new Vector2();
    private final static Vector2 temp2 = new Vector2();
    
    public RagdollCharacter(SkeletonData skeletonData, AnimationStateData animationStateData) {
        setSkeletonData(skeletonData, animationStateData);
    }
    
    @Override
    public void create() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(p2m(x), p2m(y));
    
        body = world.createBody(bodyDef);
        body.setUserData(this);
        
        for (var bone : skeleton.getBones()) {
            PolygonShape box = new PolygonShape();
            temp1.set(p2m(bone.getWorldX()), p2m(bone.getWorldY()));
            box.setAsBox(p2m(10), p2m(10), temp1, bone.getWorldRotationY());

            var fixture = body.createFixture(box, .5f);
            fixture.setFriction(0);
            box.dispose();
        }
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        System.out.println(x + " " + y);
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
}
