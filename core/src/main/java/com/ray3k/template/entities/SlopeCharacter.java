package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
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
    public float slopeStickForce = 300;
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
    /**
     * If true, the character can cling to walls if moveWallClingRight() or moveWallClingLeft() are called while
     * contacting a wall in the air.
     */
    public boolean allowClingToWalls;
    /**
     * If true, the character can climb up and down while clinging onto a wall if moveClimbUp() or moveClimbDown() are
     * called.
     */
    public boolean allowClimbWalls;
    /**
     * If true, the character can perform a wall jump while clinging onto a wall if moveJump() is called.
     */
    public boolean allowWallJump;
    /**
     * If true, the character can perform wall jump while they are next to a wall in the air without needing to cling on.
     */
    public boolean allowWallJumpWithoutCling;
    /**
     * If true, Automatically cling to walls if the character is adjacent to one in the air.
     */
    public boolean automaticallyClingToWalls;
    /**
     * If true, the character can walk up slopes that typically the character would slide down. The character will still
     * slide downward if the appropriate input is not pressed.
     */
    public boolean allowWalkUpSlides;
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
     * The maximum acceleration for left and right movement that the character has while in the air performing a wall
     * jump. This is only effective for the time while the character is in the air defined by wallJumpDeactivateTime.
     * The actual acceleration is diminished on a curve as the character approaches lateralMaxSpeed.
     */
    public float lateralAirWallJumpingAcceleration = 500;
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
    
    /**
     * The maximum speed that the player will slide down the wall by gravity while wall clinging.
     */
    public float wallSlideMaxSpeed = 400;
    /**
     * How fast the character accelerates downwards while wall clinging.
     */
    public float wallSlideAcceleration = -400;
    /**
     * The maximum speed that the character can climb walls.
     */
    public float wallClimbMaxSpeed = 800;
    /**
     * How fast movement is accelerated while climbing up or down.
     */
    public float wallClimbAcceleration = 5000;
    /**
     * The length of the ray used to check if the player is connected to a wall on the left or right.
     */
    public float wallRayDistance = 20;
    /**
     * The vertical offset of the ray used to check if the player is connected to a wall on the left or right. This is
     * measured from the offset of the foot fixture.
     */
    public float wallClimbRayYoffset;
    /**
     * The speed of the jump when the player is climbing up and reaches the top of the wall. This allows for landing on
     * top of the platform even if the climb speed is slow.
     */
    public float wallClimbLedgeJumpSpeed = 800;
    /**
     * The angle of the wall jump if jumping from a wall on the character's left side. This angle is mirrored over the
     * vertical axis if the wall is on the right side.
     */
    public float wallJumpAngle = 60;
    /**
     * The speed of the wall jump.
     */
    public float wallJumpSpeed = 1200;
    /**
     * The length of time when the character's acceleration is penalized from the wall jump. Acceleration returns to
     * normal afterwards.
     * @see SlopeCharacter#lateralAirWallJumpingAcceleration
     */
    public float wallJumpDeactivateTime = .5f;
    /**
     * The time period from a jump where the character is not allowed to cling to a wall. This prevents the character
     * from clinging to a wall immediately when pressed up in a corner.
     */
    public float clingToWallThreshold = .3f;
    
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
     * The number of midair jumps the character is allowed to conduct. Set to 0 for no mid-air jumps. Set to -1 for
     * unlimited midair jumps.
     */
    public int midairJumps;
    /**
     * The initial velocity of upwards movement when the character presses the jump input while in the air.
     */
    public float midairJumpSpeed = 1500;
    /**
     * The delay between multiple midair jumps.
     */
    public float midairJumpDelay = .5f;
    /**
     * Set to true to allow the character to jump while sliding.
     */
    public boolean allowJumpingWhileSliding;
    
    /**
     * The signed gravity applied to the character while the character is swinging.
     */
    public float swingGravity = -3000;
    /**
     * The velocity added to the character in the direction of the chracter's swing to prevent a lame movement
     */
    public float swingImpulse = 500;
    /**
     * The friction applied to the character movement so that he won't swing endlessly like a pendulum
     */
    public float swingFriction = .5f;
    /**
     * The swing is deactivated when the character reaches the apex of the swing. This is determined as when the swing
     * angle changes direction.
     */
    public boolean allowSwingTerminationAtApex;
    /**
     * If set to false, the character's velocity is set to 0 when the swing is initiated. The swingImpulse is added
     * afterwards.
     */
    public boolean swingMaintainVelocity = true;
    /**
     * The x offset of the swing anchor body attached to the character body.
     */
    public float swingCharacterAnchorOffsetX;
    /**
     * The y offset of the swing anchor body attached to the character body.
     */
    public float swingCharacterAnchorOffsetY;
    /**
     * The maximum speed for left and right movement that the character is allowed to move manually during a swing.
     */
    public float lateralSwingMaxSpeed = 2000;
    /**
     * The maximum acceleration for left and right movement that the character has while swinging. The actual acceleration is diminished on a curve
     * as the character approaches lateralMaxSpeed.
     */
    public float lateralSwingAcceleration = 2500;
    
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
     * True if the character is touching the ground.
     */
    public boolean grounded;
    /**
     * True if the character is in the air.
     */
    public boolean falling;
    /**
     * True if the character initiated a jump.
     */
    public boolean jumping;
    /**
     * True if the character just jumped from a wall. The character's air acceleration is penalized while this is true.
     * @see SlopeCharacter#wallJumpDeactivateTime
     * @see SlopeCharacter#lateralAirWallJumpingAcceleration
     */
    public boolean wallJumping;
    /**
     * True if the character is swinging from a point.
     */
    public boolean swinging;
    /**
     * True if the character was swinging in the last frame.
     */
    public boolean previousSwinging;
    /**
     * The current angle of the swing measured from the player position to the swinging origin.
     */
    public float swingAngle;
    /**
     * The amount of swing angle that has changed since the last frame.
     */
    public float swingDelta;
    /**
     * True if the ground the character is on can be walked on. 90 is completely flat ground.
     */
    public boolean canWalkOnSlope;
    /**
     * True if the ground can be slid on. It must not be walkable ground. 90 is completely flat ground.
     */
    public boolean canSlideOnSlope;
    /**
     * True if the surface the character is on can be jumped from. See allowJumpingWhileSliding.
     */
    public boolean canJump;
    /**
     * True if the character is in the air and can perform a midair jump.
     */
    public boolean canMidairJump;
    /**
     * True if the character is in the air, touching a wall, and can perform a wall jump.
     */
    public boolean canWallJump;
    /**
     * True if touching a wall.
     */
    private boolean touchingWall;
    /**
     * True if the character is clinging to a wall.
     */
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
    private final ObjectSet<Fixture> touchedGroundFixtures = new ObjectSet<>();
    /**
     * The ground fixtures that were touched in the last frame.
     */
    private final ObjectSet<Fixture> lastTouchedGroundFixtures = new ObjectSet<>();
    /**
     * Clears the lastTouchedGroundFixtures when there is a new frame and a new ground contact has been made.
     */
    private boolean clearLastTouchedGroundFixtures;
    /**
     * The origin of the swinging joint. This body does not interact with the world and serves solely to facilitate the
     * character swinging.
     */
    private Body swingAnchorOrigin;
    /**
     * The body attached to the character that facilitates swinging. This body does not interact with the world otherwise.
     */
    private Body swingAnchorCharacter;
    /**
     * The joint associated with the character swinging.
     */
    private RevoluteJoint swingJoint;
    /**
     * The world x position where the swing joint originates.
     */
    private float swingTargetX;
    /**
     * The world y position where the swing joint originates.
     */
    private float swingTargetY;
    /**
     * The previous amount of swing angle that has changed since the last frame.
     */
    private float previousSwingDelta;
    private float jointAngle;
    private float previousJointAngle;
    
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
    /**
     * Character called moveWallClingLeft() for this frame.
     * @see SlopeCharacter#moveWallClingLeft()
     */
    private boolean inputWallClingLeft;
    /**
     * Character called moveWallClingRight() for this frame.
     * @see SlopeCharacter#moveWallClingRight()
     */
    private boolean inputWallClingRight;
    /**
     * Character called moveClimbUp() for this frame.
     * @see SlopeCharacter#moveClimbUp()
     */
    private boolean inputWallClimbUp;
    /**
     * Character called moveClimbDown() for this frame.
     * @see SlopeCharacter#moveClimbDown()
     */
    private boolean inputWallClimbDown;
    private boolean inputSwing;
    private boolean inputSwingJustPressed;
    private final Array<Fixture> passThroughFixtures = new Array<>();
    /**
     * Counts down continuously and is reset when the player begins to fall. Used to compare against coyoteTime.
     */
    private float coyoteTimer;
    /**
     * Counts down after the character initiates a wall jump. This tracks how long the character's air acceleration is
     * penalized.
     * @see SlopeCharacter#lateralAirWallJumpingAcceleration
     * @see SlopeCharacter#wallJumpDeactivateTime
     */
    private float wallJumpTimer;
    /**
     * Counts down after the character initiates a midair jump. This tracks how long until the player can conduct
     * another midair jump.
     */
    private float midairJumpTimer;
    /**
     * Counts the number of midair jumps the character has performed. This value is tested against midairJumps before
     * performing another midair jump.
     * @see SlopeCharacter#midairJumps
     */
    public int midairJumpCounter;
    
    public SlopeCharacter(float footOffsetX, float footOffsetY, float footRadius, float torsoHeight) {
        this.footOffsetX = footOffsetX;
        this.footOffsetY = footOffsetY;
        this.footRadius = footRadius;
    
        footRayOffsetX = footOffsetX;
        footRayOffsetY = footOffsetY - footRadius;
        
        this.torsoHeight = torsoHeight;
        this.wallClimbRayYoffset = (torsoHeight + footRadius) / 2;
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
                    for (var lastTouchedGroundFixture : lastTouchedGroundFixtures) {
                        if (data.checkFixturesBetween(lastTouchedGroundFixture, fixture1 -> {
                            var angle = ((BoundsData) fixture1.getUserData()).angle;
                            return Utils.isEqual360(angle, 90, maxSlideAngle);
                        })) {
                            contactAngle = normal.angleDeg();
                            groundAngle = ((BoundsData) fixture.getUserData()).angle;
                            grounded = true;
                            return 0;
                        }
                    }
                }
                return 1;
            }, p2m(x + footRayOffsetX), p2m(y + footRayOffsetY), p2m(x + footRayOffsetX), p2m(y + footRayOffsetY - footRayDistance));

            if (!grounded) {
                falling = true;
                if (canWalkOnSlope || allowJumpingWhileSliding) coyoteTimer = coyoteTime;
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
        var lastInputSwing = inputSwing;
        inputSwing = false;
        inputSwingJustPressed = false;
        
        coyoteTimer -= delta;
        wallJumpTimer -= delta;
        midairJumpTimer -= delta;
        handleControls();
        if (inputJump && !lastInputJump) inputJumpJustPressed = jumpTriggerDelay;
        if (inputSwing && !lastInputSwing) inputSwingJustPressed = true;
        if (swingAnchorOrigin != null ) {
            swingAngle = Utils.pointDirection(swingTargetX, swingTargetY, x, y);
            previousJointAngle = jointAngle;
            jointAngle = swingJoint.getJointAngle();
            if (previousJointAngle == 0) previousJointAngle = jointAngle;
            previousSwingDelta = swingDelta;
            swingDelta = previousJointAngle - jointAngle;
        } else {
            swingAngle = -1;
            swingDelta = 0;
            previousSwingDelta = 0;
            previousJointAngle = 0;
            jointAngle = 0;
        }
        
        handleMovement(delta);
    
        GameScreen.statsLabel.setText("Movement Mode: " + movementMode +
                "\nTouchedGroundFixtures: " + touchedGroundFixtures.size +
                "\nGrounded: " + grounded +
                "\nFalling: " + falling +
                "\nPassthrough count: " + passThroughFixtures.size +
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
    
    public void moveSwing(float x, float y) {
        inputSwing = true;
        swingTargetX = x;
        swingTargetY = y;
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
        wallJumping = false;
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
    
    private void handleMovement(float delta) {
        var lastClingingToWall = clingingToWall;
        boolean wallToRight = Utils.isEqual360(wallAngle, 180, 90);
        if (!allowClingToWalls || coyoteTimer > -clingToWallThreshold) clingingToWall = false;
        else {
            if (!inputWallClingRight && wallToRight || !inputWallClingLeft && !wallToRight || automaticallyClingToWalls) clingingToWall = false;
            if (falling && (touchingWall || lastClingingToWall)) {
                var clingingToRight = wallToRight && (inputWallClingRight || automaticallyClingToWalls);
                var clingingToLeft = !wallToRight && (inputWallClingLeft || automaticallyClingToWalls);
                if (clingingToRight || clingingToLeft) {
                    clingingToWall = false;
                    var rayX = p2m(x + footRayOffsetX + (clingingToRight ? footRadius : -footRadius));
                    var rayY = p2m(y + footRayOffsetY + wallClimbRayYoffset);
                    world.rayCast((fixture, point, normal, fraction) -> {
                        if (!(fixture.getBody().getUserData() instanceof Bounds)) return -1;
                        clingingToWall = true;
                        return 0;
                    }, rayX, rayY, rayX + p2m(clingingToRight ? wallRayDistance : -wallRayDistance), rayY);
                }
            }
        }
        
        previousSwinging = swinging;
        swinging = inputSwing && falling && !touchingWall;
        if (previousSwinging && swinging && allowSwingTerminationAtApex && swingAnchorOrigin != null && !MathUtils.isZero(previousSwingDelta)) {
            if (Math.signum(swingDelta) != Math.signum(previousSwingDelta)) swinging = false;
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
    
            var slideDown = true;
            if (allowWalkUpSlides) {
                if (inputRight || inputLeft) {
                    slideDown = false;
                    var goRight = inputRight ? 1f : -1f;
                    var acceleration = Math.signum(lateralSpeed) == goRight ? lateralAcceleration : lateralDeceleration;
                    lateralSpeed = Utils.throttledAcceleration(lateralSpeed, goRight * lateralMaxSpeed, goRight * acceleration * delta, maintainExtraLateralMomentum);
                }
            }
            
            if (slideDown) {
                if (Utils.isEqual360(contactAngle, 0, 90)) {
                    lateralSpeed = Utils.throttledAcceleration(lateralSpeed, lateralSlideMaxSpeed,
                            lateralSlideAcceleration * delta, maintainExtraLateralMomentum);
                } else {
                    lateralSpeed = Utils.throttledAcceleration(lateralSpeed, -lateralSlideMaxSpeed,
                            -lateralSlideAcceleration * delta, maintainExtraLateralMomentum);
                }
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
            if (allowClimbWalls && (inputWallClimbUp || inputWallClimbDown)) {
                var goUp = inputWallClimbUp ? 1f : -1f;
                deltaY = Utils.throttledAcceleration(deltaY, goUp * wallClimbMaxSpeed, goUp * wallClimbAcceleration * delta, false);
                gravityY = 0;
            } else {
                gravityY = wallSlideAcceleration;
                var maxSpeed = -Math.abs(wallSlideMaxSpeed);
                if (deltaY > 0) deltaY = Utils.approach(deltaY, 0, wallClimbAcceleration * delta);
                if (deltaY < maxSpeed) deltaY = maxSpeed;
            }
        } else if (swinging) {
            var anchorCharacterX = x + swingCharacterAnchorOffsetX;
            var anchorCharacterY = y + swingCharacterAnchorOffsetY;
            if (inputSwingJustPressed && swingAnchorOrigin == null) {
                
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyType.StaticBody;
                bodyDef.position.set(p2m(swingTargetX), p2m(swingTargetY));
                swingAnchorOrigin = world.createBody(bodyDef);
    
                BodyDef bodyDef2 = new BodyDef();
                bodyDef2.type = BodyType.DynamicBody;
                bodyDef2.position.set(p2m(x), p2m(y));
                swingAnchorCharacter = world.createBody(bodyDef2);
    
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(p2m(10), p2m(10));
                var fixture = swingAnchorOrigin.createFixture(polygonShape, 1f);
                fixture.getFilterData().categoryBits = CATEGORY_NO_CONTACT;
                fixture.getFilterData().maskBits = 0;
                
                fixture = swingAnchorCharacter.createFixture(polygonShape, 1f);
                fixture.getFilterData().categoryBits = CATEGORY_NO_CONTACT;
                fixture.getFilterData().maskBits = 0;
                polygonShape.dispose();
    
                RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
                revoluteJointDef.bodyA = swingAnchorOrigin;
                revoluteJointDef.bodyB = swingAnchorCharacter;
                revoluteJointDef.collideConnected = false;
                revoluteJointDef.localAnchorA.set(0, 0);
                revoluteJointDef.localAnchorB.set(p2m(swingTargetX - anchorCharacterX), p2m(swingTargetY - anchorCharacterY));
                revoluteJointDef.referenceAngle = MathUtils.degRad * Utils.pointDirection(swingTargetX, swingTargetY, anchorCharacterX, anchorCharacterY);
                swingJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
    
                revoluteJointDef = new RevoluteJointDef();
                revoluteJointDef.bodyA = swingAnchorCharacter;
                revoluteJointDef.bodyB = body;
                revoluteJointDef.collideConnected = false;
                revoluteJointDef.localAnchorA.set(0, 0);
                revoluteJointDef.localAnchorB.set(p2m(swingCharacterAnchorOffsetX), p2m(swingCharacterAnchorOffsetY));
                world.createJoint(revoluteJointDef);
                
                bodyVelocityControl = false;
                temp1.set(p2m(swingImpulse), 0);
                temp1.rotateDeg(Utils.pointDirection(anchorCharacterX, anchorCharacterY, swingTargetX, swingTargetY) + (anchorCharacterX < swingTargetX ? -90 : 90));
                if (!swingMaintainVelocity) body.setLinearVelocity(temp1.x, temp1.y);
                else body.setLinearVelocity(body.getLinearVelocity().x + temp1.x, body.getLinearVelocity().y + temp1.y);
            }
            
            body.applyLinearImpulse(0, p2m(swingGravity * delta), p2m(anchorCharacterX), p2m(anchorCharacterY), true);
            body.setLinearVelocity(body.getLinearVelocity().x * (1 - swingFriction * delta), body.getLinearVelocity().y * (1 - swingFriction * delta));
    
            
            if (inputRight || inputLeft) {
                lateralSpeed = m2p(body.getLinearVelocity().x);
                var goRight = inputRight ? 1f : -1f;
                lateralSpeed = Utils.throttledAcceleration(lateralSpeed, goRight * lateralSwingMaxSpeed, goRight * lateralSwingAcceleration * delta, maintainExtraLateralMomentum);
                body.setLinearVelocity(p2m(lateralSpeed), body.getLinearVelocity().y);
            }
        } else {
            movementMode = FALLING;
            gravityY = gravity;
            
            if (wallJumpTimer < 0) wallJumping = false;
    
            if (inputRight || inputLeft) {
                var goRight = inputRight ? 1f : -1f;
                var acceleration = Math.signum(deltaX) == goRight ? lateralAirAcceleration : lateralAirDeceleration;
                if (wallJumping) acceleration = lateralAirWallJumpingAcceleration;
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
                wallJumping = false;
                deltaY *= jumpReleaseDampening;
            }
            
            var term = Math.abs(terminalVelocity);
            if (deltaY < -term) deltaY = -term;
        }
    
        if (previousSwinging && !swinging && swingAnchorOrigin != null) {
            world.destroyBody(swingAnchorOrigin);
            swingAnchorOrigin = null;
            world.destroyBody(swingAnchorCharacter);
            swingAnchorCharacter = null;
            
            bodyVelocityControl = true;
            deltaX = m2p(body.getLinearVelocity().x);
            deltaY = m2p(body.getLinearVelocity().y);
            lateralSpeed = deltaX;
        }
        
        canMidairJump = falling &&  coyoteTimer <= 0 && midairJumpTimer < 0 && (midairJumpCounter < midairJumps || midairJumps == -1);
        canWallJump = !grounded && (clingingToWall || allowWallJumpWithoutCling && touchingWall);
        if (allowJumpingWhileSliding) canJump = grounded && !falling || coyoteTimer > 0 || canMidairJump;
        else canJump = grounded && !falling && canWalkOnSlope || coyoteTimer > 0 || canMidairJump;
    
        if (lastClingingToWall && !clingingToWall && inputWallClimbUp) {
            jumping = false;
            falling = true;
            canJump = false;
            coyoteTimer = 0;
            deltaY = wallClimbLedgeJumpSpeed;
        }
    
        if (allowWallJump && canWallJump && MathUtils.isEqual(inputJumpJustPressed, jumpTriggerDelay)) {
            jumping = true;
            wallJumping = true;
            wallJumpTimer = wallJumpDeactivateTime;
            falling = true;
            canJump = false;
            coyoteTimer = 0;
            inputJumpJustPressed = 0;
            setMotion(wallJumpSpeed, wallToRight ? 180 - wallJumpAngle : wallJumpAngle);
        }
        
        if (canJump) {
            if (inputJumpJustPressed > 0) {
                jumping = true;
                falling = true;
                canJump = false;
                coyoteTimer = 0;
                inputJumpJustPressed = 0;
                if (inputLeft || inputRight) deltaX = lateralSpeed;
                deltaY = !canMidairJump ? jumpSpeed : midairJumpSpeed;
                if (canMidairJump) {
                    midairJumpCounter++;
                    midairJumpTimer = midairJumpDelay;
                }
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
            var rayY = y + footRayOffsetY + wallClimbRayYoffset;
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
            var manifold = contact.getWorldManifold();
            float normalAngle = manifold.getNormal().angleDeg();
            float fixtureAngle = ((BoundsData) otherFixture.getUserData()).angle;
            var bounds = (Bounds) other;
            
            if (bounds.canPassThroughBottom) {
                var alreadyContacting = !checkContactEnabledPassThrough(bounds);
                var hittingHead = falling && !Utils.isEqual360(normalAngle, 90, maxSlideAngle);
                var badAngle = Utils.isEqual360(normalAngle, fixtureAngle + 180, 90);
                if (alreadyContacting || hittingHead || badAngle) {
                    passThroughFixtures.add(otherFixture);
                    if (fixture == torsoFixture || fixture == footFixture) {
                        contact.setEnabled(false);
                        return;
                    }
                }
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
    
    private boolean checkContactEnabledPassThrough(Bounds bounds) {
        if (!bounds.canPassThroughBottom) return true;
        var iter = passThroughFixtures.iterator();
        while (iter.hasNext()) {
            var fixture = iter.next();
            if (bounds == fixture.getBody().getUserData()) return false;
        }
        
        return true;
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            var bounds = (Bounds) other;
            var manifold = contact.getWorldManifold();
            float normalAngle = manifold.getNormal().angleDeg();
            var otherFixtureData = ((BoundsData) otherFixture.getUserData());
            float fixtureAngle = otherFixtureData.angle;
            float nextFixtureAngle = ((BoundsData)otherFixtureData.nextFixture.getUserData()).angle;
            float previousFixtureAngle = ((BoundsData)otherFixtureData.previousFixture.getUserData()).angle;
            
            if (!checkContactEnabledPassThrough(bounds)) {
                contact.setEnabled(false);
                return;
            }
            
            if (fixture == footFixture) {
                canWalkOnSlope = false;
                canSlideOnSlope = false;
    
                if (Utils.isEqual360(fixtureAngle, 90, maxSlideAngle) && deltaY < 0) {
                    if (falling) {
                        falling = false;
                        jumping = false;
                        wallJumping = false;
                        midairJumpCounter = 0;
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
            var bounds = (Bounds) other;
    
            if (!checkContactEnabledPassThrough(bounds)) {
                passThroughFixtures.removeValue(otherFixture, true);
            }
            
            if (fixture == footFixture) {
                touchedGroundFixtures.remove(otherFixture);
            }
        }
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Fixture otherFixture, Contact contact) {
        if (other instanceof Bounds) {
            var bounds = (Bounds) other;
            
            if (!checkContactEnabledPassThrough(bounds)) {
                return;
            }
    
            if (fixture == footFixture) {
                float angle = contact.getWorldManifold().getNormal().angleDeg();
                if (Utils.isEqual360(angle, 90, maxSlideAngle)) contactAngle = angle;
        
                if (!canSlideOnSlope || !canWalkOnSlope) {
                    groundAngle = ((BoundsData) otherFixture.getUserData()).angle;
                    slopeCheck();
                }
            }
        }
    }
}
