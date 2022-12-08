package com.ray3k.template.entities;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.ray3k.template.*;
import com.ray3k.template.entities.Bounds.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;

public class Player extends SlopeCharacter {
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
        allowClimbWalls = true;
        allowWallJump = true;
        allowWalkUpSlides = true;
        automaticallyClingToWalls = false;
        allowWallJumpWithoutCling = true;
        this.wallSlideAcceleration = 0;
        midairJumps = -1;
        swingImpulse = 0;
        swingCharacterAnchorOffsetY = 50;
        animationState.setAnimation(0, animationBlink, true);
        animationState.setAnimation(1, animationTail, true);
        animationState.setAnimation(2, animationJumpFall, true);
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
        
        if (Core.isButtonPressed(Buttons.LEFT)) moveSwing(mouseX, mouseY);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        if (Core.isButtonJustPressed(Buttons.RIGHT)) {
            applyAirForce(3000, Utils.pointDirection(x, y, mouseX, mouseY));
        }
    }
    
    @Override
    public void eventWalking(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventWalkStopping(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventWalkStop(float delta) {
    
    }
    
    @Override
    public void eventWalkReversing(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventWalkingSlide(float delta, float lateralSpeed, float groundAngle) {
    
    }
    
    @Override
    public void eventWalkPushingWall(float delta, float wallAngle) {
    
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
    
    }
    
    @Override
    public void eventSlidePushWall(float delta, float wallAngle) {
    
    }
    
    @Override
    public void eventJump(float delta) {
    
    }
    
    @Override
    public void eventJumpReleased(float delta) {
    
    }
    
    @Override
    public void eventJumpFromSlope(float delta) {
    
    }
    
    @Override
    public void eventJumpMidair(float delta) {
    
    }
    
    @Override
    public void eventHitHead(float delta, float ceilingAngle) {
    
    }
    
    @Override
    public void eventFalling(float delta) {
    
    }
    
    @Override
    public void eventFallingTouchingWall(float delta, float wallAngle) {
    
    }
    
    @Override
    public void eventLand(float delta, float groundAngle) {
    
    }
    
    @Override
    public void eventWallCling(float delta, float wallAngle) {
    
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
}
