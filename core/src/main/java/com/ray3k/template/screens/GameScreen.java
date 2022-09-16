package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.ray3k.template.*;
import com.ray3k.template.entities.*;
import com.ray3k.template.screens.DialogDebug.*;
import com.ray3k.template.screens.DialogPause.*;
import com.ray3k.template.vfx.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.ray3k.template.Core.*;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    public Stage stage;
    public boolean paused;
    private ChainVfxEffect vfxEffect;
    private Label fpsLabel;
    
    @Override
    public void show() {
        super.show();
    
        gameScreen = this;
        vfxEffect = new GlitchEffect();
        vfxManager.addEffect(vfxEffect);
        BG_COLOR.set(Color.PINK);
    
        paused = false;
    
        stage = new Stage(new ScreenViewport(), batch);
        
        var root = new Table();
        root.setFillParent(true);
        root.align(Align.bottomLeft);
        root.pad(10);
        stage.addActor(root);
        
        fpsLabel = new Label("test", skin);
        root.add(fpsLabel);
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (!paused && keycode == Keys.ESCAPE) {
                    paused = true;
                
                    DialogPause dialogPause = new DialogPause(GameScreen.this);
                    dialogPause.show(stage);
                    dialogPause.addListener(new PauseListener() {
                        @Override
                        public void resume() {
                            paused = false;
                        }
                    
                        @Override
                        public void quit() {
                            core.transition(new MenuScreen());
                        }
                    });
                }
                return super.keyDown(event, keycode);
            }
        });
    
        stage.addListener(new DebugListener());
    
        shapeDrawer = new ShapeDrawer(batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
    
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    
        camera = new OrthographicCamera();
        viewport = new FitViewport(1024, 576, camera);
    
        entityController.clear();
        BallTestEntity ballTestEntity = new BallTestEntity();
        ballTestEntity.moveCamera = true;
        entityController.add(ballTestEntity);
    
        for (int i = 0; i < 10; i++) {
            ballTestEntity = new BallTestEntity();
            ballTestEntity.setPosition(MathUtils.random(viewport.getWorldWidth()), MathUtils.random(viewport.getWorldHeight()));
            entityController.add(ballTestEntity);
        }
    }
    
    @Override
    public void act(float delta) {
        if (!paused) {
            entityController.act(delta);
            vfxManager.update(delta);
        }
        stage.act(delta);
    
        if (isBindingJustPressed(Binding.LEFT)) {
            System.out.println("left");
        }
        if (isBindingJustPressed(Binding.UP)) {
            System.out.println("up");
        } else if (isBindingJustPressed(Binding.RIGHT)) {
            System.out.println("right");
        } else if (isBindingJustPressed(Binding.DOWN)) {
            System.out.println("down");
        }
        
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond());
    }
    
    @Override
    public void draw(float delta) {
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(Color.WHITE);
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shapeDrawer.setColor(isBindingPressed(Binding.LEFT) && isBindingPressed(Binding.UP) ? Color.ORANGE : Color.GREEN);
        shapeDrawer.filledRectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(10);
        shapeDrawer.rectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        entityController.draw(paused ? 0 : delta);
        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
    
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        if (width + height != 0) {
            vfxManager.resize(width, height);
            viewport.update(width, height);
        
            stage.getViewport().update(width, height, true);
        }
    }
    
    @Override
    public void dispose() {
        vfxEffect.dispose();
    }
    
    @Override
    public void hide() {
        super.hide();
        vfxManager.removeAllEffects();
        vfxEffect.dispose();
        entityController.dispose();
    }
    
    @Override
    public void pause() {
    
    }
    
    @Override
    public void resume() {
    
    }
}
