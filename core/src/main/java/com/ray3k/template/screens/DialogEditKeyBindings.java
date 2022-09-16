package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;

import static com.ray3k.template.Core.*;

public class DialogEditKeyBindings extends Dialog {
    private Array<Actor> focusables;
    private InputListener keysListener;
    private InputListener mouseEnterListener;
    
    public DialogEditKeyBindings(Stage stage) {
        super("", skin);
        setStage(stage);
    
        focusables = new Array<>();
    
        keysListener = new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                boolean shifting = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
                switch (keycode) {
                    case Keys.TAB:
                        if (shifting) {
                            previous();
                        } else {
                            next();
                        }
                        break;
                    case Keys.RIGHT:
                    case Keys.D:
                    case Keys.DOWN:
                    case Keys.S:
                        next();
                        break;
                    case Keys.LEFT:
                    case Keys.A:
                    case Keys.UP:
                    case Keys.W:
                        previous();
                        break;
                    case Keys.SPACE:
                    case Keys.ENTER:
                        activate();
                }
                return super.keyDown(event, keycode);
            }
        
            public void next() {
                Actor focused = getStage().getKeyboardFocus();
                if (focused == null) {
                    getStage().setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focused, true) + 1;
                    if (index >= focusables.size) index = 0;
                    getStage().setKeyboardFocus(focusables.get(index));
                }
            }
        
            public void previous() {
                Actor focused = getStage().getKeyboardFocus();
                if (focused == null) {
                    getStage().setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focused, true) - 1;
                    if (index < 0) index = focusables.size - 1;
                    getStage().setKeyboardFocus(focusables.get(index));
                }
            }
        
            public void activate() {
                Actor focused = getStage().getKeyboardFocus();
                if (focused != null) {
                    focused.fire(new ChangeEvent());
                } else {
                    getStage().setKeyboardFocus(focusables.first());
                }
            }
        };
    
        getStage().addListener(keysListener);
    
        mouseEnterListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setKeyboardFocus(null);
            }
        };
        
        setFillParent(true);
        Table root = getContentTable();
    
        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setName("scroll");
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).grow();
        
        refreshTable(table);
        
        getButtonTable().pad(5);
        getButtonTable().defaults().uniform().fill().space(10);
        TextButton textButton = new TextButton("OK", skin);
        button(textButton);
        focusables.add(textButton);
        textButton.addListener(sndChangeListener);
        textButton.addListener(mouseEnterListener);
        
        textButton = new TextButton("Defaults", skin);
        getButtonTable().add(textButton);
        focusables.add(textButton);
        textButton.addListener(sndChangeListener);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.setDefaultBindings();
                saveBindings();
                refreshTable(table);
            }
        });
    
    }
    
    private void refreshTable(Table table) {
        table.clear();
        focusables.clear();
        if (getStage() != null) getStage().setKeyboardFocus(null);
        
        table.defaults().space(10).uniform().fill();
        for (Binding binding : getBindings()) {
            String codeName = getBindingCodeName(binding);
            
            TextButton textButton = new TextButton(binding.toString() + " : " + codeName, skin);
            table.add(textButton);
            table.row();
            focusables.add(textButton);
            textButton.addListener(sndChangeListener);
            textButton.addListener(mouseEnterListener);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    DialogKeyBinding dialog = new DialogKeyBinding(binding);
                    dialog.addListener(new BindingListener() {
                        @Override
                        public void keySelected(int key) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : keyBindings) {
                                if (binding.value == key) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                addUnboundBinding(binding);
                            }
                            
                            addKeyBinding(binding, key);
                            saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void buttonSelected(int button) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : buttonBindings) {
                                if (binding.value == button) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                addUnboundBinding(binding);
                            }
                            
                            addButtonBinding(binding, button);
                            saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void scrollSelected(int scroll) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : scrollBindings) {
                                if (binding.value == scroll) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                addUnboundBinding(binding);
                            }
                            
                            addScrollBinding(binding, scroll);
                            saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void controllerButtonSelected(Controller controller, int value) {
                            Array<Binding> unbinds = new Array<>();
                            for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerButtonBindings) {
                                int bindingControllerIndex = Controllers.getControllers().indexOf(binding.value.controller, true);
                                int controllerIndex = Controllers.getControllers().indexOf(controller, true);
                                
                                if (binding.value.value == value && controllerIndex == bindingControllerIndex) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                addUnboundBinding(binding);
                            }
    
                            addControllerButtonBinding(binding, new ControllerValue(controller, 0, value));
                            saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void controllerAxisSelected(Controller controller, int axisCode, int value) {
                            Array<Binding> unbinds = new Array<>();
                            for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerAxisBindings) {
                                int bindingControllerIndex = Controllers.getControllers().indexOf(binding.value.controller, true);
                                int controllerIndex = Controllers.getControllers().indexOf(controller, true);
                                
                                if (binding.value.axisCode == axisCode && binding.value.value == value && controllerIndex == bindingControllerIndex) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                addUnboundBinding(binding);
                            }
        
                            addControllerAxisBinding(binding, new ControllerValue(controller, axisCode, value));
                            saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void cancelled() {
                        
                        }
                    });
                    dialog.show(getStage());
                }
            });
        }
        
        for (Actor actor : getButtonTable().getChildren()) {
            focusables.add(actor);
        }
    }
    
    @Override
    protected void result(Object object) {
        getStage().removeListener(keysListener);
    }
    
    private static class DialogKeyBinding extends Dialog {
        private ControllerListener controllerListener;
        
        public DialogKeyBinding(Binding binding) {
            super("", skin);
            
            setFillParent(true);
            Table root = getContentTable();
            
            text("Input any key, mouse button, scroll wheel, or controller button to set");
            root.row();
            text(binding.toString());
            root.row();
            text("(Press ESCAPE to cancel)");
            root.row();
            text("...");
            
            addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode != Keys.ESCAPE) {
                        fire(new KeyBindingEvent(keycode));
                    } else {
                        fire(new CancelEvent());
                    }
                    hide();
                    return true;
                }
    
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    fire(new ButtonBindingEvent(button));
                    hide();
                    return true;
                }
                
                @Override
                public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                    fire(new ScrollBindingEvent((int) amountY));
                    hide();
                    return true;
                }
            });
    
            controllerListener = new ControllerListener() {
                @Override
                public void connected(Controller controller) {
            
                }
        
                @Override
                public void disconnected(Controller controller) {
            
                }
        
                @Override
                public boolean buttonDown(Controller controller, int buttonCode) {
                    fire(new ControllerButtonBindingEvent(controller, buttonCode));
                    hide();
                    return false;
                }
        
                @Override
                public boolean buttonUp(Controller controller, int buttonCode) {
                    return false;
                }
        
                @Override
                public boolean axisMoved(Controller controller, int axisCode, float value) {
                    if (value > .5 || value < -.5) {
                        fire(new ControllerAxisBindingEvent(controller, axisCode, MathUtils.round(value)));
                        hide();
                    }
                    return false;
                }
            };
            Controllers.addListener(controllerListener);
        }
    
        @Override
        public void hide(Action action) {
            super.hide(action);
            Controllers.removeListener(controllerListener);
        }
    }
    
    private static class KeyBindingEvent extends Event {
        private int key;
    
        public KeyBindingEvent(int key) {
            this.key = key;
        }
    }
    
    private static class ButtonBindingEvent extends Event {
        private int button;
    
        public ButtonBindingEvent(int button) {
            this.button = button;
        }
    }
    
    private static class ScrollBindingEvent extends Event {
        private int scroll;
        
        public ScrollBindingEvent(int scroll) {
            this.scroll = scroll;
        }
    }
    
    private static class ControllerButtonBindingEvent extends Event {
        private Controller controller;
        private int value;
        
        public ControllerButtonBindingEvent(Controller controller, int value) {
            this.controller = controller;
            this.value = value;
        }
    }
    
    private static class ControllerAxisBindingEvent extends Event {
        private Controller controller;
        private int axisCode;
        private int value;
        
        public ControllerAxisBindingEvent(Controller controller, int axisCode, int value) {
            this.controller = controller;
            this.axisCode = axisCode;
            this.value = value;
        }
    }
    
    private static class CancelEvent extends Event {
    
    }
    
    private static abstract class BindingListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof KeyBindingEvent ) {
                keySelected(((KeyBindingEvent) event).key);
                return true;
            } else if (event instanceof ButtonBindingEvent) {
                buttonSelected(((ButtonBindingEvent) event).button);
                return true;
            } else if (event instanceof ScrollBindingEvent) {
                scrollSelected(((ScrollBindingEvent) event).scroll);
                return true;
            } else if (event instanceof ControllerButtonBindingEvent) {
                ControllerButtonBindingEvent ev = (ControllerButtonBindingEvent) event;
                controllerButtonSelected(ev.controller, ev.value);
                return true;
            } else if (event instanceof ControllerAxisBindingEvent) {
                ControllerAxisBindingEvent ev = (ControllerAxisBindingEvent) event;
                controllerAxisSelected(ev.controller, ev.axisCode, ev.value);
                return true;
            } else if (event instanceof CancelEvent) {
                cancelled();
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void keySelected(int key);
        public abstract void buttonSelected(int button);
        public abstract void scrollSelected(int scroll);
        public abstract void controllerButtonSelected(Controller controller, int value);
        public abstract void controllerAxisSelected(Controller controller, int axisCode, int value);
        public abstract void cancelled();
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        var dialog = super.show(stage, action);
        stage.setScrollFocus(stage.getRoot().findActor("scroll"));
        return dialog;
    }
}
