package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.*;
import com.ray3k.template.OgmoReader.*;
import com.ray3k.template.entities.*;
import com.ray3k.template.screens.DialogDebug.*;
import com.ray3k.template.screens.DialogPause.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.ray3k.template.Core.*;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    public Stage stage;
    public boolean paused;
    private Label fpsLabel;
    
    @Override
    public void show() {
        super.show();
    
        gameScreen = this;
        BG_COLOR.set(Color.BLACK);
    
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
    
        debugShapeDrawer = new Box2DDebugShapeDrawer(shapeDrawer);
    
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    
        camera = new OrthographicCamera();
        camera.zoom = 2;
        viewport = new FitViewport(1024, 576, camera);
    
        entityController.clear();
        world.dispose();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(worldContactListener);

        var ogmo = new OgmoReader();
        ogmo.addListener(new GameOgmoAdapter());
        ogmo.readFile(Gdx.files.internal("levels/test-level.json"));
    }
    
    @Override
    public void act(float delta) {
        if (!paused) {
            entityController.act(delta);
            vfxManager.update(delta);
        }
        stage.act(delta);
        
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
        entityController.draw(paused ? 0 : delta);
        shapeDrawer.setDefaultLineWidth(2f);
        debugShapeDrawer.render(world);
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
    }
    
    @Override
    public void hide() {
        super.hide();
        vfxManager.removeAllEffects();
        entityController.dispose();
    }
    
    @Override
    public void pause() {
    
    }
    
    @Override
    public void resume() {
    
    }
    
    private static class GameOgmoAdapter extends OgmoAdapter {
        @Override
        public void level(String ogmoVersion, int width, int height, int offsetX, int offsetY,
                          ObjectMap<String, OgmoValue> valuesMap) {
            
        }
    
        @Override
        public void layer(String name, int gridCellWidth, int gridCellHeight, int offsetX, int offsetY) {
        
        }
    
        @Override
        public void entity(String name, int id, int x, int y, int width, int height, boolean flippedX, boolean flippedY,
                           int originX, int originY, int rotation, Array<EntityNode> nodes,
                           ObjectMap<String, OgmoValue> valuesMap) {
            
            switch (name) {
                case "player":
                    var player = new Player();
                    entityController.add(player);
                    player.teleport(x, y);
                    break;
                case "platform-stone":
                    var platformStone = new Block();
                    entityController.add(platformStone);
                    platformStone.teleport(x, y);
                    break;
                case "bounds":
                    float[] points = new float[(nodes.size + 1) * 2];
                    for (int i = 0; i < nodes.size; i++) {
                        var node = nodes.get(i);
                        points[i*2] = p2m(node.x);
                        points[i*2 + 1] = p2m(node.y);
                    }
                    points[points.length - 2] = p2m(x);
                    points[points.length - 1] = p2m(y);
                    var bounds = new Bounds(points);
                    entityController.add(bounds);
                    bounds.teleport(0, 0);
                    break;
            }
        }
    
        @Override
        public void grid(int col, int row, int x, int y, int width, int height, int id) {
        
        }
    
        @Override
        public void decal(int x, int y, float originX, float originY, float scaleX, float scaleY, int rotation,
                          String texture, String folder, ObjectMap<String, OgmoValue> valuesMap) {
            
        }
    
        @Override
        public void layerComplete() {
        
        }
    
        @Override
        public void levelComplete() {
        
        }
    }
}
