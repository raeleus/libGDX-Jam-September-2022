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
    private float runTimer;
    private float smokeTimer;
    private Bone smokeTarget;
    private boolean inAir;
    private float jumpTimer;
    private float doubleJumpTimer;
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
        inAir = true;
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
        
        if (rightContactBlocks.size > 0 && deltaX > 0) deltaX = 0;
        if (leftContactBlocks.size > 0 && deltaX < 0) deltaX = 0;
        if (footContactBlocks.size > 0) {
            gravityY = -playerGravity;
            if (deltaY < 0) deltaY = 0;
            inAir = false;
            if (mode == FALLING || mode == DOUBLE_JUMPING) {
                mode = STANDING;
                animationState.setAnimation(2, animationJumpLand, false);
                animationState.addAnimation(2, animationStand, false, 0);
            }
        }
        
        if (headContactBlocks.size > 0 && deltaY > 0) deltaY = 0;
        
        if (footSensorBlocks.size == 0) {
            inAir = true;
            gravityY = -playerGravity;
        }
    
        
        if (inAir) {
            switch (mode) {
                case JUMPING:
                    jumpTimer -= delta;
                
                    if (isBindingPressed(Binding.RIGHT)) {
                        deltaX = Utils.approach(deltaX, playerJumpXspeed, playerJumpXacceleration * delta);
                    } else if (isBindingPressed(Binding.LEFT)) {
                        deltaX = Utils.approach(deltaX, -playerJumpXspeed, playerJumpXacceleration * delta);
                    } else {
                        deltaX = Utils.approach(deltaX, 0, playerJumpXacceleration * delta);
                    }
                
                    if (jumpTimer > 0 && isBindingPressed(Binding.JUMP)) {
                        deltaY += playerJumpYacceleration * delta;
                    } else {
                        jumpTimer = 0;
                    }
                
                    if (deltaY <= 0) {
                        mode = FALLING;
                        animationState.setAnimation(2, animationJumpFall, true);
                        doubleJumpTimer = 0;
                    }
                    break;
                case DOUBLE_JUMPING:
                    jumpTimer -= delta;
                
                    if (isBindingPressed(Binding.RIGHT)) {
                        deltaX = Utils.approach(deltaX, playerJumpXspeed, playerJumpXacceleration * delta);
                    } else if (isBindingPressed(Binding.LEFT)) {
                        deltaX = Utils.approach(deltaX, -playerJumpXspeed, playerJumpXacceleration * delta);
                    } else {
                        deltaX = Utils.approach(deltaX, 0, playerJumpXacceleration * delta);
                    }
                
                    if (jumpTimer > 0 && isBindingPressed(Binding.JUMP)) {
                        deltaY += playerDoubleJumpYacceleration * delta;
                    } else {
                        jumpTimer = 0;
                        mode = FALLING;
                        animationState.setAnimation(2, animationJumpFall, true);
                    }
                
                    if (deltaY > playerMaxFallYspeed) deltaY = playerMaxFallYspeed;
                    break;
                case FALLING:
                    doubleJumpTimer -= delta;
                    if (getAnimation(2) != animationJumpFall) animationState.setAnimation(2, animationJumpFall, true);
                
                    if (isBindingPressed(Binding.RIGHT)) {
                        deltaX = Utils.approach(deltaX, playerMaxFallXspeed, playerFallXacceleration * delta);
                    } else if (isBindingPressed(Binding.LEFT)) {
                        deltaX = Utils.approach(deltaX, -playerMaxFallXspeed, playerFallXacceleration * delta);
                    } else {
                        deltaX = Utils.approach(deltaX, 0, playerFallXacceleration * delta);
                    }
                
                    if (doubleJumpTimer <= 0 && isBindingPressed(Binding.JUMP)) {
                        doubleJumpTimer = playerDoubleJumpDelay;
                        jumpTimer = playerDoubleJumpTime;
                        if (deltaY < 0) deltaY = 0;
                    
                        mode = DOUBLE_JUMPING;
                        animationState.setAnimation(2, animationJumpDouble, true);
                    
                        var fart = new Fart();
                        entityController.add(fart);
                        fart.teleport(fartTarget.getWorldX(), fartTarget.getWorldY());
                    }
                
                    if (deltaY > playerMaxFallYspeed) deltaY = playerMaxFallYspeed;
                    break;
                default:
                    mode = FALLING;
                    animationState.setAnimation(2, animationJumpFall, true);
                    break;
            }
        } else {
            switch (mode) {
                case STANDING:
                    deltaX = Utils.approach(deltaX, 0, playerWalkDeceleration * delta);
                    if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                        animationState.setAnimation(2, animationWalk, true);
                        mode = WALKING;
                        runTimer = 0;
    
                        if (isBindingPressed(Binding.RIGHT)) {
                            skeleton.getRootBone().setScaleX(1);
                        } else if (isBindingPressed(Binding.LEFT)) {
                            skeleton.getRootBone().setScaleX(-1);
                        }
                    }
    
                    if (Math.abs(deltaX) > playerStandSmokeMinSpeed) {
                        smokeTimer -= delta;
                        if (smokeTimer < 0) {
                            smokeTimer = playerSmokeDelay;
                            var sticker = new SkidSmoke();
                            entityController.add(sticker);
                            sticker.setPosition(smokeTarget.getWorldX(), smokeTarget.getWorldY());
                        }
                    }
    
                    if (isBindingJustPressed(Binding.JUMP)) {//do jump
                        mode = JUMPING;
                        animationState.setAnimation(2, animationJump, false);
                        animationState.addAnimation(2, animationJumpAir, true, 0);
                        deltaY = playerJumpSpeed;
                        jumpTimer = playerJumpExtraTime;
                        gravityY = -playerGravity;
                        inAir = true;
                    }
                    break;
                case WALKING:
                    if (!isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                        animationState.setAnimation(2, animationStand, true);
                        mode = STANDING;
                    } else {
                        if (isBindingPressed(Binding.RIGHT)) {//right
                            deltaX = Utils.approach(deltaX, playerMaxWalkSpeed, playerWalkAcceleration * delta);
    
                            if (deltaX < -playerSkidThreshold) {
                                mode = SKIDDING;
                                animationState.setAnimation(2, animationSkid, true);
                                animationState.setAnimation(1, animationTail, true);
                            } else if (deltaX < -playerSkidThreshold) {
                                skeleton.getRootBone().setScaleX(1);
                                runTimer = 0;
                            }
                        } else {//left
                            deltaX = Utils.approach(deltaX, -playerMaxWalkSpeed, playerWalkAcceleration * delta);
    
                            if (deltaX > playerSkidThreshold) {
                                mode = SKIDDING;
                                animationState.setAnimation(2, animationSkid, true);
                                animationState.setAnimation(1, animationTail, true);
                            } else if (deltaX > 0) {
                                skeleton.getRootBone().setScaleX(-1);
                                runTimer = 0;
                            }
                        }
    
                        runTimer += delta;
                        if (runTimer > playerTimeToRun) {
                            mode = RUNNING;
                            animationState.setAnimation(2, animationRun, true);
                            animationState.setAnimation(1, animationTailRun, true);
    
                            if (isBindingPressed(Binding.RIGHT)) {
                                skeleton.getRootBone().setScaleX(1);
                            } else if (isBindingPressed(Binding.LEFT)) {
                                skeleton.getRootBone().setScaleX(-1);
                            }
                        }
                    }
    
                    if (isBindingJustPressed(Binding.JUMP)) {//do jump
                        mode = JUMPING;
                        animationState.setAnimation(2, animationJump, false);
                        animationState.addAnimation(2, animationJumpAir, true, 0);
                        deltaY = playerJumpSpeed;
                        jumpTimer = playerJumpExtraTime;
                        gravityY = -playerGravity;
                        inAir = true;
                    }
                    break;
                case RUNNING:
                    if (!isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                        animationState.setAnimation(2, animationStand, true);
                        animationState.setAnimation(1, animationTail, true);
                        mode = STANDING;
                    } else {
                        if (isBindingPressed(Binding.RIGHT)) {//right
                            deltaX = Utils.approach(deltaX, playerMaxRunSpeed, playerRunAcceleration * delta);
    
                            if (deltaX < -playerSkidThreshold) {
                                mode = SKIDDING;
                                animationState.setAnimation(2, animationSkid, true);
                                animationState.setAnimation(1, animationTail, true);
                            }
                        } else {//left
                            deltaX = Utils.approach(deltaX, -playerMaxRunSpeed, playerRunAcceleration * delta);
    
                            if (deltaX > playerSkidThreshold) {
                                mode = SKIDDING;
                                animationState.setAnimation(2, animationSkid, true);
                                animationState.setAnimation(1, animationTail, true);
                            }
                        }
                    }
    
                    if (isBindingJustPressed(Binding.JUMP)) {//do jump
                        mode = JUMPING;
                        animationState.setAnimation(2, animationJump, false);
                        animationState.addAnimation(2, animationJumpAir, true, 0);
                        deltaY = playerJumpSpeed;
                        jumpTimer = playerJumpExtraTime;
                        gravityY = -playerGravity;
                        inAir = true;
                    }
                    break;
                case SKIDDING:
                    deltaX = Utils.approach(deltaX, 0, playerStandDeceleration * delta);
                    if (MathUtils.isZero(deltaX)) {
                        animationState.setAnimation(2, animationStand, true);
                        mode = STANDING;
                    }
                    if (deltaX > 0) {
                        skeleton.getRootBone().setScaleX(1);
                    } else {
                        skeleton.getRootBone().setScaleX(-1);
                    }
    
                    smokeTimer -= delta;
                    if (smokeTimer < 0) {
                        smokeTimer = playerSmokeDelay;
                        var sticker = new SkidSmoke();
                        entityController.add(sticker);
                        sticker.setPosition(smokeTarget.getWorldX(), smokeTarget.getWorldY());
                    }
                    break;
            }
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
