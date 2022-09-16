package com.ray3k.template.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ray3k.template.*;

public class TransitionInstant implements Transition {
    private TransitionEngine te;
    
    public TransitionInstant() {
        te = Core.transitionEngine;
    }
    
    @Override
    public void create() {
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        te.textureRegion.setRegion(new TextureRegion(te.nextFrameBuffer.getFbo().getColorBufferTexture()));
        te.textureRegion.flip(false, true);
    
        batch.setColor(1, 1, 1, 1f);
        batch.draw(te.textureRegion, 0, 0);
    }
    
    @Override
    public void end() {
    
    }
}
