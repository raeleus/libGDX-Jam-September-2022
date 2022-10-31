package com.ray3k.template.entities;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;

/**
 * track 0 blink
 * track 1 tail
 * track 2 animations
 * track 3 lick
 * track 4 lick blocked
 * track 5 hurt
 * track 6 no baby
 */
public class Player extends Entity {
    private Fixture footSensor;
    
    @Override
    public void create() {
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        setCollisionBox(slotBbox, skeletonBounds, BodyType.DynamicBody);
        footSensor = setSensorBox(slotFootSensor, skeletonBounds, BodyType.DynamicBody);
        
        animationState.setAnimation(0, animationBlink, true);
        animationState.setAnimation(1, animationTail, true);
        animationState.setAnimation(2, animationJumpFall, true);
        
        gravityY = -playerGravity;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        camera.position.set(x, y, 0);
        
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Contact contact) {
        if (other instanceof Block && fixture == footSensor) {
            System.out.println("hit block" + fixture);
        }
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Contact contact) {
        if (other instanceof Block && fixture == footSensor) {
            System.out.println("going to hit block");
        }
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
}
