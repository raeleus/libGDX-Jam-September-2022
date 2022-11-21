package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ObjectSet;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;
import com.ray3k.template.screens.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;
import static com.ray3k.template.entities.Player.MovementMode.*;

public class Player extends Entity {
    public enum MovementMode {
        WALKING, SLIDING, FALLING
    }
    private final static Vector2 temp = new Vector2();
    
    public final float footRadius;
    public final float footOffsetX;
    public final float footOffsetY;
    public final float footRayOffsetX;
    public final float footRayOffsetY;
    public final float torsoHeight;
    
    public float maxSlopeAngle = 50;
    public float maxSlideAngle = 80;
    public float maxCeilingAngle = 85;
    public float footRayDistance = 90;
    public float slopeStickForce = 100;
    
    private Fixture torsoFixture;
    private Fixture footFixture;
    
    public boolean grounded;
    public boolean falling;
    public boolean canWalkOnSlope;
    public boolean canSlideOnSlope;
    public boolean canJump;
    private boolean touchingWall;
    private boolean hitHead;
    public float lateralSpeed;
    public MovementMode movementMode;
    
    private float contactAngle;
    private float groundAngle;
    private float wallAngle;
    private ObjectSet<Fixture> touchedGroundFixtures = new ObjectSet<>();
    private ObjectSet<Fixture> lastTouchedGroundFixtures = new ObjectSet<>();
    private boolean clearLastTouchedGroundFixtures;
    
    public Player(float footOffsetX, float footOffsetY, float footRadius, float torsoHeight) {
        this.footOffsetX = footOffsetX;
        this.footOffsetY = footOffsetY;
        this.footRadius = footRadius;
    
        footRayOffsetX = footOffsetX;
        footRayOffsetY = footOffsetY - footRadius;
        
        this.torsoHeight = torsoHeight;
    }
    
    @Override
    public void create() {
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        
        footFixture = setCollisionCircle(footOffsetX, footOffsetY, footRadius, BodyType.DynamicBody);
        footFixture.getFilterData().categoryBits = CATEGORY_ENTITY;
        footFixture.getFilterData().maskBits = CATEGORY_BOUNDS;
        
        torsoFixture = setCollisionBox(-footRadius + footOffsetX, footOffsetY, footRadius * 2, torsoHeight, BodyType.DynamicBody);
//        bodyFixture = setCollisionBox(slotBbox, BodyType.DynamicBody);
        torsoFixture.getFilterData().categoryBits = CATEGORY_ENTITY;
        torsoFixture.getFilterData().maskBits = CATEGORY_BOUNDS;
        
        animationState.setAnimation(0, animationBlink, true);
        animationState.setAnimation(1, animationTail, true);
        animationState.setAnimation(2, animationJumpFall, true);
        
        gravityY = -playerGravity;
    }
    
    @Override
    public void actBefore(float delta) {
        touchingWall = false;
        hitHead = false;
        clearLastTouchedGroundFixtures = true;
    }
    
    @Override
    public void act(float delta) {
        camera.position.set(getBboxCenterX(), getBboxCenterY(), 0);
        
        grounded = touchedGroundFixtures.size > 0;
        
        if (!grounded && !falling) {
            world.rayCast((fixture, point, normal, fraction) -> {
                if (fixture.getBody().getUserData() instanceof Bounds) {
                    var data = (BoundsData) fixture.getUserData();
                    if (lastTouchedGroundFixtures.contains(data.previousFixture) || lastTouchedGroundFixtures.contains(data.nextFixture) || lastTouchedGroundFixtures.contains(fixture)) {
                        contactAngle = normal.angleDeg();
                        groundAngle = ((BoundsData)fixture.getUserData()).angle;
                        grounded = true;
                        return 0;
                    }
                }
                return 1;
            }, p2m(x + footRayOffsetX), p2m(y + footRayOffsetY), p2m(x + footRayOffsetX), p2m(y + footRayOffsetY - footRayDistance));

            if (!grounded) falling = true;

            slopeCheck();
        }
        
        applyMovement(delta);
    
        GameScreen.statsLabel.setText("Movement Mode: " + movementMode +
                "\nGrounded: " + grounded +
                "\nFalling: " + falling +
                "\nHit Head: " + hitHead +
                "\nTouched Ground Fixtures: " + touchedGroundFixtures.size +
                "\nLateral Speed: " + lateralSpeed +
                "\nGround Angle: " + groundAngle +
                "\nTouching Wall: " + touchingWall +
                "\ndeltaX: " + deltaX);
    }
    
    private void slopeCheck() {
        canWalkOnSlope = Utils.isEqual360(groundAngle, 90, maxSlopeAngle);
        canSlideOnSlope = Utils.isEqual360(groundAngle, 90, maxSlideAngle);
        canJump = grounded && !falling && canWalkOnSlope;
    }
    
    private static final Vector2 temp1 = new Vector2();
    private static final Vector2 temp2 = new Vector2();
    private static final Vector2 temp3 = new Vector2();
    
    private void applyMovement(float delta) {
        if (grounded && canWalkOnSlope && !falling) {
            movementMode = WALKING;
            gravityY = 0;
            
            if (touchedGroundFixtures.size == 0) {
                setMotion(slopeStickForce, contactAngle + 180);
            }
            else setSpeed(0);
            
            if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                lateralSpeed = Utils.approach(lateralSpeed, isBindingPressed(Binding.RIGHT) ? playerMaxWalkSpeed : -playerMaxWalkSpeed, playerWalkAcceleration * delta);
            } else {
                lateralSpeed = Utils.approach(lateralSpeed, 0, playerWalkDeceleration * delta);
            }
            
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else if (grounded && !canWalkOnSlope && canSlideOnSlope && !falling) {
            movementMode = SLIDING;
            gravityY = 0;
    
            if (touchedGroundFixtures.size == 0) {
                setMotion(slopeStickForce, contactAngle + 180);
            }
            else setSpeed(0);
            
            lateralSpeed = Utils.approach(lateralSpeed, Utils.isEqual360(contactAngle, 0, 90) ? playerMaxWalkSpeed : -playerMaxWalkSpeed, playerWalkAcceleration * delta);
    
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else {
            movementMode = FALLING;
            gravityY = -playerGravity;
            if (isAnyBindingPressed(Binding.RIGHT, Binding.LEFT)) {
                deltaX = Utils.approach(deltaX, isBindingPressed(Binding.RIGHT) ? playerMaxWalkSpeed : -playerMaxWalkSpeed, playerWalkAcceleration * delta);
            } else deltaX = Utils.approach(deltaX, 0, playerWalkDeceleration * delta);
    
            if (touchingWall) {
                if (deltaX > 0 && Utils.isEqual360(wallAngle, 180, 90)) deltaX = 0;
                else if (deltaX < 0 && Utils.isEqual360(wallAngle, 0, 90)) deltaX = 0;
            }
            
            if (hitHead) {
                if (deltaY > 0) deltaY = 0;
            }
        }
        
        if (canJump) {
            if (isBindingJustPressed(Binding.JUMP)) {
                falling = true;
                canJump = false;
                deltaY = playerJumpSpeed;
            }
        }
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.setColor(Color.GREEN);
        shapeDrawer.setDefaultLineWidth(5f);
        shapeDrawer.line(x + footRayOffsetX, y + footRayOffsetY, x + footOffsetX, y + footOffsetY - footRayDistance);
        
        shapeDrawer.setColor(Color.RED);
        shapeDrawer.setDefaultLineWidth(5f);
        temp.set(20, 0);
        temp.rotateDeg(contactAngle);
        shapeDrawer.line(x, y, x + temp.x, y + temp.y);
    
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(5f);
        temp.set(20, 0);
        temp.rotateDeg(groundAngle);
        shapeDrawer.line(x, y, x + temp.x, y + temp.y);
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            if (fixture == footFixture && Utils.isEqual360(((BoundsData)otherFixture.getUserData()).angle, 90, maxSlideAngle)) {
                if (clearLastTouchedGroundFixtures) {
                    lastTouchedGroundFixtures.clear();
                    clearLastTouchedGroundFixtures = false;
                }
                touchedGroundFixtures.add(otherFixture);
                lastTouchedGroundFixtures.add(otherFixture);
            }
        }
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            var manifold = contact.getWorldManifold();
            float normalAngle = manifold.getNormal().angleDeg();
            float fixtureAngle = ((BoundsData) otherFixture.getUserData()).angle;
            
            if (fixture == footFixture) {
                canWalkOnSlope = false;
                canSlideOnSlope = false;
    
                if (Utils.isEqual360(fixtureAngle, 90, maxSlideAngle) && deltaY < 0) {
                    if (falling) {
                        falling = false;
                        lateralSpeed = deltaX;
                    }
                }
    
                contact.setFriction(0f);
            } else if (fixture == torsoFixture) {
                contact.setFriction(0f);
    
                if (Utils.isEqual360(normalAngle, 270, maxCeilingAngle)) {
                    hitHead = true;
                }
                
                if (!Utils.isEqual360(normalAngle, 90, maxSlideAngle)) {
                    touchingWall = true;
                    wallAngle = normalAngle;
                }
            }
        }
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            if (fixture == footFixture) {
                touchedGroundFixtures.remove(otherFixture);
            }
        }
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (fixture == footFixture && other instanceof Bounds) {
            float angle = contact.getWorldManifold().getNormal().angleDeg();
            if (Utils.isEqual360(angle, 90, maxSlideAngle)) contactAngle = angle;
            
            if (!canSlideOnSlope || !canWalkOnSlope) {
                groundAngle = ((BoundsData) otherFixture.getUserData()).angle;
                slopeCheck();
            }
        }
    }
}
