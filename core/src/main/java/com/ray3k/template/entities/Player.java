package com.ray3k.template.entities;

import com.badlogic.gdx.Input.Buttons;
import com.ray3k.template.*;

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
}
