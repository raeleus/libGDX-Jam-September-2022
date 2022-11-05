package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;

import static com.ray3k.template.Core.*;

public abstract class Entity {
    private static final Vector2 temp1 = new Vector2();
    private static final Vector2 temp2 = new Vector2();
    public Skeleton skeleton;
    public AnimationState animationState;
    public SkeletonBounds skeletonBounds;
    public Body body;
    public float x;
    public float y;
    public float moveTargetX;
    public float moveTargetY;
    public float moveTargetSpeed;
    public boolean moveTargetActivated;
    public float bboxOriginX;
    public float bboxOriginY;
    public float bboxWidth;
    public float bboxHeight;
    public float deltaX;
    public float deltaY;
    public boolean destroy;
    public float gravityX;
    public float gravityY;
    public boolean visible = true;
    public float depth;
    public Color collisionBoxDebugColor;
    public boolean sleepable = true;
    public boolean sleeping;
    
    public abstract void create();
    public abstract void actBefore(float delta);
    public abstract void act(float delta);
    public abstract void draw(float delta);
    public abstract void destroy();
    public abstract void beginContact(Entity other, Fixture fixture, Contact contact);
    public abstract void endContact(Entity other, Fixture fixture, Contact contact);
    public abstract void preSolve(Entity other, Fixture fixture, Contact contact);
    public abstract void postSolve(Entity other, Fixture fixture, Contact contact);
    
    public void setMotion(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        deltaX = temp1.x;
        deltaY = temp1.y;
    }
    
    public void addMotion(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        deltaX += temp1.x;
        deltaY += temp1.y;
    }
    
    public void moveTowards(float speed, float x, float y) {
        temp1.set(x, y);
        temp1.sub(this.x, this.y);
        setMotion(speed, temp1.angleDeg());
    }
    
    /**
     *
     * @param speed
     * @param x
     * @param y
     * @param delta
     * @return if target has been reached.
     */
    public boolean moveTowards(float speed, float x, float y, float delta) {
        if (MathUtils.isEqual(this.x, x)) this.x = x;
        if (MathUtils.isEqual(this.y, y)) this.y = y;
        
        if (MathUtils.isEqual(this.x, x) && MathUtils.isEqual(this.y, y)) {
            deltaX = 0;
            deltaY = 0;
            return true;
        } else {
            temp1.set(x, y);
            temp1.sub(this.x, this.y);
            setMotion(Math.min(speed, temp1.len() / delta), temp1.angleDeg());
            return false;
        }
    }
    
    public boolean moveTowardsTarget(float speed, float targetX, float targetY) {
        moveTargetSpeed = speed;
        moveTargetX = targetX;
        moveTargetY = targetY;
        
        moveTargetActivated = !MathUtils.isEqual(x, moveTargetX) || !MathUtils.isEqual(y, moveTargetY);
        if (!moveTargetActivated) setSpeed(0);
        return moveTargetActivated;
    }
    
    public void moveTowardsTarget(boolean activated) {
        moveTargetActivated = activated;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void teleport(float x, float y) {
        this.x = x;
        this.y = y;
        if (body != null) {
            body.setTransform(p2m(x), p2m(y), 0);
        }
    }
    
    public void teleport() {
        if (body != null) {
            body.setTransform(p2m(x), p2m(y), 0);
        }
    }
    
    public void setSpeed(float speed) {
        setMotion(speed, getDirection());
    }
    
    public float getSpeed() {
        temp1.set(deltaX, deltaY);
        return temp1.len();
    }
    
    public float getDirection() {
        temp1.set(deltaX, deltaY);
        return temp1.angleDeg();
    }
    
    public void setDirection(float direction) {
        setMotion(getSpeed(), direction);
    }
    
    public float getGravityDirection() {
        temp1.set(gravityX, gravityY);
        return temp1.angleDeg();
    }
    
    public float getGravitySpeed() {
        temp1.set(gravityX, gravityY);
        return temp1.len();
    }
    
    public void setGravity(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        gravityX = temp1.x;
        gravityY = temp1.y;
    }
    
    public void addGravity(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        gravityX += temp1.x;
        gravityY += temp1.y;
    }
    
    public void setSkeletonData(SkeletonData skeletonData, AnimationStateData animationStateData) {
        skeleton = new Skeleton(skeletonData);
        animationState = new AnimationState(animationStateData);
        skeletonBounds = new SkeletonBounds();
        
        skeleton.setPosition(x, y);
        skeleton.updateWorldTransform();
        animationState.apply(skeleton);
        skeletonBounds.update(skeleton, true);
    }
    
    public Bone findBone(BoneData boneData) {
        Object[] bones = skeleton.getBones().toArray();
        for (int i = 0, n = skeleton.getBones().size; i < n; i++) {
            Bone bone = (Bone)bones[i];
            if (bone.getData() == boneData) return bone;
        }
        return null;
    }
    
    public Fixture setCollisionBox(float offsetX, float offsetY, float width, float height, BodyType bodyType) {
        bboxOriginX = offsetX;
        bboxOriginY = offsetY;
        bboxWidth = width;
        bboxHeight = height;
        
        if (body == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = bodyType;
            bodyDef.position.set(x, y);
            bodyDef.fixedRotation = true;
    
            body = world.createBody(bodyDef);
            body.setUserData(this);
        }
    
        PolygonShape box = new PolygonShape();
        temp1.set(p2m(offsetX + width / 2), p2m(offsetY + height / 2));
        box.setAsBox(p2m(width / 2), p2m(height / 2), temp1, 0);
    
        var fixture = body.createFixture(box, .5f);
        fixture.setFriction(0);
        box.dispose();
        
        return fixture;
    }
    
    private static float[] verts;
    public Fixture setCollisionBox(Slot slot, BodyType bodyType) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        var bbox = (BoundingBoxAttachment) slot.getAttachment();
        if (bbox != null) {
            if (verts == null || verts.length < bbox.getWorldVerticesLength()) verts = new float[bbox.getWorldVerticesLength()];
            bbox.computeWorldVertices(slot, 0, bbox.getWorldVerticesLength(), verts, 0, 2);
            for (int i = 0; i < bbox.getWorldVerticesLength(); i += 2) {
                if (verts[i] < minX) minX = verts[i];
                if (verts[i] > maxX) maxX = verts[i];
                if (verts[i+1] < minY) minY = verts[i+1];
                if (verts[i+1] > maxY) maxY = verts[i+1];
            }
        }
        return setCollisionBox(minX - x, minY - y, maxX - minX, maxY - minY, bodyType);
    }
    
    public Fixture setCollisionBox(SlotData slotData, BodyType bodyType) {
        Object[] slots = skeleton.getSlots().items;
        Slot returnValue = null;
        for (int i = 0, n = skeleton.getSlots().size; i < n; i++) {
            var slot = (Slot) slots[i];
            if (slot.getData() == slotData) {
                returnValue = slot;
                break;
            }
        }
        
        if (returnValue == null) return null;
        return setCollisionBox(returnValue, bodyType);
    }
    
    public Fixture setSensorBox(float offsetX, float offsetY, float width, float height, BodyType bodyType) {
        if (body == null) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = bodyType;
            bodyDef.position.set(x, y);
            bodyDef.fixedRotation = true;
            
            body = world.createBody(bodyDef);
            body.setUserData(this);
        }
        
        PolygonShape box = new PolygonShape();
        temp1.set(p2m(offsetX + width / 2), p2m(offsetY + height / 2));
        box.setAsBox(p2m(width / 2), p2m(height / 2), temp1, 0);
        
        var fixture = body.createFixture(box, .5f);
        fixture.setSensor(true);
        box.dispose();
        return fixture;
    }
    
    public Fixture setSensorBox(Slot slot, BodyType bodyType) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        var bbox = (BoundingBoxAttachment) slot.getAttachment();
        if (bbox != null) {
            if (verts == null || verts.length < bbox.getWorldVerticesLength()) verts = new float[bbox.getWorldVerticesLength()];
            bbox.computeWorldVertices(slot, 0, bbox.getWorldVerticesLength(), verts, 0, 2);
            for (int i = 0; i < bbox.getWorldVerticesLength(); i += 2) {
                if (verts[i] < minX) minX = verts[i];
                if (verts[i] > maxX) maxX = verts[i];
                if (verts[i+1] < minY) minY = verts[i+1];
                if (verts[i+1] > maxY) maxY = verts[i+1];
            }
        }
        return setSensorBox(minX - x, minY - y, maxX - minX, maxY - minY, bodyType);
    }
    
    public Fixture setSensorBox(SlotData slotData, BodyType bodyType) {
        Object[] slots = skeleton.getSlots().items;
        Slot foundSlot = null;
        for (int i = 0, n = skeleton.getSlots().size; i < n; i++) {
            var slot = (Slot) slots[i];
            if (slot.getData() == slotData) {
                foundSlot = slot;
                break;
            }
        }
        
        if (foundSlot == null) return null;
        return setSensorBox(foundSlot, bodyType);
    }
    
    public boolean isOutside(float left, float bottom, float width, float height) {
        return isOutside(left, bottom, width, height, 0);
    }
    
    public boolean isInside(float left, float bottom, float width, float height, float border) {
        return !isOutside(left, bottom, width, height, border);
    }
    
    public boolean isInside(float left, float bottom, float width, float height) {
        return !isOutside(left, bottom, width, height);
    }
    
    public boolean isOutside(float left, float bottom, float width, float height, float border) {
        return getBboxRight() < left - border || getBboxLeft() > left + width + border || getBboxTop() < bottom - border || getBboxBottom() > bottom + height + border;
    }
    
    public float getBboxLeft() {
        return x + bboxOriginX;
    }
    
    public float getBboxRight() {
        return x + bboxOriginX + bboxWidth;
    }
    
    public float getBboxBottom() {
        return y + bboxOriginY;
    }
    
    public float getBboxTop() {
        return y + bboxOriginY + bboxHeight;
    }
    
    public float getBboxCenterX() {
        return x + bboxOriginX + bboxWidth / 2;
    }
    
    public float getBboxCenterY() {
        return y + bboxOriginY + bboxHeight / 2;
    }
    
    public void setBboxLeft(float x) {
        this.x = x - bboxOriginX;
    }
    
    public void setBboxRight(float x) {
        this.x = x - bboxOriginX - bboxWidth;
    }
    
    public void setBboxBottom(float y) {
        this.y = y - bboxOriginY;
    }
    
    public void setBboxTop(float y) {
        this.y = y - bboxOriginY - bboxHeight;
    }
    
    public void setBboxCenterX(float x) {
        this.x = x - bboxOriginX - bboxWidth / 2;
    }
    
    public void setBboxCenterY(float y) {
        this.y = y - bboxOriginY - bboxHeight / 2;
    }
    
    public Animation getAnimation(int track) {
        return animationState.getCurrent(track).getAnimation();
    }
}