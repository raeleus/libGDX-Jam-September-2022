package com.ray3k.template;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface CrossPlatformWorker {
    Table generateDebugTable();
    void saveDebugValues(Table table);
}
