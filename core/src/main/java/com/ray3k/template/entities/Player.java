package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.*;
import com.dongbat.jbump.Response.Result;
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
    private float runTimer;
    private float smokeTimer;
    private Bone smokeTarget;
    private boolean inAir;
    private float jumpTimer;
    private float doubleJumpTimer;
    private Bone fartTarget;
    
    @Override
    public void create() {
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        setCollisionBox(slotBbox, skeletonBounds, zebraCollisionFilter);
//        roundCollisionBox();
        
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
        
        var result = world.check(item, getBboxLeft(), getBboxBottom() - 1, defaultCollisionFilter);
        inAir = result.projectedCollisions.size() == 0;
        
        if (inAir) {
            gravityY = -playerGravity;
            
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
        } else  {
            gravityY = 0;
            switch (mode) {
                case STANDING:
                    deltaX = Utils.approach(deltaX, 0, playerWalkDeceleration * delta);
                    if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                        animationState.setAnimation(2, animationWalk, true);
                        mode = WALKING;
                        runTimer = 0;
                
                        if (isBindingPressed(Binding.RIGHT)) {
                            skeleton.getRootBone().setScaleX(1);
                        } else if (isBindingPressed(Binding.LEFT)){
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
                            } else if (deltaX > 0){
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
                            } else if (isBindingPressed(Binding.LEFT)){
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
        var rect = world.getRect(item);
        shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h, Color.PINK, 2f);
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void projectedCollision(Result result) {
        handleCollisions(result.projectedCollisions, (Collision collision) -> {
//            if (deltaX > 0 && collision.normal.x == -1) result.goalX -= 1;
//            if (deltaX < 0 && collision.normal.x == 1) result.goalX += 1;
        });
    }
    
    @Override
    public void collision(Collisions collisions) {
        System.out.println("frame");
        handleCollisions(collisions, (Collision collision) -> {
            if (collision.other.userData instanceof Block) {
                System.out.println(
                        "collision.normal.x + \" \" + collision.normal.y = " + collision.normal.x + " " + collision.normal.y);
                if (collision.normal.x != 0) {
                    deltaX = 0;
                }
                if (collision.normal.y == 1) {
                    if (deltaY < 0) deltaY = 0;
                    if (mode == FALLING || mode == DOUBLE_JUMPING) {
                        mode = STANDING;
                        animationState.setAnimation(2, animationJumpLand, false);
                        animationState.addAnimation(2, animationStand, false, 0);
                    }
                }
            }
        });
    }
    
    public static class  ZebraCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if (other.userData instanceof Block) return Response.slide;
            return null;
        }
    }
    public static ZebraCollisionFilter zebraCollisionFilter = new ZebraCollisionFilter();
}
