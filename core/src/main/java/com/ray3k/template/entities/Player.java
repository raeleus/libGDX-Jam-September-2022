package com.ray3k.template.entities;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.Animation;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;

public class Player extends SlopeCharacter {
    private Animation movementAnimation;
    private boolean movementAnimationLoop;
    private boolean faceRight;
    public static Player player;
    
    public Player() {
        super(0, 25, 25, 100);
    }
    
    @Override
    public void create() {
        super.create();
    
        showDebug = true;
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        maintainExtraLateralMomentum = true;
        allowClingToWalls = true;
        allowGrabLedges = false;
        allowClimbWalls = false;
        allowWallJump = true;
        allowWalkUpSlides = true;
        automaticallyClingToWalls = false;
        allowWallJumpWithoutCling = true;
        midairJumps = -1;
        swingImpulse = 0;
        swingCharacterAnchorOffsetY = 50;
        animationState.setAnimation(0, animationBlink, true);
        animationState.setAnimation(1, animationTail, true);
        movementAnimation = animationJumpFall;
        body.setSleepingAllowed(false);
        allowClingToCeilings = true;
        allowMagnet = true;
        
        player = this;
    }
    
    @Override
    public void handleControls() {
        if (Core.isBindingPressed(Binding.LEFT)) moveLeft();
        else if (Core.isBindingPressed(Binding.RIGHT)) moveRight();
        
        if (Core.isBindingPressed(Binding.JUMP)) moveJump();
        if (Core.isBindingPressed(Binding.LEFT)) moveWallClingLeft();
        if (Core.isBindingPressed(Binding.RIGHT)) moveWallClingRight();
        if (Core.isBindingPressed(Binding.UP)) moveClimbUp();
        if (Core.isBindingPressed(Binding.DOWN)) moveClimbDown();
        if (Core.isBindingPressed(Binding.DOWN)) movePassThroughFloor();
        if (Core.isBindingPressed(Binding.UP)) moveCeilingCling();
        
        if (Core.isButtonPressed(Buttons.LEFT)) moveSwing(mouseX, mouseY);
        if (Core.isBindingPressed(Binding.SHOOT)) moveMagnet();
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        if (Core.isButtonJustPressed(Buttons.RIGHT)) {
            applyAirForce(3000, Utils.pointDirection(x, y, mouseX, mouseY));
        }
        
        var currentAnimation = animationState.getCurrent(2);
        if (currentAnimation == null || currentAnimation.getAnimation() != movementAnimation) animationState.setAnimation(2, movementAnimation, movementAnimationLoop);
        skeleton.getRootBone().setScaleX(faceRight ? 1 : -1);
    }
    
    @Override
    public void eventWalking(float delta, float lateralSpeed, float groundAngle) {
        if (movementAnimation == animationJumpLand && !animationState.getCurrent(2).isComplete()) return;
        movementAnimation = animationWalk;
        faceRight = lateralSpeed > 0;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventWalkStopping(float delta, float lateralSpeed, float groundAngle) {
        if (movementAnimation == animationJumpLand && !animationState.getCurrent(2).isComplete()) return;
        movementAnimation = animationStand;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventWalkStop(float delta) {
        if (movementAnimation == animationJumpLand && !animationState.getCurrent(2).isComplete()) return;
        movementAnimation = animationStand;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventWalkReversing(float delta, float lateralSpeed, float groundAngle) {
//        movementAnimation = animationSkid;
        faceRight = lateralSpeed > 0;
    }
    
    @Override
    public void eventWalkingSlide(float delta, float lateralSpeed, float groundAngle) {
        if (movementAnimation == animationJumpLand && !animationState.getCurrent(2).isComplete()) return;
        movementAnimation = animationWalk;
        faceRight = lateralSpeed > 0;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventWalkPushingWall(float delta, float wallAngle) {
        movementAnimation = animationPush;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventCliffEdge(float delta, boolean right) {

    }
    
    @Override
    public void eventTouchGroundFixture(Fixture fixture, float contactNormalAngle, Bounds bounds,
                                        BoundsData boundsData) {

    }
    
    @Override
    public void eventSlideSlope(float delta, float lateralSpeed, float groundAngle, float slidingAngle) {
        if (movementAnimation == animationJumpLand && !animationState.getCurrent(2).isComplete()) return;
        movementAnimation = animationWalk;
        faceRight = lateralSpeed > 0;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventSlidePushingWall(float delta, float wallAngle) {

    }
    
    @Override
    public void eventJump(float delta) {
        movementAnimation = animationJump;
        movementAnimationLoop = false;
    }
    
    @Override
    public void eventJumpReleased(float delta) {
        movementAnimation = animationJumpFall;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventJumpApex(float delta) {
        movementAnimation = animationJumpFall;
        movementAnimationLoop = true;
    }
    
    @Override
    public void eventJumpFromSlide(float delta) {
        movementAnimation = animationJump;
        movementAnimationLoop = false;
    }
    
    @Override
    public void eventJumpMidair(float delta) {
        movementAnimation = animationJumpDouble;
    }
    
    @Override
    public void eventHitHead(float delta, float ceilingAngle) {

    }
    
    @Override
    public void eventFalling(float delta) {
        faceRight = deltaX > 0;
        if (deltaY < 0) {
            movementAnimation = animationJumpFall;
            movementAnimationLoop = true;
        } else if (movementAnimation == animationJump && animationState.getCurrent(2).isComplete()) {
            movementAnimation = animationJumpAir;
            movementAnimationLoop = true;
        }
    }
    
    @Override
    public void eventFallingTouchingWall(float delta, float wallAngle) {

    }
    
    @Override
    public void eventLand(float delta, float groundAngle) {
        movementAnimation = animationJumpLand;
        movementAnimationLoop = false;
    }
    
    @Override
    public void eventWallCling(float delta, float wallAngle) {
        faceRight = Utils.isEqual360(wallAngle, 180, 90);
    }
    
    @Override
    public void eventWallSliding(float delta, float wallAngle) {

    }
    
    @Override
    public void eventWallClimbing(float delta, float wallAngle) {

    }
    
    @Override
    public void eventWallClimbReachedTop(float delta) {

    }
    
    @Override
    public void eventWallJump(float delta, float wallAngle) {
        movementAnimation = animationJump;
        movementAnimationLoop = false;
    }
    
    @Override
    public void eventLedgeJump(float delta, float wallAngle) {
        movementAnimation = animationJump;
        movementAnimationLoop = false;
    }
    
    @Override
    public void eventPassedThroughPlatform(Fixture fixture, float fixtureAngle, Bounds bounds, BoundsData boundsData) {

    }
    
    @Override
    public void eventSwing(float delta, float swingAngle, float lateralSpeed) {

    }
    
    @Override
    public void eventSwinging(float delta, float swingAngle, float lateralSpeed) {

    }
    
    @Override
    public void eventSwingReleased(float delta, float swingAngle, float lateralSpeed, boolean automaticRelease) {

    }
    
    @Override
    public void eventSwingCrashWall(float delta, float swingAngle, float lateralSpeed) {
    
    }
    
    @Override
    public void eventSwingCrashGround(float delta, float swingAngle, float lateralSpeed) {

    }
    
    @Override
    public void eventReleaseWallCling(float delta) {
    
    }
    
    @Override
    public void eventGrabLedge(float delta, float wallAngle) {
    
    }
    
    @Override
    public void eventReleaseGrabLedge(float delta) {
    
    }
    
    @Override
    public void eventCeilingClingPushingWall(float delta, float wallContactAngle) {
    
    }
    
    @Override
    public void eventCeilingClingStop(float delta) {
    
    }
    
    @Override
    public void eventCeilingClingStopping(float previousSwingDelta, float lateralSpeed, float ceilingAngle) {
    
    }
    
    @Override
    public void eventCeilingClingMoving(float delta, float lateralSpeed, float ceilingAngle) {
    
    }
    
    @Override
    public void eventCeilingClingMovingReversing(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventCeilingClingReleased(float delta) {
    
    }
    
    @Override
    public void eventMagnetPushingWall(float delta, float wallContactAngle) {
    
    }
    
    @Override
    public void eventMagnetStop(float delta) {
    
    }
    
    @Override
    public void eventMagnetStopping(float previousSwingDelta, float lateralSpeed, float ceilingAngle) {
    
    }
    
    @Override
    public void eventMagnetMoving(float delta, float lateralSpeed, float ceilingAngle) {
    
    }
    
    @Override
    public void eventMagnetMovingReversing(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventMagnetReleased(float delta) {
    
    }
}