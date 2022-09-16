package com.ray3k.template.entities;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dongbat.jbump.Rect;

import java.util.Comparator;

import static com.ray3k.template.Core.*;

public class EntityController implements Disposable {
    public Array<Entity> entities;
    public Comparator<Entity> depthComparator;
    private Array<Entity> sortedEntities;
    
    public EntityController() {
        entities = new Array<>();
        sortedEntities = new Array<>();
        
        depthComparator = (o1, o2) -> Float.compare(o2.depth, o1.depth);
    }
    
    public void sleepAll() {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            sleep(entity);
        }
    }
    
    public void sleepInside(float left, float bottom, float width, float height, float border) {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            if (entity.isInside(left, bottom, width, height, border)) sleep(entity);
        }
    }
    
    public void sleepOutside(float left, float bottom, float width, float height, float border) {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            if (entity.isOutside(left, bottom, width, height, border)) sleep(entity);
        }
    }
    
    public void wakeAll() {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            wake(entity);
        }
    }
    
    public void wakeInside(float left, float bottom, float width, float height, float border) {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            if (entity.isInside(left, bottom, width, height, border)) wake(entity);
        }
    }
    
    public void wakeOutside(float left, float bottom, float width, float height, float border) {
        for (int i = 0; i < entities.size; i++) {
            var entity = entities.get(i);
            if (entity.isOutside(left, bottom, width, height, border)) wake(entity);
        }
    }
    
    public void sleep(Entity entity) {
        if (entity.sleepable) {
            entity.sleeping = true;
            if (entity.item != null) {
                world.remove(entity.item);
            }
        }
    }
    
    public void wake(Entity entity) {
        entity.sleeping = false;
        if (entity.item != null) {
            world.add(entity.item, entity.x + entity.bboxOriginX, entity.y + entity.bboxOriginY, entity.bboxWidth, entity.bboxHeight);
        }
    }
    
    public void add(Entity entity) {
        entities.add(entity);
        entity.create();
    }
    
    public void remove(Entity entity) {
        entities.removeValue(entity, false);
    }
    
    public void clear() {
        entities.clear();
        sortedEntities.clear();
    }
    
    public void act(float delta) {
        sortedEntities.clear();
        sortedEntities.addAll(entities);
        sortedEntities.sort(depthComparator);
        
        for (Entity entity : sortedEntities) {
            if (entity.collisions != null) entity.collisions.clear();
            entity.actBefore(delta);
        }
        
        //simulate physics and call act methods
        for (Entity entity : sortedEntities) {
            if (entity.moveTargetActivated) entity.moveTargetActivated = !entity.moveTowards(entity.moveTargetSpeed, entity.moveTargetX, entity.moveTargetY, delta);
            entity.deltaX += entity.gravityX * delta;
            entity.deltaY += entity.gravityY * delta;
            
            entity.x += entity.deltaX * delta;
            entity.y += entity.deltaY * delta;
            
            if (entity.skeleton != null) {
                entity.skeleton.setPosition(entity.x, entity.y);
                entity.animationState.update(delta);
                entity.skeleton.updateWorldTransform();
                entity.animationState.apply(entity.skeleton);
                entity.skeletonBounds.update(entity.skeleton, true);
            }
            
            if (entity.item != null && world.hasItem(entity.item)) {
                var result = world.check(entity.item, entity.x + entity.bboxOriginX, entity.y + entity.bboxOriginY, entity.collisionFilter);
                entity.projectedCollision(result);
                world.update(entity.item, result.goalX, result.goalY);
                for (int i = 0; i < result.projectedCollisions.size(); i++) {
                    var collision = result.projectedCollisions.get(i);
                    entity.collisions.add(collision);
                }
                Rect rect = world.getRect(entity.item);
                entity.x = rect.x - entity.bboxOriginX;
                entity.y = rect.y - entity.bboxOriginY;
            }
            
            entity.act(delta);
        }
        
        //call collision methods
        for (var entity : sortedEntities) {
            if (entity.collisions != null && entity.collisions.size() > 0) {
                entity.collision(entity.collisions);
            }
        }
        
        //call destroy methods and remove the entities
        for (Entity entity : sortedEntities) {
            if (entity.destroy) {
                entity.destroy();
                entities.removeValue(entity, false);
                if (entity.item != null && world.hasItem(entity.item)) world.remove(entity.item);
            }
        }
    }
    
    public void draw(float delta) {
        //call draw methods
        for (Entity entity : sortedEntities) {
            if (entity.visible) {
                if (entity.skeleton != null) {
                    //interpolate position
                    entity.skeleton.setPosition(entity.x + entity.deltaX * delta, entity.y + entity.deltaY * delta);
                    entity.skeleton.updateWorldTransform();
                    
                    skeletonRenderer.draw(batch, entity.skeleton);
                }
    
                if (entity.collisionBoxDebugColor != null && shapeDrawer != null) {
                    var rect = world.getRect(entity.item);
                    if (rect != null) shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h, entity.collisionBoxDebugColor, 1.0f);
                }
                
                entity.draw(delta);
            }
        }
    }
    
    @Override
    public void dispose() {
        for (Entity entity : entities) {
            if (entity.item != null && world.hasItem(entity.item)) world.remove(entity.item);
            if (entity instanceof Disposable) ((Disposable) entity).dispose();
        }
        entities.clear();
    }
}