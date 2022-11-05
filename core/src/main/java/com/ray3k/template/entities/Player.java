package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Bone;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;
import static com.ray3k.template.entities.Player.Mode.*;

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
    enum Mode {
        STANDING, WALKING, RUNNING, SKIDDING, JUMPING, DOUBLE_JUMPING, FALLING, SHOOTING
    }
    private Mode mode;
    private Fixture footSensor;
    private Fixture headSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture collisionBox;
    private Bone smokeTarget;
    private Bone fartTarget;
    private Array<Entity> footSensorBlocks = new Array<>();
    private Array<Entity> rightSensorBlocks = new Array<>();
    private Array<Entity> leftSensorBlocks = new Array<>();
    private Array<Entity> headSensorBlocks = new Array<>();
    private Array<Entity> footContactBlocks = new Array<>();
    private Array<Entity> rightContactBlocks = new Array<>();
    private Array<Entity> leftContactBlocks = new Array<>();
    private Array<Entity> headContactBlocks = new Array<>();
    private Array<Entity> collisionBoxContactBlocks = new Array<>();
    
    @Override
    public void create() {
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        collisionBox = setCollisionBox(slotBbox, BodyType.DynamicBody);
        footSensor = setSensorBox(slotFootSensor, BodyType.DynamicBody);
        headSensor = setSensorBox(slotHeadSensor, BodyType.DynamicBody);
        leftSensor = setSensorBox(slotLeftSensor, BodyType.DynamicBody);
        rightSensor = setSensorBox(slotRightSensor, BodyType.DynamicBody);
    
        smokeTarget = findBone(boneSmokeTarget);
        fartTarget = findBone(boneFartTarget);
        
        animationState.setAnimation(0, animationBlink, true);
        animationState.setAnimation(1, animationTail, true);
        animationState.setAnimation(2, animationJumpFall, true);
        mode = FALLING;
        
        gravityY = -playerGravity;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        camera.position.set(getBboxCenterX(), getBboxCenterY(), 0);
        
        rightContactBlocks.clear();
        leftContactBlocks.clear();
        headContactBlocks.clear();
        footContactBlocks.clear();
        
        for (var entity : collisionBoxContactBlocks) {
            if (rightSensorBlocks.contains(entity, true)) rightContactBlocks.add(entity);
            if (leftSensorBlocks.contains(entity, true)) leftContactBlocks.add(entity);
            if (footSensorBlocks.contains(entity, true)) footContactBlocks.add(entity);
            if (headSensorBlocks.contains(entity, true)) headContactBlocks.add(entity);
        }
        
        switch(mode) {
            case FALLING:
                //hit ground
                if (footContactBlocks.size > 0) {
                    deltaY = 0;
                    gravityY = 0;
                    mode = STANDING;
                }
                break;
            case STANDING:
                if (isBindingPressed(Binding.RIGHT)) {
                    deltaX = Utils.approach(deltaX, playerMaxWalkSpeed, playerWalkAcceleration * delta);
                } else if (isBindingPressed(Binding.LEFT)) {
                    deltaX = Utils.approach(deltaX, -playerMaxWalkSpeed, playerWalkAcceleration * delta);
                } else {
                    deltaX = Utils.approach(deltaX, 0, playerWalkDeceleration * delta);
                }
    
                if (footContactBlocks.size == 0 && footSensorBlocks.size > 0) {
                    gravityY = -playerGravity;
                } else if (footContactBlocks.size > 0) {
                    deltaY = 0;
                    gravityY = 0;
                } else if (footSensorBlocks.size == 0) {
                    gravityY = -playerGravity;
                    mode = FALLING;
                }
                break;
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Contact contact) {
        if (other instanceof Bounds) {
            if (fixture == footSensor) {
                footSensorBlocks.add(other);
            } else if (fixture == rightSensor) {
                rightSensorBlocks.add(other);
            } else if (fixture == leftSensor) {
                leftSensorBlocks.add(other);
            } else if (fixture == headSensor) {
                headSensorBlocks.add(other);
            } else if (fixture == collisionBox) {
                collisionBoxContactBlocks.add(other);
            }
        }
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Contact contact) {

    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Contact contact) {
        if (other instanceof Bounds) {
            if (fixture == footSensor) {
                footSensorBlocks.removeValue(other, true);
            } else if (fixture == rightSensor) {
                rightSensorBlocks.removeValue(other, true);
            } else if (fixture == leftSensor) {
                leftSensorBlocks.removeValue(other, true);
            } else if (fixture == headSensor) {
                headSensorBlocks.removeValue(other, true);
            } else if (fixture == collisionBox) {
                collisionBoxContactBlocks.removeValue(other, true);
            }
        }
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
}
