package com.ray3k.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
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

    public static Sound sfx_ahh;

    public static Sound sfx_click;

    public static Sound sfx_libgdx;

    public static Sound sfx_pleaseDontKillMe;

    public static Sound sfx_shot;

    public static Sound sfx_swoosh;

    public static Sound sfx_tv;

    public static Music bgm_audioSample;

    public static Music bgm_menu;

    public static void loadResources(AssetManager assetManager) {
        textures_textures = assetManager.get("textures/textures.atlas");
        SpineLibgdx.skeletonData = assetManager.get("spine/libgdx.json");
        SpineLibgdx.animationData = assetManager.get("spine/libgdx.json-animation");
        SpineLibgdx.animationAnimation = SpineLibgdx.skeletonData.findAnimation("animation");
        SpineLibgdx.animationStand = SpineLibgdx.skeletonData.findAnimation("stand");
        SpineLibgdx.eventAhh = SpineLibgdx.skeletonData.findEvent("ahh");
        SpineLibgdx.eventLibgdx = SpineLibgdx.skeletonData.findEvent("libgdx");
        SpineLibgdx.eventPleaseDontKillMe = SpineLibgdx.skeletonData.findEvent("please don't kill me");
        SpineLibgdx.eventPleaseDontKillMe2 = SpineLibgdx.skeletonData.findEvent("please don't kill me2");
        SpineLibgdx.eventShot = SpineLibgdx.skeletonData.findEvent("shot");
        SpineLibgdx.boneRoot = SpineLibgdx.skeletonData.findBone("root");
        SpineLibgdx.boneBlood0 = SpineLibgdx.skeletonData.findBone("blood-0");
        SpineLibgdx.boneBlood1 = SpineLibgdx.skeletonData.findBone("blood-1");
        SpineLibgdx.boneBlood2 = SpineLibgdx.skeletonData.findBone("blood-2");
        SpineLibgdx.boneBlood3 = SpineLibgdx.skeletonData.findBone("blood-3");
        SpineLibgdx.boneBlood4 = SpineLibgdx.skeletonData.findBone("blood-4");
        SpineLibgdx.boneBlood5 = SpineLibgdx.skeletonData.findBone("blood-5");
        SpineLibgdx.boneBlood6 = SpineLibgdx.skeletonData.findBone("blood-6");
        SpineLibgdx.boneBlood7 = SpineLibgdx.skeletonData.findBone("blood-7");
        SpineLibgdx.boneBlood8 = SpineLibgdx.skeletonData.findBone("blood-8");
        SpineLibgdx.boneBlood9 = SpineLibgdx.skeletonData.findBone("blood-9");
        SpineLibgdx.boneBlood10 = SpineLibgdx.skeletonData.findBone("blood-10");
        SpineLibgdx.boneBlood11 = SpineLibgdx.skeletonData.findBone("blood-11");
        SpineLibgdx.boneLibgdxL = SpineLibgdx.skeletonData.findBone("libgdx-l");
        SpineLibgdx.boneLibgdxI = SpineLibgdx.skeletonData.findBone("libgdx-i");
        SpineLibgdx.boneLibgdxB = SpineLibgdx.skeletonData.findBone("libgdx-b");
        SpineLibgdx.boneLibgdxG = SpineLibgdx.skeletonData.findBone("libgdx-g");
        SpineLibgdx.boneLibgdxD = SpineLibgdx.skeletonData.findBone("libgdx-d");
        SpineLibgdx.boneLibgdxX = SpineLibgdx.skeletonData.findBone("libgdx-x");
        SpineLibgdx.boneSpark0 = SpineLibgdx.skeletonData.findBone("spark-0");
        SpineLibgdx.slotLibgdxReference = SpineLibgdx.skeletonData.findSlot("libgdx-reference");
        SpineLibgdx.slotWhite = SpineLibgdx.skeletonData.findSlot("white");
        SpineLibgdx.slotBlood0 = SpineLibgdx.skeletonData.findSlot("blood-0");
        SpineLibgdx.slotBlood1 = SpineLibgdx.skeletonData.findSlot("blood-1");
        SpineLibgdx.slotBlood2 = SpineLibgdx.skeletonData.findSlot("blood-2");
        SpineLibgdx.slotBlood3 = SpineLibgdx.skeletonData.findSlot("blood-3");
        SpineLibgdx.slotBlood4 = SpineLibgdx.skeletonData.findSlot("blood-4");
        SpineLibgdx.slotBlood5 = SpineLibgdx.skeletonData.findSlot("blood-5");
        SpineLibgdx.slotBlood6 = SpineLibgdx.skeletonData.findSlot("blood-6");
        SpineLibgdx.slotBlood7 = SpineLibgdx.skeletonData.findSlot("blood-7");
        SpineLibgdx.slotBlood8 = SpineLibgdx.skeletonData.findSlot("blood-8");
        SpineLibgdx.slotBlood9 = SpineLibgdx.skeletonData.findSlot("blood-9");
        SpineLibgdx.slotBlood10 = SpineLibgdx.skeletonData.findSlot("blood-10");
        SpineLibgdx.slotBlood11 = SpineLibgdx.skeletonData.findSlot("blood-11");
        SpineLibgdx.slotLibgdxL = SpineLibgdx.skeletonData.findSlot("libgdx-l");
        SpineLibgdx.slotLibgdxI = SpineLibgdx.skeletonData.findSlot("libgdx-i");
        SpineLibgdx.slotLibgdxB = SpineLibgdx.skeletonData.findSlot("libgdx-b");
        SpineLibgdx.slotLibgdxG = SpineLibgdx.skeletonData.findSlot("libgdx-g");
        SpineLibgdx.slotLibgdxD = SpineLibgdx.skeletonData.findSlot("libgdx-d");
        SpineLibgdx.slotLibgdxX = SpineLibgdx.skeletonData.findSlot("libgdx-x");
        SpineLibgdx.slotMouth = SpineLibgdx.skeletonData.findSlot("mouth");
        SpineLibgdx.slotSpark0 = SpineLibgdx.skeletonData.findSlot("spark-0");
        SpineLibgdx.skinDefault = SpineLibgdx.skeletonData.findSkin("default");
        SpineRay3k.skeletonData = assetManager.get("spine/ray3k.json");
        SpineRay3k.animationData = assetManager.get("spine/ray3k.json-animation");
        SpineRay3k.animationAnimation = SpineRay3k.skeletonData.findAnimation("animation");
        SpineRay3k.animationStand = SpineRay3k.skeletonData.findAnimation("stand");
        SpineRay3k.eventSwoosh = SpineRay3k.skeletonData.findEvent("swoosh");
        SpineRay3k.eventTv = SpineRay3k.skeletonData.findEvent("tv");
        SpineRay3k.boneRoot = SpineRay3k.skeletonData.findBone("root");
        SpineRay3k.boneRay3kLogo = SpineRay3k.skeletonData.findBone("ray3k-logo");
        SpineRay3k.boneRay3kLine = SpineRay3k.skeletonData.findBone("ray3k-line");
        SpineRay3k.boneRay3kSpot = SpineRay3k.skeletonData.findBone("ray3k-spot");
        SpineRay3k.slotWhite = SpineRay3k.skeletonData.findSlot("white");
        SpineRay3k.slotRay3kSpot = SpineRay3k.skeletonData.findSlot("ray3k-spot");
        SpineRay3k.slotRay3kLine = SpineRay3k.skeletonData.findSlot("ray3k-line");
        SpineRay3k.slotRay3kLogo = SpineRay3k.skeletonData.findSlot("ray3k-logo");
        SpineRay3k.skinDefault = SpineRay3k.skeletonData.findSkin("default");
        skin_skin = assetManager.get("skin/skin.json");
        SkinSkinStyles.bClose = skin_skin.get("close", Button.ButtonStyle.class);
        SkinSkinStyles.bDefault = skin_skin.get("default", Button.ButtonStyle.class);
        SkinSkinStyles.bPause = skin_skin.get("pause", Button.ButtonStyle.class);
        SkinSkinStyles.bToggle = skin_skin.get("toggle", Button.ButtonStyle.class);
        SkinSkinStyles.bStop = skin_skin.get("stop", Button.ButtonStyle.class);
        SkinSkinStyles.bPlay = skin_skin.get("play", Button.ButtonStyle.class);
        SkinSkinStyles.cbDefault = skin_skin.get("default", CheckBox.CheckBoxStyle.class);
        SkinSkinStyles.ibDefault = skin_skin.get("default", ImageButton.ImageButtonStyle.class);
        SkinSkinStyles.itbDefault = skin_skin.get("default", ImageTextButton.ImageTextButtonStyle.class);
        SkinSkinStyles.itbRadio = skin_skin.get("radio", ImageTextButton.ImageTextButtonStyle.class);
        SkinSkinStyles.lSmall = skin_skin.get("small", Label.LabelStyle.class);
        SkinSkinStyles.lDefault = skin_skin.get("default", Label.LabelStyle.class);
        SkinSkinStyles.lstSelectBox = skin_skin.get("select-box", List.ListStyle.class);
        SkinSkinStyles.pDefaultVertical = skin_skin.get("default-vertical", ProgressBar.ProgressBarStyle.class);
        SkinSkinStyles.pDefaultHorizontal = skin_skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        SkinSkinStyles.spSelectBox = skin_skin.get("select-box", ScrollPane.ScrollPaneStyle.class);
        SkinSkinStyles.spDefault = skin_skin.get("default", ScrollPane.ScrollPaneStyle.class);
        SkinSkinStyles.sbDefault = skin_skin.get("default", SelectBox.SelectBoxStyle.class);
        SkinSkinStyles.sScrubber = skin_skin.get("scrubber", Slider.SliderStyle.class);
        SkinSkinStyles.sDefaultVertical = skin_skin.get("default-vertical", Slider.SliderStyle.class);
        SkinSkinStyles.sDefaultHorizontal = skin_skin.get("default-horizontal", Slider.SliderStyle.class);
        SkinSkinStyles.spltDefaultVertical = skin_skin.get("default-vertical", SplitPane.SplitPaneStyle.class);
        SkinSkinStyles.spltDefaultHorizontal = skin_skin.get("default-horizontal", SplitPane.SplitPaneStyle.class);
        SkinSkinStyles.tbToggle = skin_skin.get("toggle", TextButton.TextButtonStyle.class);
        SkinSkinStyles.tbDefault = skin_skin.get("default", TextButton.TextButtonStyle.class);
        SkinSkinStyles.tfDefault = skin_skin.get("default", TextField.TextFieldStyle.class);
        SkinSkinStyles.ttDefault = skin_skin.get("default", TextTooltip.TextTooltipStyle.class);
        SkinSkinStyles.tsDefault = skin_skin.get("default", Touchpad.TouchpadStyle.class);
        SkinSkinStyles.tDefault = skin_skin.get("default", Tree.TreeStyle.class);
        SkinSkinStyles.wDefault = skin_skin.get("default", Window.WindowStyle.class);
        sfx_ahh = assetManager.get("sfx/ahh.mp3");
        sfx_click = assetManager.get("sfx/click.mp3");
        sfx_libgdx = assetManager.get("sfx/libgdx.mp3");
        sfx_pleaseDontKillMe = assetManager.get("sfx/please don't kill me.mp3");
        sfx_shot = assetManager.get("sfx/shot.mp3");
        sfx_swoosh = assetManager.get("sfx/swoosh.mp3");
        sfx_tv = assetManager.get("sfx/tv.mp3");
        bgm_audioSample = assetManager.get("bgm/audio-sample.mp3");
        bgm_menu = assetManager.get("bgm/menu.mp3");
    }

    public static class SpineLibgdx {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation animationStand;

        public static EventData eventAhh;

        public static EventData eventLibgdx;

        public static EventData eventPleaseDontKillMe;

        public static EventData eventPleaseDontKillMe2;

        public static EventData eventShot;

        public static BoneData boneRoot;

        public static BoneData boneBlood0;

        public static BoneData boneBlood1;

        public static BoneData boneBlood2;

        public static BoneData boneBlood3;

        public static BoneData boneBlood4;

        public static BoneData boneBlood5;

        public static BoneData boneBlood6;

        public static BoneData boneBlood7;

        public static BoneData boneBlood8;

        public static BoneData boneBlood9;

        public static BoneData boneBlood10;

        public static BoneData boneBlood11;

        public static BoneData boneLibgdxL;

        public static BoneData boneLibgdxI;

        public static BoneData boneLibgdxB;

        public static BoneData boneLibgdxG;

        public static BoneData boneLibgdxD;

        public static BoneData boneLibgdxX;

        public static BoneData boneSpark0;

        public static SlotData slotLibgdxReference;

        public static SlotData slotWhite;

        public static SlotData slotBlood0;

        public static SlotData slotBlood1;

        public static SlotData slotBlood2;

        public static SlotData slotBlood3;

        public static SlotData slotBlood4;

        public static SlotData slotBlood5;

        public static SlotData slotBlood6;

        public static SlotData slotBlood7;

        public static SlotData slotBlood8;

        public static SlotData slotBlood9;

        public static SlotData slotBlood10;

        public static SlotData slotBlood11;

        public static SlotData slotLibgdxL;

        public static SlotData slotLibgdxI;

        public static SlotData slotLibgdxB;

        public static SlotData slotLibgdxG;

        public static SlotData slotLibgdxD;

        public static SlotData slotLibgdxX;

        public static SlotData slotMouth;

        public static SlotData slotSpark0;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineRay3k {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation animationStand;

        public static EventData eventSwoosh;

        public static EventData eventTv;

        public static BoneData boneRoot;

        public static BoneData boneRay3kLogo;

        public static BoneData boneRay3kLine;

        public static BoneData boneRay3kSpot;

        public static SlotData slotWhite;

        public static SlotData slotRay3kSpot;

        public static SlotData slotRay3kLine;

        public static SlotData slotRay3kLogo;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SkinSkinStyles {
        public static Button.ButtonStyle bClose;

        public static Button.ButtonStyle bDefault;

        public static Button.ButtonStyle bPause;

        public static Button.ButtonStyle bToggle;

        public static Button.ButtonStyle bStop;

        public static Button.ButtonStyle bPlay;

        public static CheckBox.CheckBoxStyle cbDefault;

        public static ImageButton.ImageButtonStyle ibDefault;

        public static ImageTextButton.ImageTextButtonStyle itbDefault;

        public static ImageTextButton.ImageTextButtonStyle itbRadio;

        public static Label.LabelStyle lSmall;

        public static Label.LabelStyle lDefault;

        public static List.ListStyle lstSelectBox;

        public static ProgressBar.ProgressBarStyle pDefaultVertical;

        public static ProgressBar.ProgressBarStyle pDefaultHorizontal;

        public static ScrollPane.ScrollPaneStyle spSelectBox;

        public static ScrollPane.ScrollPaneStyle spDefault;

        public static SelectBox.SelectBoxStyle sbDefault;

        public static Slider.SliderStyle sScrubber;

        public static Slider.SliderStyle sDefaultVertical;

        public static Slider.SliderStyle sDefaultHorizontal;

        public static SplitPane.SplitPaneStyle spltDefaultVertical;

        public static SplitPane.SplitPaneStyle spltDefaultHorizontal;

        public static TextButton.TextButtonStyle tbToggle;

        public static TextButton.TextButtonStyle tbDefault;

        public static TextField.TextFieldStyle tfDefault;

        public static TextTooltip.TextTooltipStyle ttDefault;

        public static Touchpad.TouchpadStyle tsDefault;

        public static Tree.TreeStyle tDefault;

        public static Window.WindowStyle wDefault;
    }

    public static class Values {
        public static float jumpVelocity = 10.0f;

        public static String name = "Raeleus";

        public static boolean godMode = true;

        public static int id = 10;
    }
}
