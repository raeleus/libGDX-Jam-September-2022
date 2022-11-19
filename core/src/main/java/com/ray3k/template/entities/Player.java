package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;
import com.ray3k.template.screens.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;

public class Player extends Entity {
    private Fixture footSensor;
    private Fixture headSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture bodyFixture;
    private Fixture footFixture;
    private final Array<Fixture> footSensorBlocks = new Array<>();
    private final Array<Fixture> rightSensorBlocks = new Array<>();
    private final Array<Fixture> leftSensorBlocks = new Array<>();
    private final Array<Fixture> headSensorBlocks = new Array<>();
    private final Array<Fixture> footContactBlocks = new Array<>();
    private final Array<Fixture> rightContactBlocks = new Array<>();
    private final Array<Fixture> leftContactBlocks = new Array<>();
    private final Array<Fixture> headContactBlocks = new Array<>();
    private final Array<Fixture> collisionBoxContactBlocks = new Array<>();
    private final static Vector2 temp = new Vector2();
    private boolean grounded;
    private final float radius = 25;
    private final float maxSlopeAngle = 50;
    private final float maxSlideAngle = 80;
    private final float maxCeilingHitAngle = 30;
    private final float maxCeilingAngle = 85;
    private final float slopeCheckDistanceH = 30;
    private final float slopeCheckDistanceV = 90;
    private final float slopeStickForce = 1000;
    private boolean canWalkOnSlope;
    private boolean canSlideOnSlope;
    private boolean canJump;
    private boolean falling;
    private EdgeShape groundShape;
    private float contactAngle;
    private float groundAngle;
    private float lateralSpeed;
    private ObjectSet<Fixture> touchedGroundFixtures = new ObjectSet<>();
    private ObjectSet<Fixture> lastTouchedGroundFixtures = new ObjectSet<>();
    private boolean touchingWall;
    private float wallAngle;
    private boolean hitHead;
    private boolean clearLastTouchedGroundFixtures;
    
    @Override
    public void create() {
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        
        footFixture = setCollisionCircle(0, radius, radius, BodyType.DynamicBody);
        footFixture.getFilterData().categoryBits = CATEGORY_ENTITY;
        footFixture.getFilterData().maskBits = CATEGORY_BOUNDS;
        
        bodyFixture = setCollisionBox(slotBbox, BodyType.DynamicBody);
        bodyFixture.getFilterData().categoryBits = CATEGORY_ENTITY;
        bodyFixture.getFilterData().maskBits = CATEGORY_BOUNDS;
        
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
        touchingWall = false;
        hitHead = false;
        clearLastTouchedGroundFixtures = true;
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
        
        grounded = touchedGroundFixtures.size > 0;
        
        if (!grounded && !falling) {
            world.rayCast((fixture, point, normal, fraction) -> {
                if (fixture.getBody().getUserData() instanceof Bounds) {
                    var data = (BoundsData) fixture.getUserData();
                    if (lastTouchedGroundFixtures.contains(data.previousFixture) || lastTouchedGroundFixtures.contains(data.nextFixture) || lastTouchedGroundFixtures.contains(fixture)) {
                        groundShape = (EdgeShape) fixture.getShape();
                        contactAngle = normal.angleDeg();
                        groundAngle = ((BoundsData)fixture.getUserData()).angle;
                        grounded = true;
                        return 0;
                    }
                }
                return 1;
            }, p2m(x), p2m(y), p2m(x), p2m(y - slopeCheckDistanceV));

            if (!grounded) falling = true;

            slopeCheck();
        }
        
        applyMovement(delta);
    
        GameScreen.statsLabel.setText("Grounded: " + grounded +
                "\nFalling: " + falling +
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
//            System.out.println("slope");
            gravityY = 0;
            
            if (footContactBlocks.size == 0) setMotion(slopeStickForce, contactAngle + 180);
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
//            System.out.println("sliding");
            gravityY = 0;
    
            temp1.set(x, y);
            groundShape.getVertex1(temp2);
            temp2.set(m2p(temp2.x), m2p(temp2.y));
            groundShape.getVertex2(temp3);
            temp3.set(m2p(temp3.x), m2p(temp3.y));
            var closest = Utils.closestPointInLine(temp1, temp2, temp3);
    
            if (footContactBlocks.size == 0) setMotion(slopeStickForce, contactAngle + 180);
            else setSpeed(0);
            
            lateralSpeed = Utils.approach(lateralSpeed, Utils.isEqual360(contactAngle, 0, 90) ? playerMaxWalkSpeed : -playerMaxWalkSpeed, playerWalkAcceleration * delta);
    
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else {
//            System.out.println("air");
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
        shapeDrawer.line(x - slopeCheckDistanceH, y, x + slopeCheckDistanceH, y);
        shapeDrawer.line(x, y, x, y - slopeCheckDistanceV);
        
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
            if (fixture == footSensor) {
                footSensorBlocks.add(otherFixture);
            } else if (fixture == rightSensor) {
                rightSensorBlocks.add(otherFixture);
            } else if (fixture == leftSensor) {
                leftSensorBlocks.add(otherFixture);
            } else if (fixture == headSensor) {
                headSensorBlocks.add(otherFixture);
            } else if (fixture == footFixture) {
                collisionBoxContactBlocks.add(otherFixture);
            }
    
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
            float normalAngle = contact.getWorldManifold().getNormal().angleDeg();
            float fixtureAngle = ((BoundsData) otherFixture.getUserData()).angle;
            
            if (fixture == footFixture) {
                groundShape = null;
                canWalkOnSlope = false;
                canSlideOnSlope = false;
    
                if (Utils.isEqual360(fixtureAngle, 90,
                        maxSlideAngle) && deltaY < 0) {
                    if (falling) {
                        falling = false;
                        lateralSpeed = deltaX;
                    }
                }
    
                if (Utils.isEqual360(normalAngle, 90, maxSlideAngle)) {
                    contact.setFriction(1f);
                } else {
                    contact.setFriction(0f);
                    touchingWall = true;
                    wallAngle = fixtureAngle;
                }
            } else if (fixture == bodyFixture) {
                if (Utils.isEqual360(fixtureAngle, 270, maxCeilingAngle) && Utils.isEqual360(normalAngle, 270, maxCeilingAngle)) {
                    contact.setEnabled(true);
                    if (Utils.isEqual360(fixtureAngle, 270, maxCeilingHitAngle) && Utils.isEqual360(normalAngle, 270, maxCeilingHitAngle)) {
                        hitHead = true;
                    }
                } else {
                    contact.setEnabled(false);
                }
            }
        }
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            if (fixture == footSensor) {
                footSensorBlocks.removeValue(otherFixture, true);
            } else if (fixture == rightSensor) {
                rightSensorBlocks.removeValue(otherFixture, true);
            } else if (fixture == leftSensor) {
                leftSensorBlocks.removeValue(otherFixture, true);
            } else if (fixture == headSensor) {
                headSensorBlocks.removeValue(otherFixture, true);
            } else if (fixture == footFixture) {
                collisionBoxContactBlocks.removeValue(otherFixture, true);
            }
    
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
                groundShape = (EdgeShape) otherFixture.getShape();
                groundAngle = ((BoundsData) otherFixture.getUserData()).angle;
                slopeCheck();
            }
        }
    }
}
