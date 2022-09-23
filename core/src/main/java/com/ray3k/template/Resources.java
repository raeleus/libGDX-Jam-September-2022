package com.ray3k.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.BoneData;
import com.esotericsoftware.spine.EventData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SlotData;
import java.lang.String;

public class Resources {
    public static TextureAtlas textures_textures;

    public static Skin skin_skin;

    public static Sound sfx_click;

    public static Sound sfx_libgdxBrakes;

    public static Sound sfx_libgdxLaugh;

    public static Sound sfx_libgdxSaw;

    public static Sound sfx_libgdxSplatter;

    public static Sound sfx_libgdxTreefall;

    public static Music bgm_audioSample;

    public static Music bgm_menu;

    public static void loadResources(AssetManager assetManager) {
        textures_textures = assetManager.get("textures/textures.atlas");
        SpineLibGDX.skeletonData = assetManager.get("spine/libGDX.json");
        SpineLibGDX.animationData = assetManager.get("spine/libGDX.json-animation");
        SpineLibGDX.animationAnimation = SpineLibGDX.skeletonData.findAnimation("animation");
        SpineLibGDX.animationStand = SpineLibGDX.skeletonData.findAnimation("stand");
        SpineLibGDX.eventLibgdxBrakes = SpineLibGDX.skeletonData.findEvent("libgdx/brakes");
        SpineLibGDX.eventLibgdxLaugh = SpineLibGDX.skeletonData.findEvent("libgdx/laugh");
        SpineLibGDX.eventLibgdxSaw = SpineLibGDX.skeletonData.findEvent("libgdx/saw");
        SpineLibGDX.eventLibgdxSplatter = SpineLibGDX.skeletonData.findEvent("libgdx/splatter");
        SpineLibGDX.eventLibgdxTreefall = SpineLibGDX.skeletonData.findEvent("libgdx/treefall");
        SpineLibGDX.boneRoot = SpineLibGDX.skeletonData.findBone("root");
        SpineLibGDX.boneLibgdxSaw = SpineLibGDX.skeletonData.findBone("libgdx/saw");
        SpineLibGDX.boneLibgdxLibgdxI = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-i");
        SpineLibGDX.boneLibgdxLibgdxL = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-l");
        SpineLibGDX.boneLibgdxLibgdxX = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-x");
        SpineLibGDX.boneLibgdxBasket = SpineLibGDX.skeletonData.findBone("libgdx/basket");
        SpineLibGDX.boneLibgdxTree = SpineLibGDX.skeletonData.findBone("libgdx/tree");
        SpineLibGDX.boneLibgdxBlood = SpineLibGDX.skeletonData.findBone("libgdx/blood");
        SpineLibGDX.boneBone = SpineLibGDX.skeletonData.findBone("bone");
        SpineLibGDX.boneLibgdxAmbulance = SpineLibGDX.skeletonData.findBone("libgdx/ambulance");
        SpineLibGDX.boneLibgdxAmbulanceWheelLeft = SpineLibGDX.skeletonData.findBone("libgdx/ambulance-wheel-left");
        SpineLibGDX.boneLibgdxAmbulanceWheelRight = SpineLibGDX.skeletonData.findBone("libgdx/ambulance-wheel-right");
        SpineLibGDX.boneBone2 = SpineLibGDX.skeletonData.findBone("bone2");
        SpineLibGDX.boneLibgdxLibgdxG = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-g");
        SpineLibGDX.boneLibgdxLibgdxD = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-d");
        SpineLibGDX.boneLibgdxGurney = SpineLibGDX.skeletonData.findBone("libgdx/gurney");
        SpineLibGDX.boneLibgdxLibgdxB = SpineLibGDX.skeletonData.findBone("libgdx/libgdx-b");
        SpineLibGDX.boneBone3 = SpineLibGDX.skeletonData.findBone("bone3");
        SpineLibGDX.slotLibgdxBg = SpineLibGDX.skeletonData.findSlot("libgdx/bg");
        SpineLibGDX.slotLibgdxBlanket = SpineLibGDX.skeletonData.findSlot("libgdx/blanket");
        SpineLibGDX.slotLibgdxBlood = SpineLibGDX.skeletonData.findSlot("libgdx/blood");
        SpineLibGDX.slotLibgdxBasket = SpineLibGDX.skeletonData.findSlot("libgdx/basket");
        SpineLibGDX.slotLibgdxLibgdxX = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-x");
        SpineLibGDX.slotLibgdxStump = SpineLibGDX.skeletonData.findSlot("libgdx/stump");
        SpineLibGDX.slotClip = SpineLibGDX.skeletonData.findSlot("clip");
        SpineLibGDX.slotLibgdxTree = SpineLibGDX.skeletonData.findSlot("libgdx/tree");
        SpineLibGDX.slotLibgdxLibgdxL = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-l");
        SpineLibGDX.slotLibgdxLibgdxI = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-i");
        SpineLibGDX.slotLibgdxSaw = SpineLibGDX.skeletonData.findSlot("libgdx/saw");
        SpineLibGDX.slotLibgdxLibgdxG = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-g");
        SpineLibGDX.slotLibgdxLibgdxD = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-d");
        SpineLibGDX.slotLibgdxGurney = SpineLibGDX.skeletonData.findSlot("libgdx/gurney");
        SpineLibGDX.slotLibgdxAmbulanceWheelLeft = SpineLibGDX.skeletonData.findSlot("libgdx/ambulance-wheel-left");
        SpineLibGDX.slotLibgdxAmbulanceWheelRight = SpineLibGDX.skeletonData.findSlot("libgdx/ambulance-wheel-right");
        SpineLibGDX.slotLibgdxAmbulance = SpineLibGDX.skeletonData.findSlot("libgdx/ambulance");
        SpineLibGDX.slotLibgdxLibgdxB = SpineLibGDX.skeletonData.findSlot("libgdx/libgdx-b");
        SpineLibGDX.skinDefault = SpineLibGDX.skeletonData.findSkin("default");
        skin_skin = assetManager.get("skin/skin.json");
        SkinSkinStyles.lDefault = skin_skin.get("default", Label.LabelStyle.class);
        SkinSkinStyles.lMenu = skin_skin.get("menu", Label.LabelStyle.class);
        SkinSkinStyles.lTextfield = skin_skin.get("textfield", Label.LabelStyle.class);
        SkinSkinStyles.spDefault = skin_skin.get("default", ScrollPane.ScrollPaneStyle.class);
        SkinSkinStyles.sDefaultHorizontal = skin_skin.get("default-horizontal", Slider.SliderStyle.class);
        SkinSkinStyles.tbToggle = skin_skin.get("toggle", TextButton.TextButtonStyle.class);
        SkinSkinStyles.tbDefault = skin_skin.get("default", TextButton.TextButtonStyle.class);
        SkinSkinStyles.tbMenu = skin_skin.get("menu", TextButton.TextButtonStyle.class);
        SkinSkinStyles.tfDefault = skin_skin.get("default", TextField.TextFieldStyle.class);
        SkinSkinStyles.ttDefault = skin_skin.get("default", TextTooltip.TextTooltipStyle.class);
        SkinSkinStyles.wDefault = skin_skin.get("default", Window.WindowStyle.class);
        sfx_click = assetManager.get("sfx/click.mp3");
        sfx_libgdxBrakes = assetManager.get("sfx/libgdx/brakes.mp3");
        sfx_libgdxLaugh = assetManager.get("sfx/libgdx/laugh.mp3");
        sfx_libgdxSaw = assetManager.get("sfx/libgdx/saw.mp3");
        sfx_libgdxSplatter = assetManager.get("sfx/libgdx/splatter.mp3");
        sfx_libgdxTreefall = assetManager.get("sfx/libgdx/treefall.mp3");
        bgm_audioSample = assetManager.get("bgm/audio-sample.mp3");
        bgm_menu = assetManager.get("bgm/menu.mp3");
    }

    public static class SpineLibGDX {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation animationStand;

        public static EventData eventLibgdxBrakes;

        public static EventData eventLibgdxLaugh;

        public static EventData eventLibgdxSaw;

        public static EventData eventLibgdxSplatter;

        public static EventData eventLibgdxTreefall;

        public static BoneData boneRoot;

        public static BoneData boneLibgdxSaw;

        public static BoneData boneLibgdxLibgdxI;

        public static BoneData boneLibgdxLibgdxL;

        public static BoneData boneLibgdxLibgdxX;

        public static BoneData boneLibgdxBasket;

        public static BoneData boneLibgdxTree;

        public static BoneData boneLibgdxBlood;

        public static BoneData boneBone;

        public static BoneData boneLibgdxAmbulance;

        public static BoneData boneLibgdxAmbulanceWheelLeft;

        public static BoneData boneLibgdxAmbulanceWheelRight;

        public static BoneData boneBone2;

        public static BoneData boneLibgdxLibgdxG;

        public static BoneData boneLibgdxLibgdxD;

        public static BoneData boneLibgdxGurney;

        public static BoneData boneLibgdxLibgdxB;

        public static BoneData boneBone3;

        public static SlotData slotLibgdxBg;

        public static SlotData slotLibgdxBlanket;

        public static SlotData slotLibgdxBlood;

        public static SlotData slotLibgdxBasket;

        public static SlotData slotLibgdxLibgdxX;

        public static SlotData slotLibgdxStump;

        public static SlotData slotClip;

        public static SlotData slotLibgdxTree;

        public static SlotData slotLibgdxLibgdxL;

        public static SlotData slotLibgdxLibgdxI;

        public static SlotData slotLibgdxSaw;

        public static SlotData slotLibgdxLibgdxG;

        public static SlotData slotLibgdxLibgdxD;

        public static SlotData slotLibgdxGurney;

        public static SlotData slotLibgdxAmbulanceWheelLeft;

        public static SlotData slotLibgdxAmbulanceWheelRight;

        public static SlotData slotLibgdxAmbulance;

        public static SlotData slotLibgdxLibgdxB;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SkinSkinStyles {
        public static Label.LabelStyle lDefault;

        public static Label.LabelStyle lMenu;

        public static Label.LabelStyle lTextfield;

        public static ScrollPane.ScrollPaneStyle spDefault;

        public static Slider.SliderStyle sDefaultHorizontal;

        public static TextButton.TextButtonStyle tbToggle;

        public static TextButton.TextButtonStyle tbDefault;

        public static TextButton.TextButtonStyle tbMenu;

        public static TextField.TextFieldStyle tfDefault;

        public static TextTooltip.TextTooltipStyle ttDefault;

        public static Window.WindowStyle wDefault;
    }

    public static class Values {
        public static float jumpVelocity = 10.0f;

        public static String name = "Raeleus";

        public static boolean godMode = true;

        public static int id = 10;
    }
}
