package com.ray3k.template.transitions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class Transitions {
    public static TransitionInstant instant() {
        return new TransitionInstant();
    }
    
    public static TransitionCrossFade crossFade(Interpolation interpolation) {
        return new TransitionCrossFade(interpolation);
    }
    
    public static TransitionCrossFade crossFade() {
        return crossFade(Interpolation.linear);
    }
    
    public static TransitionColorFade colorFade(Color backgroundColor, Interpolation interpolation) {
        return new TransitionColorFade(backgroundColor, interpolation);
    }
    
    public static TransitionColorFade colorFade(Color backgroundColor) {
        return colorFade(backgroundColor, Interpolation.linear);
    }
    
    public static TransitionPush push(float toDirection, Color backgroundColor, Interpolation interpolation) {
        return new TransitionPush(toDirection, backgroundColor, interpolation);
    }
    
    public static TransitionPush push(float toDirection, Color backgroundColor) {
        return push(toDirection, backgroundColor, Interpolation.linear);
    }
    
    public static TransitionSlide slide(float toDirection, Interpolation interpolation) {
        return new TransitionSlide(toDirection, interpolation);
    }
    
    public static TransitionSlide slide(float toDirection) {
        return slide(toDirection, Interpolation.linear);
    }
    
    public static TransitionWipe wipe(float toDirection, Interpolation interpolation) {
        return new TransitionWipe(toDirection, interpolation);
    }
    
    public static TransitionWipe wipe(float toDirection) {
        return wipe(toDirection, Interpolation.linear);
    }
    
    public static TransitionBlinds blinds(float toDirection, int blindsNumber, Interpolation interpolation) {
        return new TransitionBlinds(toDirection, blindsNumber, interpolation);
    }
    
    public static TransitionBlinds blinds(float toDirection, int blindsNumber) {
        return blinds(toDirection, blindsNumber, Interpolation.linear);
    }
    
    public static TransitionZoomIn zoomIn(Interpolation interpolation) {
        return new TransitionZoomIn(interpolation);
    }
    
    public static TransitionZoomIn zoomIn() {
        return zoomIn(Interpolation.linear);
    }
    
    public static TransitionZoomOut zoomOut(Interpolation interpolation) {
        return new TransitionZoomOut(interpolation);
    }
    
    public static TransitionZoomOut zoomOut() {
        return zoomOut(Interpolation.linear);
    }
    
    public static TransitionFlyThrough flyThrough(float scale, Interpolation interpolation) {
        return new TransitionFlyThrough(scale, interpolation);
    }
    
    public static TransitionFlyThrough flyThrough(float scale) {
        return flyThrough(scale, Interpolation.linear);
    }
}
