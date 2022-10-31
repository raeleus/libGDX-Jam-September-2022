package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpineSkidDust.*;

public class SkidSmoke extends Entity {
    @Override
    public void create() {
        depth = DEPTH_PARTICLES;
        setSkeletonData(skeletonData, animationData);
        animationState.setAnimation(0, animationAnimation, false);
        skeleton.getRootBone().setRotation(MathUtils.random(360f));
        animationState.addListener(new AnimationStateAdapter() {
            @Override
            public void complete(TrackEntry entry) {
                destroy = true;
            }
        });
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void preSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void beginContact(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void endContact(Entity other, Fixture fixture, Contact contact) {
    
    }
    
    @Override
    public void postSolve(Entity other, Fixture fixture, Contact contact) {
    
    }
}
