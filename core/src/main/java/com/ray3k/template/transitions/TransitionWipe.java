package com.ray3k.template.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.ray3k.template.*;

public class TransitionWipe implements Transition {
    private TransitionEngine te;
    private float toDirection;
    private Interpolation interpolation;
    private Polygon polygon;
    private static final Vector2 vector2 = new Vector2();
    
    public TransitionWipe(float toDirection, Interpolation interpolation) {
        this.toDirection = toDirection;
        this.interpolation = interpolation;
        te = Core.transitionEngine;
        polygon = new Polygon();
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
        float distance = Utils.rectLongestDiagonal(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        vector2.set(distance / 2, 0);
        vector2.rotateDeg(toDirection + 180);
        Utils.rotatedRectangle(Gdx.graphics.getWidth() / 2 + vector2.x, Gdx.graphics.getHeight() / 2 + vector2.y - distance / 2, distance * interpolation.apply((te.time + delta) / te.duration), distance, toDirection, 0, distance / 2, polygon);

        te.textureRegion.setRegion(new TextureRegion(te.frameBuffer.getFbo().getColorBufferTexture()));
        te.textureRegion.flip(false, true);
        
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        batch.setColor(1, 1, 1, 1);
        batch.draw(te.textureRegion, 0, 0);
        batch.end();
    
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
        
        Core.shapeRenderer.begin(ShapeType.Filled);
        Core.shapeRenderer.setProjectionMatrix(te.viewport.getCamera().combined);
        float[] points = polygon.getTransformedVertices();
        Core.shapeRenderer.triangle(points[0], points[1], points[2], points[3], points[4], points[5]);
        Core.shapeRenderer.triangle(points[4], points[5], points[6], points[7], points[0], points[1]);
        Core.shapeRenderer.end();
        
        batch.begin();
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
        
        te.textureRegion.setRegion(new TextureRegion(te.nextFrameBuffer.getFbo().getColorBufferTexture()));
        te.textureRegion.flip(false, true);

        batch.draw(te.textureRegion, 0, 0);
    }
    
    @Override
    public void end() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }
}
