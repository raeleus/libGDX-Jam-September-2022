package com.ray3k.template.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.template.*;

public class DialogDebug extends Dialog {
    public DialogDebug() {
        super("", Core.skin);
        setFillParent(true);
        button("close").key(Keys.ESCAPE, null).key(Keys.ENTER, null).key(Keys.F10, null);
        
        var textButton = new TextButton("save", Core.skin);
        button(textButton);
        
        var table = Core.crossPlatformWorker.generateDebugTable();
        if (table != null) {
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Core.crossPlatformWorker.saveDebugValues(table);
                }
            });
            var scrollPane = new ScrollPane(table, Core.skin);
            scrollPane.setName("scrollPane");
            scrollPane.setFadeScrollBars(false);
            scrollPane.setFlickScroll(false);
            getContentTable().add(scrollPane);
            getContentTable().layout();
        }
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        stage.setScrollFocus(getContentTable().findActor("scrollPane"));
        return this;
    }
    
    public static class DebugListener extends InputListener {
        DialogDebug dialog;
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Keys.F10) {
                if (dialog == null) {
                    dialog = new DialogDebug();
                }
                if (dialog.getParent() == null) dialog.show(event.getStage());
                return true;
            }
            return super.keyDown(event, keycode);
        }
    }
}
