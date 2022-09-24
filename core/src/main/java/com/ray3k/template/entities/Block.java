package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Response.Result;

import static com.ray3k.template.Core.*;
import static com.ray3k.template.Resources.SpinePlatformStone.*;

public class Block extends Entity {
    @Override
    public void create() {
        setSkeletonData(skeletonData, animationData);
        setCollisionBox(slotBbox, skeletonBounds, nullCollisionFilter);
//        roundCollisionBox();
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.rectangle(getBboxLeft(), getBboxBottom(), skeletonBounds.getWidth(), skeletonBounds.getHeight(), Color.GREEN, 2f);
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void projectedCollision(Result result) {
    
    }
    
    @Override
    public void collision(Collisions collisions) {
    
    }
}
