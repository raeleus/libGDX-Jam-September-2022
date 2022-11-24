package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.ObjectSet;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;
import com.ray3k.template.screens.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;
import static com.ray3k.template.entities.SlopeCharacter.MovementMode.*;

public abstract class SlopeCharacter extends Entity {
    public enum MovementMode {
        WALKING, SLIDING, FALLING, WALL_CLINGING
    }
    private final static Vector2 temp1 = new Vector2();
    private final static Vector2 temp2 = new Vector2();
    
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
    /**
     * The additional time the character is allowed to jump after falling off a ledge
     */
    public float coyoteTime = .2f;
    /**
     * The multiplier applied to deltaY when the character releases the jump input while deltaY is still positive.
     * This affects how floaty a short jump is. Values 0-1.
     */
    public float jumpReleaseDampening = .3f;
    /**
     * How much leading time the character has to press the jump input before landing to initiate a jump.
     */
    public float jumpTriggerDelay = .2f;
    
    /**
     * If true, the character will stick to the ground and slide on slopes as long as the groundAngle is within maxWalkAngle and maxSlideAngle respectively
     */
    public boolean stickToGround = true;
    public boolean canClingToWalls;
    public boolean canClimbWalls;
    public boolean canWallJump;
    /**
     * If true, the character is allowed to maintain additional momentum if they are holding the input in that direction.
     */
    public boolean maintainExtraLateralMomentum;
    /**
     * The maximum speed that the character is allowed to walk.
     */
    public float lateralMaxSpeed = 800;
    /**
     * The maximum acceleration that the character has while walking. The actual acceleration is diminished on a curve
     * as the character approaches lateralMaxSpeed.
     */
    public float lateralAcceleration = 2500;
    /**
     * The maximum deceleration that the character has while walking. This is implemented when the character presses
     * input in the opposite direction of which they are moving. This value is on a curve and actual acceleration may be higher.
     */
    public float lateralDeceleration = 3500;
    /**
     * The minimum deceleration that the character has when they stop walking. This is implemented when there is no left
     * or right input. This is used when the character is close to lateralMaxSpeed.
     */
    public float lateralStopMinDeceleration = 1000;
    /**
     * The maximum deceleration that the character has when they stop walking. This is implemented when there is no left
     * or right input. The actual deceleration is on a curve where maximum deceleration is experienced when closer to 0.
     */
    public float lateralStopDeceleration = 4000;
    
    /**
     * The maximum speed that the character has when sliding down a slope.
     */
    public float lateralSlideMaxSpeed = 1000;
    /**
     * The maximum accleration that the character has while sliding. The actual acceleration is diminished on a curve
     * as the character approaches lateralMaxSpeed.
     */
    public float lateralSlideAcceleration = 4000;
    
    /**
     * The maximum speed for left and right movement that the character is allowed to move in the air.
     */
    public float lateralAirMaxSpeed = 800;
    /**
     * The maximum acceleration for left and right movement that the character has while in the air. The actual acceleration is diminished on a curve
     * as the character approaches lateralMaxSpeed.
     */
    public float lateralAirAcceleration = 2500;
    /**
     * The maximum deceleration for left and right movement that the character has while in the air. This is implemented when the character presses
     * input in the opposite direction of which they are moving. This value is on a curve and actual acceleration may be higher.
     */
    public float lateralAirDeceleration = 2500;
    /**
     * The minimum deceleration for left and right movement that the character has when they stop moving in the air. This is implemented when there is no left
     * or right input. This is used when the character is close to lateralAirMaxSpeed.
     */
    public float lateralAirStopMinDeceleration = 400;
    /**
     * The maximum deceleration for left and right movement that the character has when they stop moving in the air. This is implemented when there is no left
     * or right input. The actual deceleration is on a curve where maximum deceleration is experienced when closer to 0.
     */
    public float lateralAirStopDeceleration = 500;
    
    public float wallClimbMinWallCoverage = .5f;
    public float wallSlideMaxSpeed = 400;
    public float wallSlideAcceleration = -400;
    public float wallClimbMaxSpeed = 800;
    public float wallClimbAcceleration = 800;
    public float wallRayDistance = 20;
    
    /**
     * The signed gravity applied to the character while the character is in the air.
     */
    public float gravity = -3000;
    /**
     * The initial velocity of upwards movement when the character presses the jump input.
     */
    public float jumpSpeed = 1500;
    /**
     * The maximum downward velocity when the character is in the air.
     */
    public float terminalVelocity = 3000;
    
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
    private boolean clingingToWall;
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
    
    /**
     * The angle of the collision normal when contacting the ground. 90 is collision with completely flat ground.
     * ContactAngle may vary compared to groundAngle depending on if the collision was with a vertex.
     */
    private float contactAngle;
    /**
     * The angle of the ground that the character last touched. 90 is completely flat ground.
     */
    private float groundAngle;
    /**
     * The angle of the last touched wall.
     */
    private float wallAngle;
    /**
     * The ground fixtures that were touched in this frame.
     */
    private ObjectSet<Fixture> touchedGroundFixtures = new ObjectSet<>();
    /**
     * The ground fixtures that were touched in the last frame.
     */
    private ObjectSet<Fixture> lastTouchedGroundFixtures = new ObjectSet<>();
    /**
     * Clears the lastTouchedGroundFixtures when there is a new frame and a new ground contact has been made.
     */
    private boolean clearLastTouchedGroundFixtures;
    
    /**
     * Character called moveLeft() for this frame.
     */
    private boolean inputLeft;
    /**
     * Character called moveRight() for this frame.
     */
    private boolean inputRight;
    /**
     * Character called moveJump() for this frame.
     */
    private boolean inputJump;
    /**
     * Character called moveJump() for this frame when inputJump was false last frame.
     */
    private float inputJumpJustPressed;
    private boolean inputWallClingLeft;
    private boolean inputWallClingRight;
    private boolean inputWallClimbUp;
    private boolean inputWallClimbDown;
    /**
     * Counts down continuously and is reset when the player begins to fall. Used to compare against coyoteTime.
     */
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
        
        gravityY = gravity;
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
        inputWallClimbDown = false;
        inputWallClimbUp = false;
        inputWallClingLeft = false;
        inputWallClingRight = false;
        
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
    
    public void moveWallClingLeft() {
        inputWallClingLeft = true;
    }
    
    public void moveWallClingRight() {
        inputWallClingRight = true;
    }
    
    public void moveClimbUp() {
        inputWallClimbUp = true;
    }
    
    public void moveClimbDown() {
        inputWallClimbDown = true;
    }
    
    /**
     * Applies a force to the character that will only propel them if they are on the ground. They will not be pushed into the air.
     * @param speed
     * @param direction
     */
    public void applyGroundForce(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        temp2.set(lateralSpeed, 0);
        temp2.rotateDeg(groundAngle - 90);
        temp1.add(temp2);
        temp1.rotateDeg(-(groundAngle - 90));
        lateralSpeed = temp1.x;
    }
    
    /**
     * Applies a force to the character that will propel them along the ground and in the air.
     * @param speed
     * @param direction
     */
    public void applyAirForce(float speed, float direction) {
        jumping = false;
        falling = true;
        canJump = false;
        coyoteTimer = 0;
        inputJumpJustPressed = 0;
        addMotion(speed, direction);
        lateralSpeed = deltaX;
    }
    
    /**
     * This method must be overridden to handle chracter controls before movement is applied to the character.
     */
    public abstract void handleControls();
    
    private void applyMovement(float delta) {
        var lastClingingToWall = clingingToWall;
        boolean wallToRight = Utils.isEqual360(wallAngle, 180, 90);
        if (!canClingToWalls) clingingToWall = false;
        else {
            if (!inputWallClingRight && wallToRight || !inputWallClingLeft && !wallToRight) clingingToWall = false;
            if (falling && (touchingWall || lastClingingToWall)) {
                var clingingToRight = wallToRight && inputWallClingRight;
                var clingingToLeft = !wallToRight && inputWallClingLeft;
                if (clingingToRight || clingingToLeft) {
                    clingingToWall = false;
                    var rayX = p2m(x + footRayOffsetX + (clingingToRight ? footRadius : -footRadius));
                    var rayY = p2m(y + footRayOffsetY + (torsoHeight + footRadius) / 2);
                    world.rayCast((fixture, point, normal, fraction) -> {
                        if (!(fixture.getBody().getUserData() instanceof Bounds)) return -1;
                        clingingToWall = true;
                        System.out.println("hit");
                        return 0;
                    }, rayX, rayY, rayX + p2m(clingingToRight ? wallRayDistance : -wallRayDistance), rayY);
                }
            }
        }
        
        if (stickToGround && grounded && canWalkOnSlope && !falling) {
            movementMode = WALKING;
            gravityY = 0;
            
            if (touchedGroundFixtures.size == 0) {
                setMotion(slopeStickForce, contactAngle + 180);
            }
            else setSpeed(0);
            
            if (inputRight || inputLeft) {
                var goRight = inputRight ? 1f : -1f;
                var acceleration = Math.signum(lateralSpeed) == goRight ? lateralAcceleration : lateralDeceleration;
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, goRight * lateralMaxSpeed, goRight * acceleration * delta, maintainExtraLateralMomentum);
            } else {
                lateralSpeed = Utils.throttledDeceleration(lateralSpeed, lateralMaxSpeed, lateralStopMinDeceleration * delta, lateralStopDeceleration * delta);
            }
            
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else if (stickToGround && grounded && !canWalkOnSlope && canSlideOnSlope && !falling) {
            movementMode = SLIDING;
            gravityY = 0;
    
            if (touchedGroundFixtures.size == 0) {
                setMotion(slopeStickForce, contactAngle + 180);
            }
            else setSpeed(0);
    
            if (Utils.isEqual360(contactAngle, 0, 90)) {
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, lateralSlideMaxSpeed, lateralSlideAcceleration * delta, maintainExtraLateralMomentum);
            } else {
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, -lateralSlideMaxSpeed, -lateralSlideAcceleration * delta, maintainExtraLateralMomentum);
            }
    
            if (touchingWall) {
                if (lateralSpeed > 0 && Utils.isEqual360(wallAngle, 180, 90)) lateralSpeed = 0;
                else if (lateralSpeed < 0 && Utils.isEqual360(wallAngle, 0, 90)) lateralSpeed = 0;
            }
            
            addMotion(lateralSpeed, contactAngle - 90f);
        } else if (clingingToWall) {
            movementMode = WALL_CLINGING;
            deltaX = 0;
            if (!lastClingingToWall) deltaY = 0;
            gravityY = wallSlideAcceleration;
            var maxSpeed = -Math.abs(wallSlideMaxSpeed);
            if (deltaY < maxSpeed) deltaY = maxSpeed;
        } else {
            movementMode = FALLING;
            gravityY = gravity;
    
            if (inputRight || inputLeft) {
                var goRight = inputRight ? 1f : -1f;
                var acceleration = Math.signum(deltaX) == goRight ? lateralAirAcceleration : lateralAirDeceleration;
                deltaX = Utils.throttledAcceleration(deltaX, goRight * lateralAirMaxSpeed, goRight * acceleration * delta, maintainExtraLateralMomentum);
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
            
            var term = Math.abs(terminalVelocity);
            if (deltaY < -term) deltaY = -term;
        }
    
        if (allowJumpingWhileSliding) canJump = grounded && !falling || coyoteTimer > 0;
        else canJump = grounded && !falling && canWalkOnSlope || coyoteTimer > 0;
        
        if (canJump) {
            if (inputJumpJustPressed > 0) {
                jumping = true;
                falling = true;
                canJump = false;
                coyoteTimer = 0;
                inputJumpJustPressed = 0;
                if (inputLeft || inputRight) deltaX = lateralSpeed;
                deltaY = jumpSpeed;
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
    
            var rayX = x + footRayOffsetX + footRadius;
            var rayY = y + footRayOffsetY + (torsoHeight + footRadius) / 2;
            shapeDrawer.line(rayX, rayY, rayX + wallRayDistance, rayY);
            rayX = x + footRayOffsetX - footRadius;
            shapeDrawer.line(rayX, rayY, rayX - wallRayDistance, rayY);
    
            shapeDrawer.setColor(Color.RED);
            shapeDrawer.setDefaultLineWidth(5f);
            temp1.set(20, 0);
            temp1.rotateDeg(contactAngle);
            shapeDrawer.line(x, y, x + temp1.x, y + temp1.y);
    
            shapeDrawer.setColor(Color.BLUE);
            shapeDrawer.setDefaultLineWidth(5f);
            temp1.set(20, 0);
            temp1.rotateDeg(groundAngle);
            shapeDrawer.line(x, y, x + temp1.x, y + temp1.y);
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
