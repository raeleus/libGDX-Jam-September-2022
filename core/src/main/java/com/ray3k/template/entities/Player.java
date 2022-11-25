package com.ray3k.template.entities;

import com.badlogic.gdx.Input.Buttons;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineZebra.*;

public class Player extends SlopeCharacter {
    public Player() {
        super(0, 25, 25, 100);
        showDebug = true;
        depth = DEPTH_PLAYER;
        animationData.setDefaultMix(.25f);
        setSkeletonData(skeletonData, animationData);
        maintainExtraLateralMomentum = true;
        allowClingToWalls = true;
        allowClimbWalls = true;
        allowWallJump = true;
        allowWalkUpSlides = true;
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
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        if (Core.isButtonJustPressed(Buttons.RIGHT)) {
            applyAirForce(1500, Utils.pointDirection(x, y, mouseX, mouseY));
        }
    }
}
