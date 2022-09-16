package com.ray3k.template;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.crashinvaders.vfx.VfxManager;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;
import com.ray3k.template.transitions.*;

import static com.ray3k.template.transitions.Transitions.*;

public abstract class JamGame implements ApplicationListener {
    protected JamScreen screen;
    
    private long previous;
    private long lag;
    
    @Override
    public void create() {
        Core.batch = new TwoColorPolygonBatch(Core.MAX_VERTEX_SIZE);
        
        previous = TimeUtils.millis();
        lag = 0;
        
        Core.assetManager = new AssetManager(new InternalFileHandleResolver());
        Core.shapeRenderer = new ShapeRenderer();
        Core.vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        Core.transitionEngine = new TransitionEngine(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Core.defaultTransition = crossFade();
        Core.defaultTransitionDuration = .5f;
    
        Core.sceneBuilder = new SceneComposerStageBuilder();
        
        loadAssets();
    }
    
    @Override
    public void render() {
        if (screen != null) {
            long current = TimeUtils.millis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
            
            while (lag >= Core.MS_PER_UPDATE) {
                float delta = Core.MS_PER_UPDATE / 1000.0f;
                
                if (!Core.transitionEngine.inTransition) {
                    screen.updateMouse();
                    screen.act(delta);
                    screen.clearStates();
                } else {
                    Core.transitionEngine.update(delta);
                }
                
                lag -= Core.MS_PER_UPDATE;
            }
            
            if (Core.transitionEngine.inTransition) {
                Core.transitionEngine.draw(Core.batch, lag / Core.MS_PER_UPDATE);
            } else {
                ((JamScreen) screen).draw(lag / Core.MS_PER_UPDATE);
            }
        }
    }
    
    @Override
    public void pause() {
        if (screen != null) screen.pause();
    }
    
    @Override
    public void resume() {
        if (screen != null) screen.resume();
    }
    
    @Override
    public void dispose() {
        if (screen != null) screen.hide();
    
        Core.batch.dispose();
        Core.vfxManager.dispose();
        Core.assetManager.dispose();
        Core.transitionEngine.dispose();
        Core.shapeRenderer.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        if (screen != null) screen.resize(width, height);
        
        if (width != 0 && height != 0) Core.transitionEngine.resize(width, height);
    }
    
    public abstract void loadAssets();
    
    public void transition(JamScreen nextScreen, Transition transition, float duration) {
        Core.transitionEngine.transition((JamScreen) getScreen(), nextScreen, transition, duration);
    }
    
    public void transition(JamScreen nextScreen) {
        transition(nextScreen, Core.defaultTransition, Core.defaultTransitionDuration);
    }
    
    public void setScreen (JamScreen screen) {
        if (this.screen != null) this.screen.hide();
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
    
    /** @return the currently active {@link JamScreen}. */
    public JamScreen getScreen () {
        return screen;
    }
}