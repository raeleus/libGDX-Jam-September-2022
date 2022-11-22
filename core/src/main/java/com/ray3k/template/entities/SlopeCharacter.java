package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.ObjectSet;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;
import com.ray3k.template.screens.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.Resources.Values.*;
import static com.ray3k.template.entities.SlopeCharacter.MovementMode.*;

public abstract class SlopeCharacter extends Entity {
    public enum MovementMode {
        WALKING, SLIDING, FALLING
    }
    private final static Vector2 temp = new Vector2();
    
    /**
     * The radius of the circle foot fixture that will contact the ground.
     */
    public final float footRadius;
    /**
     * The x offset of the circle foot fixture that will contact the ground.
     */
    public final float footOffsetX;
    /**
     * The y offset of the circle foot fixture that will contact the ground.
     */
    public final float footOffsetY;
    /**
     * The x offset of the origin of the ray that finds the ground immediately below the foot fixture.
     */
    public final float footRayOffsetX;
    /**
     * The y offset of the origin of the ray that finds the ground immediately below the foot fixture.
     */
    public final float footRayOffsetY;
    /**
     * The height of the character's torso which originates from the center of the foot fixture.
     */
    public final float torsoHeight;
    
    /**
     * The angle tolerance for walkable slopes. 0 would only allow walking on completely flat surfaces. 90 would allow walking on all slopes.
     */
    public float maxWalkAngle = 50;
    /**
     * The angle tolerance for slidable slopes. 0 would only allow sliding on completely flat surfaces. 90 would allow sliding on all slopes. Must be larger than maxWalkAngle.
     */
    public float maxSlideAngle = 75;
    /**
     * The angle tolerance for ceiling collisions. The angle of the normal necessary to move the character out of the collision is compared to 270. 0 would only allow for head collisions that are directly up
     */
    public float maxCeilingAngle = 85;
    /**
     * The distance of the ray that begins at the bottom of the foot fixture and points downward to detect the ground. This must be sufficiently long enough in order to detect steep slopes.
     */
    public float footRayDistance = 90;
    /**
     * The velocity used to correct the position of the character above the ground when it's floating above a slope. This usually only occurs when going down hill.
     */
    public float slopeStickForce = 100;
    public float coyoteTime = .2f;
    public float jumpReleaseDampening = .3f;
    public float jumpTriggerDelay = .2f;
    
    public float lateralMaxSpeed = 800;
    public float lateralAcceleration = 2500;
    public float lateralDeceleration = 3500;
    public float lateralStopMinDeceleration = 1000;
    public float lateralStopDeceleration = 4000;
    
    public float lateralSlideMaxSpeed = 1000;
    public float lateralSlideAcceleration = 4000;
    
    public float lateralAirMaxSpeed = 800;
    public float lateralAirAcceleration = 2500;
    public float lateralAirDeceleration = 2500;
    public float lateralAirStopMinDeceleration = 400;
    public float lateralAirStopDeceleration = 500;
    
    /**
     * Set to true to allow the character to jump while sliding.
     */
    public boolean allowJumpingWhileSliding;
    /**
     * Set to true to draw slope debug lines.
     */
    public boolean showDebug;
    
    /**
     * The main fixture of the body that is used to contact walls and ceilings.
     */
    public Fixture torsoFixture;
    /**
     * The bottom fixture of the body that is used to contact the ground.
     */
    public Fixture footFixture;
    
    /**
     * true if the character is touching the ground.
     */
    public boolean grounded;
    /**
     * true if the character is in the air.
     */
    public boolean falling;
    /**
     * true if the character initiated a jump.
     */
    public boolean jumping;
    /**
     * true if the ground the character is on can be walked on. 90 is completely flat ground.
     */
    public boolean canWalkOnSlope;
    /**
     * true if the ground can be slid on. It must not be walkable ground. 90 is completely flat ground.
     */
    public boolean canSlideOnSlope;
    /**
     * true if the surface the character is on can be jumped from. See allowJumpingWhileSliding.
     */
    public boolean canJump;
    /**
     * true if touching a wall.
     */
    private boolean touchingWall;
    /**
     * true if hitting a ceiling.
     */
    private boolean hitHead;
    /**
     * The speed that the character moves across the ground. Movement is parallel to the ground slope.
     */
    public float lateralSpeed;
    /**
     * The movement mode of the character: walking, sliding, or falling.
     */
    public MovementMode movementMode;
    
    private float contactAngle;
    private float groundAngle;
    private float wallAngle;
    private ObjectSet<Fixture> touchedGroundFixtures = new ObjectSet<>();
    private ObjectSet<Fixture> lastTouchedGroundFixtures = new ObjectSet<>();
    private boolean clearLastTouchedGroundFixtures;
    
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputJump;
    private float inputJumpJustPressed;
    private float coyoteTimer;
    
    public SlopeCharacter(float footOffsetX, float footOffsetY, float footRadius, float torsoHeight) {
        this.footOffsetX = footOffsetX;
        this.footOffsetY = footOffsetY;
        this.footRadius = footRadius;
    
        footRayOffsetX = footOffsetX;
        footRayOffsetY = footOffsetY - footRadius;
        
        this.torsoHeight = torsoHeight;
    }
    
    @Override
    public void create() {
        footFixture = setCollisionCircle(footOffsetX, footOffsetY, footRadius, BodyType.DynamicBody);
        footFixture.getFilterData().categoryBits = CATEGORY_ENTITY;
        footFixture.getFilterData().maskBits = CATEGORY_BOUNDS;
        
        torsoFixture = setCollisionBox(-footRadius + footOffsetX, footOffsetY, footRadius * 2, torsoHeight, BodyType.DynamicBody);
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

            if (!grounded) {
                falling = true;
                coyoteTimer = coyoteTime;
            }
        }
        slopeCheck();
        
        inputLeft = false;
        inputRight = false;
        var lastInputJump = inputJump;
        inputJump = false;
        inputJumpJustPressed -= delta;
        coyoteTimer -= delta;
        handleControls();
        if (inputJump && !lastInputJump) inputJumpJustPressed = jumpTriggerDelay;
        
        applyMovement(delta);
    
        GameScreen.statsLabel.setText("Movement Mode: " + movementMode +
                "\nGrounded: " + grounded +
                "\nFalling: " + falling +
                "\nHit Head: " + hitHead +
                "\nTouched Ground Fixtures: " + touchedGroundFixtures.size +
                "\nLateral Speed: " + lateralSpeed +
                "\nGround Angle: " + groundAngle +
                "\nTouching Wall: " + touchingWall +
                "\nCoyote Timer: " + coyoteTimer +
                "\nCan Jump: " + canJump +
                "\ndeltaX: " + deltaX);
    }
    
    private void slopeCheck() {
        canWalkOnSlope = Utils.isEqual360(groundAngle, 90, maxWalkAngle);
        canSlideOnSlope = !canWalkOnSlope && Utils.isEqual360(groundAngle, 90, maxSlideAngle);
        
        if (allowJumpingWhileSliding) canJump = grounded && !falling || coyoteTimer > 0;
        else canJump = grounded && !falling && canWalkOnSlope || coyoteTimer > 0;
    }
    
    public void moveLeft() {
        inputLeft = true;
    }
    
    public void moveRight() {
        inputRight = true;
    }
    
    public void moveJump() {
        inputJump = true;
    }
    
    /**
     * This method must be overridden to handle player controls before movement is applied to the character.
     */
    public abstract void handleControls();
    
    private void applyMovement(float delta) {
        if (grounded && canWalkOnSlope && !falling) {
            movementMode = WALKING;
            gravityY = 0;
            
            if (touchedGroundFixtures.size == 0) {
                setMotion(slopeStickForce, contactAngle + 180);
            }
            else setSpeed(0);
            
            if (inputRight || inputLeft) {
                var goRight = inputRight ? 1f : -1f;
                var acceleration = Math.signum(lateralSpeed) == goRight ? lateralAcceleration : lateralDeceleration;
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, goRight * lateralMaxSpeed, goRight * acceleration * delta, false);
            } else {
                lateralSpeed = Utils.throttledDeceleration(lateralSpeed, lateralMaxSpeed, lateralStopMinDeceleration * delta, lateralStopDeceleration * delta);
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
    
            if (Utils.isEqual360(contactAngle, 0, 90)) {
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, lateralSlideMaxSpeed, lateralSlideAcceleration * delta, false);
            } else {
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, -lateralSlideMaxSpeed, -lateralSlideAcceleration * delta, false);
            }
    
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else {
            movementMode = FALLING;
            gravityY = -playerGravity;
    
            if (inputRight || inputLeft) {
                var goRight = inputRight ? 1f : -1f;
                var acceleration = Math.signum(deltaX) == goRight ? lateralAirAcceleration : lateralAirDeceleration;
                deltaX = Utils.throttledAcceleration(deltaX, goRight * lateralAirMaxSpeed, goRight * acceleration * delta, false);
            } else {
                deltaX = Utils.throttledDeceleration(deltaX, lateralAirMaxSpeed, lateralAirStopMinDeceleration * delta, lateralAirStopDeceleration * delta);
            }
            lateralSpeed = deltaX;
    
            if (touchingWall) {
                if (deltaX > 0 && Utils.isEqual360(wallAngle, 180, 90)) deltaX = 0;
                else if (deltaX < 0 && Utils.isEqual360(wallAngle, 0, 90)) deltaX = 0;
            }
            
            if (hitHead) {
                if (deltaY > 0) deltaY = 0;
            }
            
            if (jumping && deltaY > 0 && !inputJump) {
                jumping = false;
                deltaY *= jumpReleaseDampening;
            }
        }
        
        if (canJump) {
            if (inputJumpJustPressed > 0) {
                jumping = true;
                falling = true;
                canJump = false;
                coyoteTimer = 0;
                inputJumpJustPressed = 0;
                if (inputLeft || inputRight) deltaX = lateralSpeed;
                deltaY = playerJumpSpeed;
            }
        }
    }
    
    @Override
    public void draw(float delta) {
        if (showDebug) {
            shapeDrawer.setColor(Color.GREEN);
            shapeDrawer.setDefaultLineWidth(5f);
            shapeDrawer.line(x + footRayOffsetX, y + footRayOffsetY, x + footOffsetX,
                    y + footOffsetY - footRayDistance);
    
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
                        jumping = false;
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
