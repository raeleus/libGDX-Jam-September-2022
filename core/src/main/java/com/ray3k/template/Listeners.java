package com.ray3k.template;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Listeners {
    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }
    
    public static void onClick (Actor actor, Runnable runnable) {
        onClick(actor, Buttons.LEFT, runnable);
    }
    
    public static void onClick (Actor actor, int button, Runnable runnable) {
        actor.addListener(new ClickListener(button) {
            public void clicked (InputEvent event, float x, float y) {
                runnable.run();
            }
        });
    }
}
