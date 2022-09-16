package com.ray3k.template.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;

/**
 * Launches the GWT application.
 */
public class GwtLauncher extends GwtApplication implements CrossPlatformWorker {
    @Override
    public GwtApplicationConfiguration getConfig() {
        var cfg = new GwtApplicationConfiguration(true);
        cfg.antialiasing = true;
        cfg.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
        cfg.useAccelerometer = false;
        cfg.padHorizontal = 0;
        cfg.padVertical = 0;
        //cfg.useDebugGL = true;
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListener());
        return cfg;
    }
    
    class ResizeListener implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                int width = event.getWidth();
                int height = event.getHeight();
                getRootPanel().setWidth("" + width + "px");
                getRootPanel().setHeight("" + height + "px");
                getApplicationListener().resize(width, height);
                Gdx.graphics.setWindowedMode(width, height);
            }
        }
    }
    
    @Override
    public ApplicationListener createApplicationListener () {
        Core core = new Core();
        crossPlatformWorker = this;
        return core;
    }
    
    @Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
        return createPreloaderPanel(GWT.getHostPageBaseURL() + "preloadlogo.gif");
    }
    
    @Override
    protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
        meterPanel.setStyleName("gdx-meter");
        meterPanel.addStyleName("nostripes");
        meterStyle.setProperty("backgroundColor", "#ffffff");
        meterStyle.setProperty("backgroundImage", "none");
    }
    
    @Override
    public void onModuleLoad() {
        FreetypeInjector.inject(GwtLauncher.super::onModuleLoad);
    }
    
    @Override
    public Table generateDebugTable() {
        return null;
    }
    
    @Override
    public void saveDebugValues(Table table) {
    
    }
}
