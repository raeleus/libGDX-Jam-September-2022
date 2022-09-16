package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static com.ray3k.template.Core.*;

public abstract class JamScreen implements InputProcessor, ControllerListener {
    private static final Vector3 tempVector3 = new Vector3();
    
    public void show() {
        Controllers.addListener(this);
    
        for (Controller controller : Controllers.getControllers()) {
            addControllerHandler(controller);
        }
    }
    
    public void hide() {
        Controllers.removeListener(this);
        clearControllerHandlers();
    }
    
    public void updateMouse() {
        if (viewport != null) {
            tempVector3.x = Gdx.input.getX();
            tempVector3.y = Gdx.input.getY();
            viewport.unproject(tempVector3);
            mouseX = tempVector3.x;
            mouseY = tempVector3.y;
        } else {
            mouseX = Gdx.input.getX();
            mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        }
    }
    
    public void clearStates() {
        keysJustPressed.clear();
        buttonsJustPressed.clear();
        scrollJustPressed.clear();
        for (ControllerHandler handler : controllerMap.values()) {
            handler.controllerButtonsJustPressed.clear();
            handler.controllerAxisJustPressed.clear();
        }
    }
    
    public abstract void act(float delta);
    
    public abstract void draw(float delta);
    
    public abstract void pause();
    
    public abstract void resume();
    
    public abstract void resize(int width, int height);
    
    public abstract void dispose();
    
    @Override
    public boolean keyDown(int keycode) {
        keysJustPressed.add(keycode);
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        buttonsJustPressed.add(button);
        buttonsPressed.add(button);
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttonsPressed.removeValue(button);
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollJustPressed.add((int) amountY);
        return false;
    }
    
    @Override
    public void connected(Controller controller) {
        addControllerHandler(controller);
    }
    
    private void addControllerHandler(Controller controller) {
        ControllerHandler controllerHandler = new ControllerHandler();
        controller.addListener(controllerHandler);
        controllerMap.put(controller, controllerHandler);
    
        //fix null controllers in bindings loaded from preferences
        for (var binding : bindings) {
            if (hasControllerButtonBinding(binding)) {
                var controllerValue = getControllerButtonBinding(binding);
                var key = "controllerbutton:" + binding.toString();
                if (preferences.contains(key)) {
                    String[] line = preferences.getString(key).split(" ");
                    var controllerIndex = Integer.parseInt(line[0]);
                    if (controllerIndex < Controllers.getControllers().size) {
                        controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                    }
                }
            } else if (hasControllerAxisBinding(binding)) {
                var controllerValue = getControllerAxisBinding(binding);
                var key = "controlleraxis:" + binding.toString();
                if (preferences.contains(key)) {
                    String[] line = preferences.getString(key).split(" ");
                    var controllerIndex = Integer.parseInt(line[0]);
                    if (controllerIndex < Controllers.getControllers().size) {
                        controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                    }
                }
            }
        }
    }
    
    private void clearControllerHandlers() {
        controllerMap.clear();
    }
    
    @Override
    public void disconnected(Controller controller) {
        controllerMap.remove(controller);
    }
    
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        for (ControllerValue controllerValue : controllerButtonBindings.values()) {
            if (controllerValue.controller == null && buttonCode == controllerValue.value) {
                controllerValue.controller = controller;
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }
    
    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        for (ControllerValue controllerValue : controllerAxisBindings.values()) {
            if (controllerValue.controller == null && axisCode == controllerValue.axisCode && MathUtils.round(value) == controllerValue.value) {
                controllerValue.controller = controller;
                break;
            }
        }
        return false;
    }
    
}