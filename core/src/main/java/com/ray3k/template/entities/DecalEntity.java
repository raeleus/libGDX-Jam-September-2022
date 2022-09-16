package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Response.Result;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;

public class DecalEntity extends Entity {
    private Sprite region;
    public boolean panning = true;
    
    public DecalEntity(Sprite region, int centerX, int centerY) {
        this.region = region;
        x = centerX - region.getWidth() / 2f;
        y = centerY - region.getHeight() / 2f;
    }
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        if (panning) {
            region.setPosition(x, y);
            region.draw(batch);
        } else {
            var camera = Core.camera;
            var viewport = Core.viewport;
            region.setPosition(camera.position.x - viewport.getWorldWidth() / 2 + x * camera.zoom, camera.position.y - viewport.getWorldHeight() / 2 + y * camera.zoom);
            region.setScale(camera.zoom);
            region.draw(batch);
        }
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
