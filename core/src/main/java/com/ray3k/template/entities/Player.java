package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;

public class Player extends Entity {
    private Fixture footSensor;
    private Fixture headSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture collisionBox;
    private Array<Entity> footSensorBlocks = new Array<>();
    private Array<Entity> rightSensorBlocks = new Array<>();
    private Array<Entity> leftSensorBlocks = new Array<>();
    private Array<Entity> headSensorBlocks = new Array<>();
    private Array<Entity> footContactBlocks = new Array<>();
    private Array<Entity> rightContactBlocks = new Array<>();
    private Array<Entity> leftContactBlocks = new Array<>();
    private Array<Entity> headContactBlocks = new Array<>();
    private Array<Entity> collisionBoxContactBlocks = new Array<>();
    private static Vector2 temp = new Vector2();
    private boolean isOnSlope;
    private boolean isGrounded;
    private float slopeDownAngle;
    private final float maxSlopeAngle = 50;
    private final float slopeCheckDistance = 30;
    private float slopeSideAngle;
    private float slopeNormalPerp;
    private float lastSlopeAngle;
    private boolean canWalkOnSlope;
    private boolean canJump;
    private boolean isJumping;
    private boolean touchedTheGround;
    
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
        
        slopeCheck();
        applyMovement();
    }
    
    private void slopeCheck() {
        //check ground
        if (!touchedTheGround) touchedTheGround = footContactBlocks.size > 0;
        isGrounded = footContactBlocks.size > 0;
        if (isJumping && isGrounded && deltaY <= 0) {
            isJumping = false;
        }
        
        isOnSlope = false;
        //horizontal check
        world.rayCast((fixture, point, normal, fraction) -> {
            if (fixture.getBody().getUserData() instanceof Bounds) {
                isOnSlope = true;
                slopeSideAngle = normal.angleDeg();
                return 1;
            } else return -1;
        
        }, p2m(x), p2m(y), p2m(x + slopeCheckDistance), p2m(y));
    
        if (!isOnSlope) world.rayCast((fixture, point, normal, fraction) -> {
            if (fixture.getBody().getUserData() instanceof Bounds) {
                isOnSlope = true;
                slopeSideAngle = normal.angleDeg();
                return 1;
            } else return -1;
        }, p2m(x), p2m(y), p2m(x - slopeCheckDistance), p2m(y));
        
        //vertical check
        world.rayCast((fixture, point, normal, fraction) -> {
            if (touchedTheGround) isGrounded = true;
            slopeDownAngle = normal.angleDeg();
            slopeNormalPerp = normal.rotate90(1).angleDeg();
        
            if (slopeDownAngle != lastSlopeAngle) {
                isOnSlope = true;
            }
        
            lastSlopeAngle = slopeDownAngle;
        
            return 1;
        }, p2m(x), p2m(y), p2m(x), p2m(y - slopeCheckDistance));
    
        if ((Utils.isEqual360(slopeDownAngle, 90, maxSlopeAngle) || Utils.isEqual360(slopeDownAngle, 270, maxSlopeAngle))
                && (Utils.isEqual360(slopeSideAngle, 90, maxSlopeAngle) || Utils.isEqual360(slopeSideAngle, 270, maxSlopeAngle))) {
            canWalkOnSlope = true;
        } else {
            canWalkOnSlope = false;
        }
        
        if (!isGrounded) touchedTheGround = false;
        canJump = isGrounded && !isJumping && (!isOnSlope || canWalkOnSlope);
    }
    
    private void applyMovement() {
        if (isGrounded && !isOnSlope && !isJumping) {
            if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                setMotion(playerMaxWalkSpeed, isBindingPressed(Binding.RIGHT) ? 0 : 180);
            } else setSpeed(0);
        } else if (isGrounded && isOnSlope && canWalkOnSlope && !isJumping) {
            if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                setMotion(playerMaxWalkSpeed,
                        isBindingPressed(Binding.RIGHT) ? slopeNormalPerp + 180 : slopeNormalPerp);
            } else setSpeed(0);
        } else {
            if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                deltaX = isBindingPressed(Binding.RIGHT) ? playerMaxWalkSpeed : -playerMaxWalkSpeed;
            }
        }
        
        if (canJump) {
            if (isBindingPressed(Binding.JUMP)) {
                isJumping = true;
                canJump = false;
                deltaY = playerJumpSpeed;
            }
        }
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.setColor(Color.GREEN);
        shapeDrawer.setDefaultLineWidth(5f);
        shapeDrawer.line(x - slopeCheckDistance, y, x + slopeCheckDistance, y);
        shapeDrawer.line(x, y, x, y - slopeCheckDistance);
        
        shapeDrawer.setColor(Color.RED);
        shapeDrawer.setDefaultLineWidth(5f);
        temp.set(20, 0);
        temp.rotateDeg(slopeDownAngle);
        shapeDrawer.line(x, y, x + temp.x, y + temp.y);
        
        
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
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
    public void preSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (fixture == collisionBox && other instanceof Bounds) {
            var boundsAngle = (contact.getWorldManifold().getNormal().angleDeg() + 90);
            if (Utils.isEqual360(boundsAngle, 180, maxSlopeAngle) || Utils.isEqual360(boundsAngle, 0, maxSlopeAngle)) {
                contact.setFriction(1);
            } else {
                contact.setFriction(.1f);
            }
        }
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
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
    public void postSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
    
    }
}
