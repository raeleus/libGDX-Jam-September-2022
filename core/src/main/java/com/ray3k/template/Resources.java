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
        SpineChomper.skeletonData = assetManager.get("spine/chomper.json");
        SpineChomper.animationData = assetManager.get("spine/chomper.json-animation");
        SpineChomper.animationAttack = SpineChomper.skeletonData.findAnimation("attack");
        SpineChomper.animationStand = SpineChomper.skeletonData.findAnimation("stand");
        SpineChomper.boneRoot = SpineChomper.skeletonData.findBone("root");
        SpineChomper.boneGameChompNeck3 = SpineChomper.skeletonData.findBone("game/chomp-neck3");
        SpineChomper.boneGameChompNeck2 = SpineChomper.skeletonData.findBone("game/chomp-neck2");
        SpineChomper.boneGameChompNeck = SpineChomper.skeletonData.findBone("game/chomp-neck");
        SpineChomper.boneGameChompHead = SpineChomper.skeletonData.findBone("game/chomp-head");
        SpineChomper.boneGameChompHead3 = SpineChomper.skeletonData.findBone("game/chomp-head3");
        SpineChomper.boneGameChompHead2 = SpineChomper.skeletonData.findBone("game/chomp-head2");
        SpineChomper.boneBone = SpineChomper.skeletonData.findBone("bone");
        SpineChomper.boneBone2 = SpineChomper.skeletonData.findBone("bone2");
        SpineChomper.boneTarget = SpineChomper.skeletonData.findBone("target");
        SpineChomper.slotGameChompNeck = SpineChomper.skeletonData.findSlot("game/chomp-neck");
        SpineChomper.slotGameChompNeck2 = SpineChomper.skeletonData.findSlot("game/chomp-neck2");
        SpineChomper.slotGameChompNeck3 = SpineChomper.skeletonData.findSlot("game/chomp-neck3");
        SpineChomper.slotGameChompHead = SpineChomper.skeletonData.findSlot("game/chomp-head");
        SpineChomper.slotGameChompMouth = SpineChomper.skeletonData.findSlot("game/chomp-mouth");
        SpineChomper.slotNeck = SpineChomper.skeletonData.findSlot("neck");
        SpineChomper.skinDefault = SpineChomper.skeletonData.findSkin("default");
        SpineFart.skeletonData = assetManager.get("spine/fart.json");
        SpineFart.animationData = assetManager.get("spine/fart.json-animation");
        SpineFart.animationAnimation = SpineFart.skeletonData.findAnimation("animation");
        SpineFart.boneRoot = SpineFart.skeletonData.findBone("root");
        SpineFart.slotGameFart01 = SpineFart.skeletonData.findSlot("game/fart-01");
        SpineFart.skinDefault = SpineFart.skeletonData.findSkin("default");
        SpineFish.skeletonData = assetManager.get("spine/fish.json");
        SpineFish.animationData = assetManager.get("spine/fish.json-animation");
        SpineFish.animationAnimation = SpineFish.skeletonData.findAnimation("animation");
        SpineFish.boneRoot = SpineFish.skeletonData.findBone("root");
        SpineFish.boneGameFish = SpineFish.skeletonData.findBone("game/fish");
        SpineFish.boneGameFishTail = SpineFish.skeletonData.findBone("game/fish-tail");
        SpineFish.slotGameFishTail = SpineFish.skeletonData.findSlot("game/fish-tail");
        SpineFish.slotGameFish = SpineFish.skeletonData.findSlot("game/fish");
        SpineFish.skinDefault = SpineFish.skeletonData.findSkin("default");
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
        SpinePlatformStone.skeletonData = assetManager.get("spine/platform-stone.json");
        SpinePlatformStone.animationData = assetManager.get("spine/platform-stone.json-animation");
        SpinePlatformStone.animationAnimation = SpinePlatformStone.skeletonData.findAnimation("animation");
        SpinePlatformStone.boneRoot = SpinePlatformStone.skeletonData.findBone("root");
        SpinePlatformStone.slotGamePlatformStone = SpinePlatformStone.skeletonData.findSlot("game/platform-stone");
        SpinePlatformStone.slotBbox = SpinePlatformStone.skeletonData.findSlot("bbox");
        SpinePlatformStone.skinDefault = SpinePlatformStone.skeletonData.findSkin("default");
        SpineShy.skeletonData = assetManager.get("spine/shy.json");
        SpineShy.animationData = assetManager.get("spine/shy.json-animation");
        SpineShy.animationSpear = SpineShy.skeletonData.findAnimation("spear");
        SpineShy.animationStanding = SpineShy.skeletonData.findAnimation("standing");
        SpineShy.animationWalking = SpineShy.skeletonData.findAnimation("walking");
        SpineShy.boneRoot = SpineShy.skeletonData.findBone("root");
        SpineShy.boneGameShy = SpineShy.skeletonData.findBone("game/shy");
        SpineShy.boneGameShyFootRight = SpineShy.skeletonData.findBone("game/shy-foot-right");
        SpineShy.boneGameShyFootLeft = SpineShy.skeletonData.findBone("game/shy-foot-left");
        SpineShy.boneGameShy2 = SpineShy.skeletonData.findBone("game/shy2");
        SpineShy.boneGameShyShield = SpineShy.skeletonData.findBone("game/shy-shield");
        SpineShy.boneGameShySpear = SpineShy.skeletonData.findBone("game/shy-spear");
        SpineShy.slotGameShyFootRight = SpineShy.skeletonData.findSlot("game/shy-foot-right");
        SpineShy.slotGameShy = SpineShy.skeletonData.findSlot("game/shy");
        SpineShy.slotGameShyFootLeft = SpineShy.skeletonData.findSlot("game/shy-foot-left");
        SpineShy.slotGameShyShield = SpineShy.skeletonData.findSlot("game/shy-shield");
        SpineShy.slotGameShySpear = SpineShy.skeletonData.findSlot("game/shy-spear");
        SpineShy.skinDefault = SpineShy.skeletonData.findSkin("default");
        SpineSkidDust.skeletonData = assetManager.get("spine/skid-dust.json");
        SpineSkidDust.animationData = assetManager.get("spine/skid-dust.json-animation");
        SpineSkidDust.animationAnimation = SpineSkidDust.skeletonData.findAnimation("animation");
        SpineSkidDust.boneRoot = SpineSkidDust.skeletonData.findBone("root");
        SpineSkidDust.slotGameTrail01 = SpineSkidDust.skeletonData.findSlot("game/trail-01");
        SpineSkidDust.skinDefault = SpineSkidDust.skeletonData.findSkin("default");
        SpineZebra.skeletonData = assetManager.get("spine/zebra.json");
        SpineZebra.animationData = assetManager.get("spine/zebra.json-animation");
        SpineZebra.animationAim = SpineZebra.skeletonData.findAnimation("aim");
        SpineZebra.animationBlink = SpineZebra.skeletonData.findAnimation("blink");
        SpineZebra.animationDuck = SpineZebra.skeletonData.findAnimation("duck");
        SpineZebra.animationFull = SpineZebra.skeletonData.findAnimation("full");
        SpineZebra.animationGulp = SpineZebra.skeletonData.findAnimation("gulp");
        SpineZebra.animationHurt = SpineZebra.skeletonData.findAnimation("hurt");
        SpineZebra.animationJump = SpineZebra.skeletonData.findAnimation("jump");
        SpineZebra.animationJumpAir = SpineZebra.skeletonData.findAnimation("jump-air");
        SpineZebra.animationJumpDouble = SpineZebra.skeletonData.findAnimation("jump-double");
        SpineZebra.animationJumpFall = SpineZebra.skeletonData.findAnimation("jump-fall");
        SpineZebra.animationJumpLand = SpineZebra.skeletonData.findAnimation("jump-land");
        SpineZebra.animationLick = SpineZebra.skeletonData.findAnimation("lick");
        SpineZebra.animationLickBlocked = SpineZebra.skeletonData.findAnimation("lick-blocked");
        SpineZebra.animationLickUp = SpineZebra.skeletonData.findAnimation("lick-up");
        SpineZebra.animationLickUpBlocked = SpineZebra.skeletonData.findAnimation("lick-up-blocked");
        SpineZebra.animationNoBaby = SpineZebra.skeletonData.findAnimation("no-baby");
        SpineZebra.animationPush = SpineZebra.skeletonData.findAnimation("push");
        SpineZebra.animationRun = SpineZebra.skeletonData.findAnimation("run");
        SpineZebra.animationSkid = SpineZebra.skeletonData.findAnimation("skid");
        SpineZebra.animationSpit = SpineZebra.skeletonData.findAnimation("spit");
        SpineZebra.animationSpitUp = SpineZebra.skeletonData.findAnimation("spit-up");
        SpineZebra.animationStand = SpineZebra.skeletonData.findAnimation("stand");
        SpineZebra.animationTail = SpineZebra.skeletonData.findAnimation("tail");
        SpineZebra.animationTailRun = SpineZebra.skeletonData.findAnimation("tail-run");
        SpineZebra.animationWalk = SpineZebra.skeletonData.findAnimation("walk");
        SpineZebra.animationYum = SpineZebra.skeletonData.findAnimation("yum");
        SpineZebra.boneRoot = SpineZebra.skeletonData.findBone("root");
        SpineZebra.boneTail1 = SpineZebra.skeletonData.findBone("tail1");
        SpineZebra.boneTail2 = SpineZebra.skeletonData.findBone("tail2");
        SpineZebra.boneTail3 = SpineZebra.skeletonData.findBone("tail3");
        SpineZebra.boneTail4 = SpineZebra.skeletonData.findBone("tail4");
        SpineZebra.boneTail5 = SpineZebra.skeletonData.findBone("tail5");
        SpineZebra.boneTail6 = SpineZebra.skeletonData.findBone("tail6");
        SpineZebra.boneHip = SpineZebra.skeletonData.findBone("hip");
        SpineZebra.boneBody = SpineZebra.skeletonData.findBone("body");
        SpineZebra.boneLegRight = SpineZebra.skeletonData.findBone("leg-right");
        SpineZebra.boneForelegRight = SpineZebra.skeletonData.findBone("foreleg-right");
        SpineZebra.boneLegLeft = SpineZebra.skeletonData.findBone("leg-left");
        SpineZebra.boneForelegLeft = SpineZebra.skeletonData.findBone("foreleg-left");
        SpineZebra.boneHand = SpineZebra.skeletonData.findBone("hand");
        SpineZebra.boneHead = SpineZebra.skeletonData.findBone("head");
        SpineZebra.boneSkelHip = SpineZebra.skeletonData.findBone("skel-hip");
        SpineZebra.boneSkelFootRight = SpineZebra.skeletonData.findBone("skel-foot-right");
        SpineZebra.boneSkelFootLeft = SpineZebra.skeletonData.findBone("skel-foot-left");
        SpineZebra.boneSkelTorso = SpineZebra.skeletonData.findBone("skel-torso");
        SpineZebra.boneSkelHandRight = SpineZebra.skeletonData.findBone("skel-hand-right");
        SpineZebra.boneSkelHandLeft = SpineZebra.skeletonData.findBone("skel-hand-left");
        SpineZebra.boneSkelHead = SpineZebra.skeletonData.findBone("skel-head");
        SpineZebra.boneSkelHat = SpineZebra.skeletonData.findBone("skel-hat");
        SpineZebra.boneIkRight = SpineZebra.skeletonData.findBone("ik-right");
        SpineZebra.boneIkLeft = SpineZebra.skeletonData.findBone("ik-left");
        SpineZebra.boneFootLeft = SpineZebra.skeletonData.findBone("foot-left");
        SpineZebra.boneFootRight = SpineZebra.skeletonData.findBone("foot-right");
        SpineZebra.boneGameZebraTongue = SpineZebra.skeletonData.findBone("game/zebra-tongue");
        SpineZebra.boneGameZebraTongue2 = SpineZebra.skeletonData.findBone("game/zebra-tongue2");
        SpineZebra.boneGameZebraTongue3 = SpineZebra.skeletonData.findBone("game/zebra-tongue3");
        SpineZebra.boneGameZebraTongue4 = SpineZebra.skeletonData.findBone("game/zebra-tongue4");
        SpineZebra.boneGameZebraTongue5 = SpineZebra.skeletonData.findBone("game/zebra-tongue5");
        SpineZebra.boneGameZebraTongue6 = SpineZebra.skeletonData.findBone("game/zebra-tongue6");
        SpineZebra.boneGameZebraTongue7 = SpineZebra.skeletonData.findBone("game/zebra-tongue7");
        SpineZebra.boneGameZebraTongue8 = SpineZebra.skeletonData.findBone("game/zebra-tongue8");
        SpineZebra.boneGameZebraTongue9 = SpineZebra.skeletonData.findBone("game/zebra-tongue9");
        SpineZebra.boneGameZebraTongue1 = SpineZebra.skeletonData.findBone("game/zebra-tongue_1");
        SpineZebra.boneSmokeTarget = SpineZebra.skeletonData.findBone("smoke-target");
        SpineZebra.boneFartTarget = SpineZebra.skeletonData.findBone("fart-target");
        SpineZebra.slotGameZebraFootLeft = SpineZebra.skeletonData.findSlot("game/zebra-foot-left");
        SpineZebra.slotGameZebraTail = SpineZebra.skeletonData.findSlot("game/zebra-tail");
        SpineZebra.slotGameSkelFootLeft = SpineZebra.skeletonData.findSlot("game/skel-foot-left");
        SpineZebra.slotGameZebraBodyBack = SpineZebra.skeletonData.findSlot("game/zebra-body-back");
        SpineZebra.slotGameZebraHead = SpineZebra.skeletonData.findSlot("game/zebra-head");
        SpineZebra.slotGameZebraBody = SpineZebra.skeletonData.findSlot("game/zebra-body");
        SpineZebra.slotGameZebraDuck = SpineZebra.skeletonData.findSlot("game/zebra-duck");
        SpineZebra.slotGameZebraEyes = SpineZebra.skeletonData.findSlot("game/zebra-eyes");
        SpineZebra.slotGameZebraFace = SpineZebra.skeletonData.findSlot("game/zebra-face");
        SpineZebra.slotGameZebraFootRight = SpineZebra.skeletonData.findSlot("game/zebra-foot-right");
        SpineZebra.slotGameSkelHatBack = SpineZebra.skeletonData.findSlot("game/skel-hat-back");
        SpineZebra.slotGameSkelHead = SpineZebra.skeletonData.findSlot("game/skel-head");
        SpineZebra.slotGameSkelHat = SpineZebra.skeletonData.findSlot("game/skel-hat");
        SpineZebra.slotGameSkelHandLeft = SpineZebra.skeletonData.findSlot("game/skel-hand-left");
        SpineZebra.slotGameSkelTorso = SpineZebra.skeletonData.findSlot("game/skel-torso");
        SpineZebra.slotGameSkelHip = SpineZebra.skeletonData.findSlot("game/skel-hip");
        SpineZebra.slotGameSkelFootRight = SpineZebra.skeletonData.findSlot("game/skel-foot-right");
        SpineZebra.slotGameSkelHandRight = SpineZebra.skeletonData.findSlot("game/skel-hand-right");
        SpineZebra.slotGameZebraTail1 = SpineZebra.skeletonData.findSlot("game/zebra-tail1");
        SpineZebra.slotGameTemp = SpineZebra.skeletonData.findSlot("game/temp");
        SpineZebra.slotGameZebraFaceFront = SpineZebra.skeletonData.findSlot("game/zebra-face-front");
        SpineZebra.slotGameZebraTongue = SpineZebra.skeletonData.findSlot("game/zebra-tongue");
        SpineZebra.slotGameZebraTongueTip = SpineZebra.skeletonData.findSlot("game/zebra-tongue-tip");
        SpineZebra.slotGameZebraTongue1 = SpineZebra.skeletonData.findSlot("game/zebra-tongue_1");
        SpineZebra.slotGameZebraHand = SpineZebra.skeletonData.findSlot("game/zebra-hand");
        SpineZebra.slotBbox = SpineZebra.skeletonData.findSlot("bbox");
        SpineZebra.slotFootSensor = SpineZebra.skeletonData.findSlot("foot-sensor");
        SpineZebra.slotHeadSensor = SpineZebra.skeletonData.findSlot("head-sensor");
        SpineZebra.slotRightSensor = SpineZebra.skeletonData.findSlot("right-sensor");
        SpineZebra.slotLeftSensor = SpineZebra.skeletonData.findSlot("left-sensor");
        SpineZebra.skinDefault = SpineZebra.skeletonData.findSkin("default");
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

    public static class SpineChomper {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAttack;

        public static Animation animationStand;

        public static BoneData boneRoot;

        public static BoneData boneGameChompNeck3;

        public static BoneData boneGameChompNeck2;

        public static BoneData boneGameChompNeck;

        public static BoneData boneGameChompHead;

        public static BoneData boneGameChompHead3;

        public static BoneData boneGameChompHead2;

        public static BoneData boneBone;

        public static BoneData boneBone2;

        public static BoneData boneTarget;

        public static SlotData slotGameChompNeck;

        public static SlotData slotGameChompNeck2;

        public static SlotData slotGameChompNeck3;

        public static SlotData slotGameChompHead;

        public static SlotData slotGameChompMouth;

        public static SlotData slotNeck;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineFart {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static BoneData boneRoot;

        public static SlotData slotGameFart01;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineFish {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static BoneData boneRoot;

        public static BoneData boneGameFish;

        public static BoneData boneGameFishTail;

        public static SlotData slotGameFishTail;

        public static SlotData slotGameFish;

        public static com.esotericsoftware.spine.Skin skinDefault;
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

    public static class SpinePlatformStone {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static BoneData boneRoot;

        public static SlotData slotGamePlatformStone;

        public static SlotData slotBbox;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineShy {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationSpear;

        public static Animation animationStanding;

        public static Animation animationWalking;

        public static BoneData boneRoot;

        public static BoneData boneGameShy;

        public static BoneData boneGameShyFootRight;

        public static BoneData boneGameShyFootLeft;

        public static BoneData boneGameShy2;

        public static BoneData boneGameShyShield;

        public static BoneData boneGameShySpear;

        public static SlotData slotGameShyFootRight;

        public static SlotData slotGameShy;

        public static SlotData slotGameShyFootLeft;

        public static SlotData slotGameShyShield;

        public static SlotData slotGameShySpear;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineSkidDust {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static BoneData boneRoot;

        public static SlotData slotGameTrail01;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineZebra {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAim;

        public static Animation animationBlink;

        public static Animation animationDuck;

        public static Animation animationFull;

        public static Animation animationGulp;

        public static Animation animationHurt;

        public static Animation animationJump;

        public static Animation animationJumpAir;

        public static Animation animationJumpDouble;

        public static Animation animationJumpFall;

        public static Animation animationJumpLand;

        public static Animation animationLick;

        public static Animation animationLickBlocked;

        public static Animation animationLickUp;

        public static Animation animationLickUpBlocked;

        public static Animation animationNoBaby;

        public static Animation animationPush;

        public static Animation animationRun;

        public static Animation animationSkid;

        public static Animation animationSpit;

        public static Animation animationSpitUp;

        public static Animation animationStand;

        public static Animation animationTail;

        public static Animation animationTailRun;

        public static Animation animationWalk;

        public static Animation animationYum;

        public static BoneData boneRoot;

        public static BoneData boneTail1;

        public static BoneData boneTail2;

        public static BoneData boneTail3;

        public static BoneData boneTail4;

        public static BoneData boneTail5;

        public static BoneData boneTail6;

        public static BoneData boneHip;

        public static BoneData boneBody;

        public static BoneData boneLegRight;

        public static BoneData boneForelegRight;

        public static BoneData boneLegLeft;

        public static BoneData boneForelegLeft;

        public static BoneData boneHand;

        public static BoneData boneHead;

        public static BoneData boneSkelHip;

        public static BoneData boneSkelFootRight;

        public static BoneData boneSkelFootLeft;

        public static BoneData boneSkelTorso;

        public static BoneData boneSkelHandRight;

        public static BoneData boneSkelHandLeft;

        public static BoneData boneSkelHead;

        public static BoneData boneSkelHat;

        public static BoneData boneIkRight;

        public static BoneData boneIkLeft;

        public static BoneData boneFootLeft;

        public static BoneData boneFootRight;

        public static BoneData boneGameZebraTongue;

        public static BoneData boneGameZebraTongue2;

        public static BoneData boneGameZebraTongue3;

        public static BoneData boneGameZebraTongue4;

        public static BoneData boneGameZebraTongue5;

        public static BoneData boneGameZebraTongue6;

        public static BoneData boneGameZebraTongue7;

        public static BoneData boneGameZebraTongue8;

        public static BoneData boneGameZebraTongue9;

        public static BoneData boneGameZebraTongue1;

        public static BoneData boneSmokeTarget;

        public static BoneData boneFartTarget;

        public static SlotData slotGameZebraFootLeft;

        public static SlotData slotGameZebraTail;

        public static SlotData slotGameSkelFootLeft;

        public static SlotData slotGameZebraBodyBack;

        public static SlotData slotGameZebraHead;

        public static SlotData slotGameZebraBody;

        public static SlotData slotGameZebraDuck;

        public static SlotData slotGameZebraEyes;

        public static SlotData slotGameZebraFace;

        public static SlotData slotGameZebraFootRight;

        public static SlotData slotGameSkelHatBack;

        public static SlotData slotGameSkelHead;

        public static SlotData slotGameSkelHat;

        public static SlotData slotGameSkelHandLeft;

        public static SlotData slotGameSkelTorso;

        public static SlotData slotGameSkelHip;

        public static SlotData slotGameSkelFootRight;

        public static SlotData slotGameSkelHandRight;

        public static SlotData slotGameZebraTail1;

        public static SlotData slotGameTemp;

        public static SlotData slotGameZebraFaceFront;

        public static SlotData slotGameZebraTongue;

        public static SlotData slotGameZebraTongueTip;

        public static SlotData slotGameZebraTongue1;

        public static SlotData slotGameZebraHand;

        public static SlotData slotBbox;

        public static SlotData slotFootSensor;

        public static SlotData slotHeadSensor;

        public static SlotData slotRightSensor;

        public static SlotData slotLeftSensor;

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
        public static float playerTimeToRun = 1.0f;

        public static int playerStandDeceleration = 2500;

        public static int playerWalkAcceleration = 1500;

        public static int playerWalkDeceleration = 1500;

        public static int playerMaxWalkSpeed = 600;

        public static int playerRunAcceleration = 2000;

        public static int playerRunDeceleration = 2000;

        public static int playerMaxRunSpeed = 1200;

        public static int playerSkidThreshold = 650;

        public static float playerSmokeDelay = 0.1f;

        public static int playerStandSmokeMinSpeed = 500;

        public static int playerGravity = 3000;

        public static int playerMaxFallYspeed = 1500;

        public static int playerFallXacceleration = 1500;

        public static int playerMaxFallXspeed = 600;

        public static int playerJumpSpeed = 1500;

        public static int playerJumpXacceleration = 1500;

        public static int playerJumpXspeed = 600;

        public static int playerJumpYacceleration = 5000;

        public static float playerJumpExtraTime = 0.2f;

        public static int playerDoubleJumpYacceleration = 5000;

        public static float playerDoubleJumpTime = 0.5f;

        public static float playerDoubleJumpDelay = 1.0f;

        public static String name = "Raeleus";

        public static boolean godMode = true;

        public static int id = 10;
    }
}
