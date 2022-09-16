package com.ray3k.template.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ray3k.template.*;

public class TransitionPush implements Transition {
    private TransitionEngine te;
    private float toDirection;
    private Color backgroundColor;
    private final Vector2 screenPosition = new Vector2();
    private final Vector2 nextScreenPosition = new Vector2();
    private static final Vector3 temp3 = new Vector3();
    private static Rectangle rectangle = new Rectangle();
    private Interpolation interpolation;
    
    public TransitionPush(float toDirection, Color backgroundColor, Interpolation interpolation) {
        this.toDirection = toDirection;
        this.backgroundColor = backgroundColor;
        this.interpolation = interpolation;
        te = Core.transitionEngine;
    }
    
    @Override
    public void create() {
        te.frameBuffer.begin();
        te.screen.draw(0);
        te.frameBuffer.end();
    
        te.jamGame.setScreen(te.nextScreen);
        te.nextFrameBuffer.begin();
        te.nextScreen.act(0);
        te.nextScreen.draw(0);
        te.nextFrameBuffer.end();
    }
    
    @Override
    public void resize(int width, int height) {
        create();
    }
    
    @Override
    public void act() {
    
    }
    
    @Override
    public void draw(Batch batch, float delta) {
        rectangle.set(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Utils.rayIntersectRectangle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, toDirection, rectangle, temp3);

        float distance = Utils.pointDistance(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, temp3.x, temp3.y) * 2;
        
        screenPosition.set(distance * interpolation.apply((te.time + delta) / te.duration), 0);
        screenPosition.rotateDeg(toDirection);

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        te.textureRegion.setRegion(new TextureRegion(te.frameBuffer.getFbo().getColorBufferTexture()));
        te.textureRegion.flip(false, true);

        batch.setColor(1, 1, 1, 1);
        batch.draw(te.textureRegion, screenPosition.x, screenPosition.y);

        nextScreenPosition.set(distance * interpolation.apply((te.time + delta) / te.duration ) - distance, 0);
        nextScreenPosition.rotateDeg(toDirection);

        te.textureRegion.setRegion(new TextureRegion(te.nextFrameBuffer.getFbo().getColorBufferTexture()));
        te.textureRegion.flip(false, true);

        batch.draw(te.textureRegion, nextScreenPosition.x, nextScreenPosition.y);
    }
    
    @Override
    public void end() {
    
    }
}
