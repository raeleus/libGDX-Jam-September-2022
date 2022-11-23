package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.ray3k.template.Core.*;
import com.ray3k.template.entities.*;
import regexodus.Matcher;
import regexodus.Pattern;

public class Utils {
    private static EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
    private static FloatArray floatArray = new FloatArray();
    private static final Vector3 v3Temp1 = new Vector3();
    private static final Vector3 v3Temp2 = new Vector3();
    private static final BoundingBox bboxTemp = new BoundingBox();
    private static final Ray rayTemp = new Ray();
    private static final Vector2 vector2 = new Vector2();
    private static Pattern fileNamePattern = new Pattern("([^/.]+)(?:\\.?[^/.])*$");
    private static final Rectangle tempRectangle1 = new Rectangle();
    private static Pattern noExtensionPattern = new Pattern(".*(?=\\.)");
    
    public static Array<Actor> getActorsRecursive(Actor actor) {
        Array<Actor> actors = new Array<>();
        if (actor instanceof Group) {
            actors.addAll(((Group) actor).getChildren());
            
            for (int i = 0; i < ((Group) actor).getChildren().size; i++) {
                Actor child = ((Group) actor).getChild(i);
                Array<Actor> newActors = getActorsRecursive(child);
                actors.addAll(newActors);
            }
        }
        
        return actors;
    }
    
    public static float[] skeletonBoundsToTriangles(SkeletonBounds skeletonBounds) {
        floatArray.clear();
        
        for (FloatArray points : skeletonBounds.getPolygons()) {
            ShortArray shortArray = earClippingTriangulator.computeTriangles(points);
            for (int i = 0; i < shortArray.size; i++) {
                floatArray.add(shortArray.get(i));
            }
        }
        return floatArray.items;
    }
    
    public static float[] boundingBoxAttachmentToTriangles(SkeletonBounds skeletonBounds, BoundingBoxAttachment boundingBoxAttachment) {
        floatArray.clear();
        var points = skeletonBounds.getPolygon(boundingBoxAttachment);
        ShortArray shortArray = earClippingTriangulator.computeTriangles(points);
        for (int i = 0; i < shortArray.size; i++) {
            floatArray.add(points.get(shortArray.get(i) * 2));
            floatArray.add(points.get(shortArray.get(i) * 2 + 1));
        }
        return floatArray.toArray();
    }
    
    public static Rectangle verticesToAABB(FloatArray vertices) {
        float minX = vertices.get(0), maxX = vertices.get(0), minY = vertices.get(1), maxY = vertices.get(1);
        for (int i = 2; i + 1 < vertices.size; i += 2) {
            if (vertices.get(i) < minX) minX = vertices.get(i);
            if (vertices.get(i+1) < minY) minY = vertices.get(i+1);
            
            if (vertices.get(i) > maxX) maxX = vertices.get(i);
            if (vertices.get(i+1) > maxY) maxY = vertices.get(i+1);
        }
        
        tempRectangle1.set(minX, minY, maxX - minX, maxY - minY);
        return tempRectangle1;
    }
    
    public static Color inverseColor(Color color) {
        return new Color(1 - color.r, 1 - color.g, 1 - color.b, color.a);
    }
    
    public static Color blackOrWhiteBgColor(Color color) {
        return brightness(color) > .5f ? new Color(Color.BLACK) : new Color(Color.WHITE);
    }
    
    public static float brightness(Color color) {
        return (float) (Math.sqrt(0.299f * Math.pow(color.r, 2) + 0.587 * Math.pow(color.g, 2) + 0.114 * Math.pow(color.b, 2)));
    }
    
    public static int colorToInt(Color color) {
        return ((int)(255 * color.r) << 24) | ((int)(255 * color.g) << 16) | ((int)(255 * color.b) << 8) | ((int)(255 * color.a));
    }
    
    public static float floorPot(float value) {
        float returnValue = 0.0f;
        for (float newValue = 2.0f; newValue < value; newValue *= 2.0f) {
            returnValue = newValue;
        }
        
        return returnValue;
    }
    
    public static Pixmap textureRegionToPixmap(TextureRegion textureRegion) {
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap returnValue = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), Pixmap.Format.RGBA8888);
        returnValue.setBlending(Pixmap.Blending.None);
        
        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y);
                returnValue.drawPixel(x, y, colorInt);
            }
        }
        
        pixmap.dispose();
        
        return returnValue;
    }
    
    public static Cursor textureRegionToCursor(TextureRegion textureRegion, int xHotspot, int yHotspot) {
        return Gdx.graphics.newCursor(textureRegionToPixmap(textureRegion), xHotspot, yHotspot);
    }
    
    public static String fileName(String path) {
        Matcher matcher = fileNamePattern.matcher(path);
        matcher.find();
        return matcher.group(1);
    }
    
    public static String filePathNoExtension(String path) {
        Matcher matcher = noExtensionPattern.matcher(path);
        matcher.find();
        return matcher.group(0);
    }
    
    public static String mouseButtonToString(int button) {
        String returnValue = "Unknown";
        switch (button) {
            case Input.Buttons.LEFT:
                returnValue = "Left Click";
                break;
            case Input.Buttons.RIGHT:
                returnValue = "Right Click";
                break;
            case Input.Buttons.MIDDLE:
                returnValue = "Middle Click";
                break;
            case Input.Buttons.BACK:
                returnValue = "Back Button";
                break;
            case Input.Buttons.FORWARD:
                returnValue = "Forward Button";
                break;
        }
        
        return returnValue;
    }
    
    public static String scrollAmountToString(int amount) {
        if (amount < 0) {
            return "Scroll Up";
        } else if (amount > 0) {
            return "Scroll Down";
        } else {
            return "No Scroll";
        }
    }
    
    public static String controllerButtonToString(ControllerValue controllerValue) {
        return controllerValue == Core.ANY_CONTROLLER_BUTTON ? "ANY CONTROLLER BUTTON" : "Pad" + (controllerValue.controller == null ? "?" : Controllers.getControllers().indexOf(controllerValue.controller, true)) + " Button " + controllerValue.value;
    }
    
    public static String controllerAxisToString(ControllerValue controllerValue) {
        return controllerValue == Core.ANY_CONTROLLER_AXIS ? "ANY CONTROLLER AXIS" : "Pad" + (controllerValue.controller == null ? "?" : Controllers.getControllers().indexOf(controllerValue.controller, true)) + " Axis " + controllerValue.axisCode + " " + controllerValue.value;
    }
    
    public static float distanceBetween(Entity entity, Entity other) {
        var right = entity.getBboxRight();
        var left = entity.getBboxLeft();
        var top = entity.getBboxTop();
        var bottom = entity.getBboxBottom();
        var otherRight = entity.getBboxRight();
        var otherLeft = entity.getBboxLeft();
        var otherTop = entity.getBboxTop();
        var otherBottom = entity.getBboxBottom();
        vector2.set(right < otherLeft ? right : left, top < otherBottom ? top : bottom);
        return vector2.dst(right < otherLeft ? otherLeft : otherRight, top < otherBottom ? otherBottom : otherTop);
    }
    
    public static float pointDistance(float x1, float y1, float x2, float y2) {
        vector2.set(x1, y1);
        return vector2.dst(x2, y2);
    }
    
    public static float pointDistance(Entity entity, Entity other) {
        return pointDistance(entity.x, entity.y, other.x, other.y);
    }
    
    public static float pointDirection(float x1, float y1, float x2, float y2) {
        vector2.set(x2, y2);
        vector2.sub(x1, y1);
        return vector2.angleDeg();
    }
    
    private static final Vector2 temp1 = new Vector2();
    private static final Vector2 temp2 = new Vector2();
    public static float angleToHitMovingTarget(float projectileX, float projectileY, float projectileSpeed, float enemyX, float enemyY, float enemyDeltaX, float enemyDeltaY) {
        temp1.set(enemyX, enemyY);
        temp1.sub(projectileX, projectileY);
        
        temp2.set(enemyDeltaX, enemyDeltaY);
        float a = temp2.dot(enemyDeltaX, enemyDeltaY) - projectileSpeed * projectileSpeed;
        temp2.set(enemyDeltaX, enemyDeltaY);
        float b = 2 * temp2.dot(temp1);
        temp2.set(temp1);
        float c = temp2.dot(temp1);
        
        float p = -b / 2 * a;
        float q = (float) Math.sqrt((b * b) - 4 * a * c) / (2 * a);
        
        float t1 = p - q;
        float t2 = p + q;
        float t;
        
        if (t1 > t2 && t2 > 0)
        {
            t = t2;
        }
        else
        {
            t = t1;
        }
        
        temp1.set(enemyX, enemyY);
        temp2.set(enemyDeltaX, enemyDeltaY);
        temp2.scl(t);
        temp1.add(temp2);
        temp1.sub(projectileX, projectileY);
        return temp1.angleDeg();
    }
    
    public static Vector2 positionToHitMovingTarget(float projectileX, float projectileY, float projectileSpeed, float enemyX, float enemyY, float enemyDeltaX, float enemyDeltaY, Vector2 result) {
        temp1.set(enemyX, enemyY);
        temp1.sub(projectileX, projectileY);
        
        temp2.set(enemyDeltaX, enemyDeltaY);
        float a = temp2.dot(enemyDeltaX, enemyDeltaY) - projectileSpeed * projectileSpeed;
        temp2.set(enemyDeltaX, enemyDeltaY);
        float b = 2 * temp2.dot(temp1);
        temp2.set(temp1);
        float c = temp2.dot(temp1);
        
        float p = -b / 2 * a;
        float q = (float) Math.sqrt((b * b) - 4 * a * c) / (2 * a);
        
        float t1 = p - q;
        float t2 = p + q;
        float t;
        
        if (t1 > t2 && t2 > 0)
        {
            t = t2;
        }
        else
        {
            t = t1;
        }
        
        temp1.set(enemyX, enemyY);
        temp2.set(enemyDeltaX, enemyDeltaY);
        temp2.scl(t);
        temp1.add(temp2);
        result.set(temp1);
        return result;
    }
    
    public static float approach(float start, float target, float increment) {
        increment = Math.abs(increment);
        if (start < target) {
            start += increment;
            
            if (start > target) {
                start = target;
            }
        } else {
            start -= increment;
            
            if (start < target) {
                start = target;
            }
        }
        return start;
    }
    
    public static float approach360(float start, float target, float increment) {
        float delta = ((target - start + 360 + 180) % 360) - 180;
        return (start + Math.signum(delta) * MathUtils.clamp(increment, 0.0f, Math.abs(delta)) + 360) % 360;
    }
    
    public static boolean isEqual360(float a, float b, float tolerance) {
        return MathUtils.isZero((a - b + 180 + 360) % 360 - 180, tolerance);
    }
    
    public static boolean isEqual360(float a, float b) {
        return isEqual360(a, b, MathUtils.FLOAT_ROUNDING_ERROR);
    }
    
    public static boolean rayOverlapRectangle(float x, float y, float direction, Rectangle rectangle, Vector3 intersection) {
        rectToBoundingBox(rectangle, bboxTemp);
        
        vector2.set(1,0);
        vector2.rotateDeg(direction);
        
        rayTemp.set(x, y, 0, vector2.x, vector2.y, 0);
        return Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection);
    }
    
    public static boolean rayIntersectRectangle(float x, float y, float direction, Rectangle rectangle, Vector3 intersection) {
        vector2.set(1,0);
        vector2.rotateDeg(direction);
        rayTemp.set(x, y, 0, vector2.x, vector2.y, 0);
        
        rectToBoundingBox(rectangle.x, rectangle.y, 0, rectangle.height, bboxTemp);
        if (Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection)) return true;
        
        rectToBoundingBox(rectangle.x, rectangle.y + rectangle.height, rectangle.width, 0, bboxTemp);
        if (Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection)) return true;
        
        rectToBoundingBox(rectangle.x + rectangle.width, rectangle.y, 0, rectangle.height, bboxTemp);
        if (Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection)) return true;
        
        rectToBoundingBox(rectangle.x, rectangle.y, rectangle.width, 0, bboxTemp);
        if (Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection)) return true;
        
        return false;
    }
    
    public static Rectangle setRectToSkeletonBounds(Rectangle rectangle, SkeletonBounds skeletonBounds) {
        rectangle.x = skeletonBounds.getMinX();
        rectangle.y = skeletonBounds.getMinY();
        rectangle.width = skeletonBounds.getWidth();
        rectangle.height = skeletonBounds.getHeight();
        return rectangle;
    }
    
    public static BoundingBox rectToBoundingBox(Rectangle rectangle, BoundingBox boundingBox) {
        return rectToBoundingBox(rectangle.x, rectangle.y, rectangle.width, rectangle.height, boundingBox);
    }
    
    public static BoundingBox rectToBoundingBox(float x, float y, float width, float height, BoundingBox boundingBox) {
        v3Temp1.set(x, y, 0);
        v3Temp2.set(x + width, y + height, 0);
        boundingBox.set(v3Temp1, v3Temp2);
        return boundingBox;
    }
    
    public static float rectLongestDiagonal(float width, float height) {
        vector2.set(width, height);
        return vector2.len();
    }
    
    public static Polygon rotatedRectangle(float x, float y, float width, float height, float angle, float originX, float originY, Polygon polygon) {
        polygon.setVertices(new float[]{0, 0, width, 0, width, height, 0, height});
        polygon.setOrigin(originX, originY);
        polygon.setRotation(angle);
        polygon.setPosition(x, y);
        return polygon;
    }
    
    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }
    
    public static Bone findBone(Skeleton skeleton, BoneData boneData) {
        if (boneData == null) throw new IllegalArgumentException("boneData cannot be null.");
        Object[] bones = skeleton.getBones().items;
        for (int i = 0, n = skeleton.getBones().size; i < n; i++) {
            Bone bone = (Bone)bones[i];
            if (bone.getData() == boneData) return bone;
        }
        return null;
    }
    
    public static Slot findSlot(Skeleton skeleton, SlotData slotData) {
        if (slotData == null) throw new IllegalArgumentException("slotData cannot be null.");
        Object[] slots = skeleton.getSlots().items;
        for (int i = 0, n = skeleton.getBones().size; i < n; i++) {
            Slot slot = (Slot) slots[i];
            if (slot.getData() == slotData) return slot;
        }
        return null;
    }
    
    public static String intToTwoDigit(int value) {
        return (value < 10 ? "0" : "") + value;
    }
    
    /**
     * not sure this works...
     * @param points
     * @return
     */
    public static boolean isClockwise(float[] points) {
//        var sum = 0;
//        for (int i = 0; i + 3 < points.length; i += 2) {
//            sum += (points[i + 2] - points[i]) * (points[i + 3] - points[i + 1]);
//        }
//        sum += (points[0] - points[points.length - 2]) * (points[1] - points[points.length - 1]);
//        return sum < 0;
    
        var sum = 0;
        for (int i = 0; i < points.length; i += 2) {
            var x1 = points[i];
            var y1 = points[i + 1];
            float x2, y2;
            if (i == points.length - 2) {
                x2 = points[i];
                y2 = points[i + 1];
            } else {
                x2 = points[i + 2];
                y2 = points[i + 3];
            }
    
            sum += (x1 * y2 - x2 * y1);
        }
        return sum / 2 < 0;
    }
    
    private static final Vector2 closest=new Vector2();
    private static final Vector2 bVec1=new Vector2();
    private static final Vector2 bVec2=new Vector2();
    public static Vector2 closestPointInLine(Vector2 point,Vector2 linePoint1,Vector2 linePoint2){
        bVec1.set(linePoint2).sub(linePoint1);
        bVec2.set(point).sub(linePoint1);
        float av=bVec1.x*bVec1.x+bVec1.y*bVec1.y;
        float bv=bVec2.x*bVec1.x+bVec2.y*bVec1.y;
        float t=bv/av;
        if(t<0) t=0;
        if(t>1) t=1f;
        closest.set(linePoint1).add(bVec1.x*t,bVec1.y*t);
        return closest;
    }
    
    private static final Vector2 point = new Vector2();
    private static final Vector2 linePoint1 = new Vector2();
    private static final Vector2 linePoint2 = new Vector2();
    public static Vector2 closestPointInLine(float pointX, float pointY, float x1, float y1, float x2, float y2) {
        point.set(pointX, pointY);
        linePoint1.set(x1, y1);
        linePoint2.set(x2, y2);
        return closestPointInLine(point, linePoint1, linePoint2);
    }
    
    public static float degDifference(float sourceAngle, float targetAngle) {
        var angle = targetAngle - sourceAngle;
        angle = mod((angle + 180), 360) - 180;
        return angle;
    }
    
    private static float mod(float a, float n) {
        return (a % n + n) % n;
    }
    
    public static float throttledAcceleration(float speed, float maxSpeed, float acceleration, boolean maintainExtraMomentum) {
        acceleration *= (1 - speed / maxSpeed);
        if (maintainExtraMomentum && Math.signum(acceleration) != Math.signum(maxSpeed)) {
            acceleration = 0;
        }
        return speed + acceleration;
    }
    
    public static float throttledDeceleration(float speed, float maxSpeed, float minDeceleration, float deceleration) {
        deceleration *= (1 - Math.abs(speed) / maxSpeed);
        if (deceleration < minDeceleration) deceleration = minDeceleration;
        System.out.println("deceleration = " + deceleration);
        return Utils.approach(speed, 0, deceleration);
    }
}