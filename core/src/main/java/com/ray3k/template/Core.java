package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.stripe.FreeTypeSkinLoader;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;
import com.ray3k.template.AnimationStateDataLoader.*;
import com.ray3k.template.entities.*;
import com.ray3k.template.screens.*;
import com.ray3k.template.transitions.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Iterator;
import java.util.Objects;

import static com.ray3k.template.Resources.*;

public class Core extends JamGame {
    public static final String PROJECT_NAME = "Player's Island";
    public final static IntArray keysJustPressed = new IntArray();
    public final static IntArray buttonsJustPressed = new IntArray();
    public final static IntArray buttonsPressed = new IntArray();
    public final static IntArray scrollJustPressed = new IntArray();
    public final static ObjectIntMap<Binding> keyBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Binding> buttonBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Binding> scrollBindings = new ObjectIntMap<>();
    public final static ObjectMap<Binding, ControllerValue> controllerButtonBindings = new ObjectMap<>();
    public final static ObjectMap<Binding, ControllerValue> controllerAxisBindings = new ObjectMap<>();
    public final static ObjectSet<Binding> unboundBindings = new ObjectSet<>();
    public final static Array<Binding> bindings = new Array<>();
    public final static int ANY_BUTTON = -1;
    public final static int SCROLL_UP = -1;
    public final static int SCROLL_DOWN = 1;
    public final static int ANY_SCROLL = 0;
    public final static ControllerValue ANY_CONTROLLER_BUTTON = new ControllerValue(null, -1, 0);
    public final static ControllerValue ANY_CONTROLLER_AXIS = new ControllerValue(null, -1, 0);
    public final static ObjectMap<Controller, ControllerHandler> controllerMap = new ObjectMap<>();
    final static long MS_PER_UPDATE = 10;
    static final int MAX_VERTEX_SIZE = 32767;
    public static Core core;
    public static Skin skin;
    public static SkeletonRenderer skeletonRenderer;
    public static ChangeListener sndChangeListener;
    public static EntityController entityController;
    public static World world;
    public final static float PPM = 100f;
    public static Box2DDebugShapeDrawer debugShapeDrawer;
    public static CrossPlatformWorker crossPlatformWorker;
    public static float mouseX;
    public static float mouseY;
    public static Viewport viewport;
    public static OrthographicCamera camera;
    public static AssetManager assetManager;
    public static TransitionEngine transitionEngine;
    public static TwoColorPolygonBatch batch;
    public static ShapeRenderer shapeRenderer;
    public static VfxManager vfxManager;
    public static SceneComposerStageBuilder sceneBuilder;
    public static ShapeDrawer shapeDrawer;
    public static Transition defaultTransition;
    public static float defaultTransitionDuration;
    public static float DEPTH_PLAYER = 20;
    public static float DEPTH_PARTICLES = 10;
    public static float DEPTH_PARTICLES_BEHIND = 100;
    
    public static boolean isKeyJustPressed(int key) {
        return key == Keys.ANY_KEY ? keysJustPressed.size > 0 : keysJustPressed.contains(key);
    }
    
    public static boolean isKeyJustPressed(int... keys) {
        for (int key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if the associated mouse button has been pressed since the last step.
     *
     * @param button The button value or -1 for any button
     * @return
     */
    public static boolean isButtonJustPressed(int button) {
        return button == ANY_BUTTON ? buttonsJustPressed.size > 0 : buttonsJustPressed.contains(button);
    }
    
    public static boolean isButtonJustPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonJustPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isScrollJustPressed(int scroll) {
        return scroll == ANY_SCROLL ? scrollJustPressed.size > 0 : scrollJustPressed.contains(scroll);
    }
    
    public static boolean isScrollJustPressed(int... scrolls) {
        for (int scroll : scrolls) {
            if (isScrollJustPressed(scroll)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
    
    public static boolean isAnyKeyPressed(int... keys) {
        for (int key : keys) {
            if (isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyKeyPressed() {
        return isKeyPressed(Keys.ANY_KEY);
    }
    
    public static boolean isAnyKeyJustPressed(int... keys) {
        for (int key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyKeyJustPressed() {
        return keysJustPressed.size > 0;
    }
    
    public static boolean areAllKeysPressed(int... keys) {
        for (int key : keys) {
            if (!isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isControllerButtonJustPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerButtonJustPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isControllerAxisJustPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerAxisJustPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isControllerButtonPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerButtonPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isControllerAxisPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerAxisPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyControllerButtonPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerButtonPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyControllerButtonJustPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerButtonJustPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyControllerAxisPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerAxisPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyControllerAxisJustPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerAxisJustPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean areAllControllerButtonsPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.areAllControllerButtonsPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean areAllControllerAxisPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.areAllControllerAxisPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isButtonPressed(int button) {
        if (button == ANY_BUTTON) {
            return buttonsPressed.contains(Input.Buttons.LEFT) || buttonsPressed.contains(Input.Buttons.RIGHT)
                    || buttonsPressed.contains(Input.Buttons.MIDDLE) || buttonsPressed.contains(Input.Buttons.BACK)
                    || buttonsPressed.contains(Input.Buttons.FORWARD);
        } else {
            return buttonsPressed.contains(button);
        }
    }
    
    public static boolean isAnyButtonPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyButtonPressed() {
        return buttonsPressed.size > 0;
    }
    
    public static boolean isAnyButtonJustPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonJustPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyButtonJustPressed() {
        return buttonsJustPressed.size > 0;
    }
    
    public static boolean areAllButtonsPressed(int... buttons) {
        for (int button : buttons) {
            if (!isButtonPressed(button)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBindingPressed(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyPressed(keyBindings.get(binding, Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonPressed(buttonBindings.get(binding, ANY_BUTTON));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return isControllerButtonPressed(controllerButtonBindings.get(binding));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return isControllerAxisPressed(controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS));
        } else {
            return false;
        }
    }
    
    public static boolean isAnyBindingJustPressed() {
        for (Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyBindingJustPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyBindingPressed() {
        for (Binding binding : bindings) {
            if (isBindingPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAnyBindingPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (isBindingPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean areAllBindingsPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (!isBindingPressed(binding)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBindingJustPressed(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyJustPressed(keyBindings.get(binding, Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonJustPressed(buttonBindings.get(binding, ANY_BUTTON));
        } else if (scrollBindings.containsKey(binding)) {
            return isScrollJustPressed(scrollBindings.get(binding, ANY_SCROLL));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return isControllerButtonJustPressed(controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return isControllerAxisJustPressed(controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS));
        } else {
            return false;
        }
    }
    
    public static boolean isBindingJustPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static void clearBindings() {
        keyBindings.clear();
        buttonBindings.clear();
        scrollBindings.clear();
        controllerButtonBindings.clear();
        controllerAxisBindings.clear();
        unboundBindings.clear();
        bindings.clear();
    }
    
    public static void addKeyBinding(Binding binding, int key) {
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        unboundBindings.remove(binding);
        keyBindings.put(binding, key);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addButtonBinding(Binding binding, int button) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        unboundBindings.remove(binding);
        buttonBindings.put(binding, button);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addScrollBinding(Binding binding, int scroll) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        unboundBindings.remove(binding);
        scrollBindings.put(binding, scroll);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerButtonBinding(Binding binding, ControllerValue controllerValue) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerButtonBindings.put(binding, controllerValue);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerAxisBinding(Binding binding, ControllerValue controllerValue) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.put(binding, controllerValue);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addUnboundBinding(Binding binding) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        unboundBindings.add(binding);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void removeBinding(Binding binding) {
        keyBindings.remove(binding, Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        bindings.removeValue(binding, true);
    }
    
    public static boolean hasBinding(Binding binding) {
        return bindings.contains(binding, true);
    }
    
    public static boolean hasKeyBinding(Binding binding) {
        return keyBindings.containsKey(binding);
    }
    
    public static boolean hasButtonBinding(Binding binding) {
        return buttonBindings.containsKey(binding);
    }
    
    public static boolean hasScrollBinding(Binding binding) {
        return scrollBindings.containsKey(binding);
    }
    
    public static boolean hasControllerButtonBinding(Binding binding) {
        return controllerButtonBindings.containsKey(binding);
    }
    
    public static boolean hasControllerAxisBinding(Binding binding) {
        return controllerAxisBindings.containsKey(binding);
    }
    
    public static boolean hasUnboundBinding(Binding binding) {
        return unboundBindings.contains(binding);
    }
    
    public static Array<Binding> getBindings() {
        return bindings;
    }
    
    public static int getKeyBinding(Binding binding) {
        return keyBindings.get(binding, Keys.ANY_KEY);
    }
    
    public static int getButtonBinding(Binding binding) {
        return buttonBindings.get(binding, ANY_BUTTON);
    }
    
    public static int getScrollBinding(Binding binding) {
        return scrollBindings.get(binding, ANY_SCROLL);
    }
    
    public static ControllerValue getControllerButtonBinding(Binding binding) {
        return controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON);
    }
    
    public static ControllerValue getControllerAxisBinding(Binding binding) {
        return controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS);
    }
    
    public static String getBindingCodeName(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return Keys.toString(getKeyBinding(binding));
        } else if (buttonBindings.containsKey(binding)) {
            return Utils.mouseButtonToString(getButtonBinding(binding));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return Utils.controllerButtonToString(getControllerButtonBinding(binding));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return Utils.controllerAxisToString(getControllerAxisBinding(binding));
        } else if (scrollBindings.containsKey(binding)) {
            return Utils.scrollAmountToString(getScrollBinding(binding));
        } else {
            return "UNBOUND";
        }
    }
    
    public static void saveBindings() {
        for (Entry<Binding> binding : keyBindings) {
            preferences.putInteger("key:" + binding.key.toString(), binding.value);
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : buttonBindings) {
            preferences.putInteger("button:" + binding.key.toString(), binding.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : scrollBindings) {
            preferences.putInteger("scroll:" + binding.key.toString(), binding.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerButtonBindings) {
            preferences.putString("controllerbutton:" + binding.key.toString(), Controllers.getControllers().indexOf(binding.value.controller, true) + " " + binding.value.axisCode + " " + binding.value.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerAxisBindings) {
            preferences.putString("controlleraxis:" + binding.key.toString(), Controllers.getControllers().indexOf(binding.value.controller, true) + " " + binding.value.axisCode + " " + binding.value.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Binding binding : unboundBindings) {
            preferences.putBoolean("unbound:" + binding.toString(), true);
            preferences.remove("key:" + binding.toString());
            preferences.remove("button:" + binding.toString());
            preferences.remove("scroll:" + binding.toString());
            preferences.remove("controllerbutton:" + binding.toString());
            preferences.remove("controlleraxis:" + binding.toString());
        }
        preferences.flush();
    }
    
    public static void loadBindings() {
        for (Binding binding : bindings) {
            String key = "key:" + binding.toString();
            if (preferences.contains(key)) {
                addKeyBinding(binding, preferences.getInteger(key));
            }
            
            key = "button:" + binding.toString();
            if (preferences.contains(key)) {
                addButtonBinding(binding, preferences.getInteger(key));
            }
            
            key = "scroll:" + binding.toString();
            if (preferences.contains(key)) {
                addScrollBinding(binding, preferences.getInteger(key));
            }
            
            key = "controllerbutton:" + binding.toString();
            if (preferences.contains(key)) {
                ControllerValue controllerValue = new ControllerValue();
                String[] line = preferences.getString(key).split(" ");
                var controllerIndex = Integer.parseInt(line[0]);
                if (controllerIndex < Controllers.getControllers().size) {
                    controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                }
                controllerValue.axisCode = Integer.parseInt(line[1]);
                controllerValue.value = Integer.parseInt(line[2]);
                addControllerButtonBinding(binding, controllerValue);
            }
            
            key = "controlleraxis:" + binding.toString();
            if (preferences.contains(key)) {
                ControllerValue controllerValue = new ControllerValue();
                String[] line = preferences.getString(key).split(" ");
                var controllerIndex = Integer.parseInt(line[0]);
                if (controllerIndex < Controllers.getControllers().size) {
                    controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                }
                controllerValue.axisCode = Integer.parseInt(line[1]);
                controllerValue.value = Integer.parseInt(line[2]);
                addControllerAxisBinding(binding, controllerValue);
            }
            
            key = "unbound:" + binding.toString();
            if (preferences.contains(key)) {
                addUnboundBinding(binding);
            }
        }
    }
    
    public enum Binding {
        LEFT, RIGHT, UP, DOWN, JUMP, LICK, SHOOT;
    }
    public static float bgm;
    public static float sfx;
    public static Preferences preferences;
    
    @Override
    public void create() {
        super.create();
        core = this;
        
        preferences = Gdx.app.getPreferences(PROJECT_NAME);
        
        bgm = preferences.getFloat("bgm", 1.0f);
        sfx = preferences.getFloat("sfx", 1.0f);
        
        setDefaultBindings();
        loadBindings();
        
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        entityController = new EntityController();
    
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();
            
                if (entityA != null) entityA.beginContact(entityB, contact.getFixtureA(), contact);
                if (entityB != null) entityB.beginContact(entityA, contact.getFixtureA(), contact);
            }
        
            @Override
            public void endContact(Contact contact) {
                Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();
            
                if (entityA != null) entityA.endContact(entityB, contact.getFixtureA(), contact);
                if (entityB != null) entityB.endContact(entityA, contact.getFixtureA(), contact);
            }
        
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();
            
                if (entityA != null) entityA.preSolve(entityB, contact.getFixtureA(), contact);
                if (entityB != null) entityB.preSolve(entityA, contact.getFixtureA(), contact);
            }
        
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();
            
                if (entityA != null) entityA.postSolve(entityB, contact.getFixtureA(), contact);
                if (entityB != null) entityB.postSolve(entityA, contact.getFixtureA(), contact);
            }
        });
        
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfx_click.play(sfx);
            }
        };
        
        setScreen(new LoadScreen(() -> {
            loadResources(assetManager);
            skin = skin_skin;
        }));
        defaultTransition = Transitions.colorFade(Color.BLACK);
        defaultTransitionDuration = .5f;
    }
    
    public static float p2m(float pixels) {
        return pixels / PPM;
    }
    
    public static float m2p(float meters) {
        return meters * PPM;
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void loadAssets() {
        assetManager.setLoader(Skin.class, new FreeTypeSkinLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(AnimationStateData.class, new AnimationStateDataLoader(assetManager.getFileHandleResolver()));
        
        String textureAtlasPath = null;
        var fileHandle = Gdx.files.internal("textures.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString("UTF-8").split("\\n")) {
            assetManager.load(path, TextureAtlas.class);
            textureAtlasPath = path;
        }
        
        fileHandle = Gdx.files.internal("skin.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString("UTF-8").split("\\n")) {
            assetManager.load(path, Skin.class, new SkinParameter(textureAtlasPath));
        }
    
        fileHandle = Gdx.files.internal("bgm.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString("UTF-8").split("\\n")) {
            assetManager.load(path, Music.class);
        }
    
        fileHandle = Gdx.files.internal("sfx.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString("UTF-8" ).split("\\n")) {
            assetManager.load(path, Sound.class);
        }
        
        fileHandle = Gdx.files.internal("spine.txt");
        if (fileHandle.exists()) for (String path2 : fileHandle.readString("UTF-8").split("\\n")) {
            assetManager.load(path2 + "-animation", AnimationStateData.class, new AnimationStateDataParameter(path2, textureAtlasPath));
        }
    }
    
    public void setDefaultBindings() {
        addKeyBinding(Binding.LEFT, Keys.LEFT);
        addKeyBinding(Binding.RIGHT, Keys.RIGHT);
        addKeyBinding(Binding.UP, Keys.UP);
        addKeyBinding(Binding.DOWN, Keys.DOWN);
        addKeyBinding(Binding.SHOOT, Keys.C);
        addKeyBinding(Binding.LICK, Keys.X);
        addKeyBinding(Binding.JUMP, Keys.Z);
    }
    
    public static class ControllerHandler implements ControllerListener {
        public Array<ControllerValue> controllerButtonsJustPressed = new Array<>();
        public Array<ControllerValue> controllerButtonsPressed = new Array<>();
        public Array<ControllerValue> controllerAxisJustPressed = new Array<>();
        public Array<ControllerValue> controllerAxisPressed = new SnapshotArray<>();
    
        //button
    
        public boolean isControllerButtonJustPressed(ControllerValue buttonCode) {
            return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsJustPressed.size > 0 : controllerButtonsJustPressed.contains(
                    buttonCode, false);
        }
    
        public boolean isControllerButtonJustPressed(ControllerValue... controllerButtons) {
            for (ControllerValue controllerButton : controllerButtons) {
                if (isControllerButtonJustPressed(controllerButton)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean isAnyControllerButtonJustPressed() {
            return controllerButtonsJustPressed.size > 0;
        }
    
        public boolean isControllerButtonPressed(ControllerValue buttonCode) {
            return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsPressed.size > 0 : controllerButtonsPressed.contains(
                    buttonCode, false);
        }
    
        public boolean isControllerButtonPressed(ControllerValue... buttonCodes) {
            for (ControllerValue controllerButton : buttonCodes) {
                if (isControllerButtonPressed(controllerButton)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean areAllControllerButtonsPressed(ControllerValue... buttonCodes) {
            for (ControllerValue buttonCode : buttonCodes) {
                if (!isControllerButtonPressed(buttonCode)) {
                    return false;
                }
            }
            return true;
        }
    
        public boolean isAnyControllerButtonPressed() {
            return controllerButtonsPressed.size > 0;
        }
    
        //axis
    
        public boolean isControllerAxisJustPressed(ControllerValue axisCode) {
            return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisJustPressed.size > 0 : controllerAxisJustPressed.contains(
                    axisCode, false);
        }
    
        public boolean isControllerAxisJustPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (isControllerAxisJustPressed(axisCode)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean isAnyControllerAxisJustPressed() {
            return controllerButtonsJustPressed.size > 0;
        }
    
        public boolean isControllerAxisPressed(ControllerValue axisCode) {
            return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisPressed.size > 0 : controllerAxisPressed.contains(
                    axisCode, false);
        }
    
        public boolean isControllerAxisPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (isControllerAxisPressed(axisCode)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean areAllControllerAxisPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (!isControllerAxisPressed(axisCode)) {
                    return false;
                }
            }
            return true;
        }
    
        public boolean isAnyControllerAxisPressed() {
            return controllerAxisPressed.size > 0;
        }
    
        @Override
        public void connected(Controller controller) {
        
        }
    
        @Override
        public void disconnected(Controller controller) {
    
        }
    
        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            ControllerValue controllerValue = new ControllerValue(controller, 0, buttonCode);
            controllerButtonsJustPressed.add(controllerValue);
            controllerButtonsPressed.add(controllerValue);
            return false;
        }
    
        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            ControllerValue controllerValue = new ControllerValue(controller, 0, buttonCode);
            controllerButtonsPressed.removeValue(controllerValue, false);
            return false;
        }
    
        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            int roundedValue = MathUtils.round(value);
    
            ControllerValue controllerValue = new ControllerValue(controller, axisCode, roundedValue);
            if (roundedValue != 0) {
                controllerAxisJustPressed.add(controllerValue);
            }
    
            Iterator<ControllerValue> iterator = controllerAxisPressed.iterator();
            while (iterator.hasNext()) {
                ControllerValue next = iterator.next();
                if (next.axisCode == axisCode) iterator.remove();
            }
    
            if (roundedValue != 0) {
                controllerAxisPressed.add(controllerValue);
            }
            return false;
        }
    }
    
    public static class ControllerValue {
        public Controller controller;
        public int axisCode;
        public int value;
    
        public ControllerValue() {
        }
    
        public ControllerValue(Controller controller, int axisCode, int value) {
            this.controller = controller;
            this.axisCode = axisCode;
            this.value = value;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ControllerValue that = (ControllerValue) o;
            return axisCode == that.axisCode &&
                    value == that.value &&
                    Objects.equals(that.controller, controller);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(axisCode, value);
        }
    }
}
