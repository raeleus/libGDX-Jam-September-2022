package com.ray3k.template.listupdater;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.*;
import com.ray3k.stripe.FreeTypeSkinLoader;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;

public class ListUpdater {
    private static String levelFolderPath = System.getProperty("user.dir") + "/design/output/levels";
    private final static LameDuckAttachmentLoader lameDuckAttachmentLoader = new LameDuckAttachmentLoader();
    
    public static void main(String args[]) {
        new Lwjgl3Application(new ApplicationAdapter() {
            @Override
            public void create() {
                System.out.println("Check if lists need to be updated.");
                
                var resources = new Array<ResourceDescriptor>();
                
                boolean updated = createList("textures", Paths.get("assets/textures.txt").toFile(), TextureAtlas.class, resources, "atlas");
                updated |= createList("spine", Paths.get("assets/spine.txt").toFile(), SkeletonData.class, resources, "json");
                updated |= createList("skin", Paths.get("assets/skin.txt").toFile(), Skin.class, resources, "json");
                updated |= createList("sfx", Paths.get("assets/sfx.txt").toFile(), Sound.class, resources, "ogg", "mp3", "wav");
                updated |= createList("bgm", Paths.get("assets/bgm.txt").toFile(), Music.class, resources, "ogg", "mp3", "wav");
                
                writeResources(resources, Paths.get("core/src/main/java/com/ray3k/template/Resources.java").toFile(), new FileHandle(Paths.get("assets/data").toFile()));
                
                if (updated) {
                    System.out.println("Updated lists.");
                } else {
                    System.out.println("Lists not updated.");
                }
    
                java.awt.Toolkit.getDefaultToolkit().beep();
                Gdx.app.exit();
            }
        }, new Lwjgl3ApplicationConfiguration());
    }
    
    private static boolean createList(String folderName, File outputPath, Class type, Array<ResourceDescriptor> resources, String... extensions) {
        boolean changed = false;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            String digest = outputPath.exists() ? getFileChecksum(md5Digest, outputPath) : "";
            Array<FileHandle> files = new Array<>();
            
            File directory = new File("./assets/" + folderName + "/");
            files.addAll(createList(directory, extensions));
            
            String outputText = "";
            for (int i = 0; i < files.size; i++) {
                FileHandle fileHandle = files.get(i);
                outputText += fileHandle.path().replace("./assets/", "");
                if (i < files.size - 1) {
                    outputText += "\n";
                }
                
                resources.add(new ResourceDescriptor(type, fileHandle));
            }
            if (!outputText.equals("")) {
                Files.writeString(outputPath.toPath(), outputText);
            } else {
                outputPath.delete();
            }
            changed = !getFileChecksum(md5Digest, outputPath).equals(digest);
        } catch (Exception e) {
        
        }
        return changed;
    }
    
    private static Array<FileHandle> createList(File folder, String... extensions) {
        Array<FileHandle> files = new Array<>();
        
        if (folder.listFiles() != null) for (File file : folder.listFiles()) {
            if (file.isFile()) {
                for (var extension : extensions) {
                    if (file.getPath().toLowerCase().endsWith(extension.toLowerCase())) {
                        files.add(new FileHandle(file));
                    }
                }
            } else {
                files.addAll(createList(file, extensions));
            }
        }
        return files;
    }
    
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);
        
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        
        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
        
        //close the stream; We don't need it now.
        fis.close();
        
        //Get the hash's bytes
        byte[] bytes = digest.digest();
        
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        //return complete hash
        return sb.toString();
    }
    
    private static void writeResources(Array<ResourceDescriptor> resources, File resourcesFile, FileHandle dataPath) {
        var methodSpecBuilder = MethodSpec.methodBuilder("loadResources")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(AssetManager.class, "assetManager");
        var subTypes = new Array<TypeSpec>();
        String textureAtlasPath = "";
        for (var resource : resources) {
            if (resource.type.equals(TextureAtlas.class)) textureAtlasPath = sanitizePath(resource.file.path());
            if (resource.type.equals(SkeletonData.class)) {
                var name = sanitizeVariableName(resource.file.nameWithoutExtension());
                name = "Spine" + upperCaseFirstLetter(name);
                methodSpecBuilder.addStatement("$L.skeletonData = assetManager.get($S)", name, sanitizePath(resource.file.path()));
                methodSpecBuilder.addStatement("$L.animationData = assetManager.get($S)", name, sanitizePath(resource.file.path()) + "-animation");
                
                var typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                
                typeSpecBuilder.addField(SkeletonData.class, "skeletonData", Modifier.PUBLIC, Modifier.STATIC);
                typeSpecBuilder.addField(AnimationStateData.class, "animationData", Modifier.PUBLIC, Modifier.STATIC);
                
                SkeletonJson skeletonJson = new SkeletonJson(lameDuckAttachmentLoader);
                var skeletonData = skeletonJson.readSkeletonData(resource.file);
                for (var animation : skeletonData.getAnimations()) {
                    var variableName = "animation" + upperCaseFirstLetter(sanitizeVariableName(animation.getName()));
                    typeSpecBuilder.addField(Animation.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
                    
                    methodSpecBuilder.addStatement("$L.$L = $L.skeletonData.findAnimation($S)", name, variableName, name, animation.getName());
                }
    
                for (var event : skeletonData.getEvents()) {
                    var variableName = "event" + upperCaseFirstLetter(sanitizeVariableName(event.getName()));
                    typeSpecBuilder.addField(EventData.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
        
                    methodSpecBuilder.addStatement("$L.$L = $L.skeletonData.findEvent($S)", name, variableName, name, event.getName());
                }
    
                for (var bone : skeletonData.getBones()) {
                    var variableName = "bone" + upperCaseFirstLetter(sanitizeVariableName(bone.getName()));
                    typeSpecBuilder.addField(BoneData.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
        
                    methodSpecBuilder.addStatement("$L.$L = $L.skeletonData.findBone($S)", name, variableName, name, bone.getName());
                }
    
                for (var slot : skeletonData.getSlots()) {
                    var variableName = "slot" + upperCaseFirstLetter(sanitizeVariableName(slot.getName()));
                    typeSpecBuilder.addField(SlotData.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
        
                    methodSpecBuilder.addStatement("$L.$L = $L.skeletonData.findSlot($S)", name, variableName, name, slot.getName());
                }
                
                for (var skin : skeletonData.getSkins()) {
                    var variableName = "skin" + upperCaseFirstLetter(sanitizeVariableName(skin.getName()));
                    typeSpecBuilder.addField(com.esotericsoftware.spine.Skin.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
                    
                    methodSpecBuilder.addStatement("$L.$L = $L.skeletonData.findSkin($S)", name, variableName, name, skin.getName());
                }
                
                subTypes.add(typeSpecBuilder.build());
            }
            else if (resource.type.equals(Skin.class)) {
                methodSpecBuilder.addStatement("$L = assetManager.get($S)", resource.variableName, sanitizePath(resource.file.path()));
                var className = camelCaseUpper(resource.variableName) + "Styles";
                var typeSpecBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                
                AssetManager assetManager = new AssetManager();
                assetManager.setLoader(Skin.class, new FreeTypeSkinLoader(assetManager.getFileHandleResolver()));
                var skinPath = sanitizePath(resource.file.path());
                assetManager.load(skinPath, Skin.class, new SkinParameter(textureAtlasPath));
                assetManager.finishLoading();
                Skin skin = assetManager.get(skinPath);
                var classes = new Class[] {ButtonStyle.class, CheckBoxStyle.class, ImageButtonStyle.class,
                        ImageTextButtonStyle.class, LabelStyle.class, ListStyle.class, ProgressBarStyle.class,
                        ScrollPaneStyle.class, SelectBoxStyle.class, SliderStyle.class, SplitPaneStyle.class,
                        TextButtonStyle.class, TextFieldStyle.class, TextTooltipStyle.class, TouchpadStyle.class,
                        TreeStyle.class, WindowStyle.class};
                var abbreviations = new String[] {"b", "cb", "ib", "itb", "l", "lst", "p", "sp", "sb", "s", "splt", "tb", "tf", "tt", "ts", "t", "w"};
                for (int i = 0; i < classes.length; i++) {
                    var styles = skin.getAll(classes[i]);
                    if (styles != null) for (Object object : styles.entries()) {
                        var entry = (Entry) object;
                        var variableName = abbreviations[i] + upperCaseFirstLetter(sanitizeVariableName((String) entry.key));
                        typeSpecBuilder.addField(classes[i], variableName, Modifier.PUBLIC, Modifier.STATIC);
                        methodSpecBuilder.addStatement("$L.$L = $L.get($S, $T.class)", className, variableName, resource.variableName, entry.key, classes[i]);
                    }
                }
                
                subTypes.add(typeSpecBuilder.build());
            }
            else {
                methodSpecBuilder.addStatement("$L = assetManager.get($S)", resource.variableName, sanitizePath(resource.file.path()));
            }
        }
    
        for (var dataFile : dataPath.list()) {
            subTypes.add(readDataFile(dataFile));
        }
        
        var methodSpec = methodSpecBuilder.build();
        
        var typeSpecBuilder = TypeSpec.classBuilder("Resources")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec);
        for (var resource : resources) {
            if (!resource.type.equals(SkeletonData.class)) typeSpecBuilder.addField(resource.type, resource.variableName, Modifier.PUBLIC, Modifier.STATIC);
        }
        for (var subType : subTypes) {
            typeSpecBuilder.addType(subType);
        }
        
        var typeSpec = typeSpecBuilder.build();
        
        var javaFile = JavaFile.builder("com.ray3k.template", typeSpec)
                .indent("    ")
                .build();
        
        try {
            Files.writeString(resourcesFile.toPath(), javaFile.toString());
        } catch (Exception e) {}
    
        if (levelFolderPath != null) {
            var sourcePath = Paths.get(levelFolderPath);
            try {
                Files.walk(sourcePath).filter(Files::isRegularFile).forEach(source -> {
                    Path destination = Paths.get(System.getProperty("user.dir") + "/assets/levels/" + source.toString().substring(levelFolderPath.length()));
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static final class ResourceDescriptor {
        Class type;
        FileHandle file;
        String variableName;
        
        public ResourceDescriptor(Class type, FileHandle file) {
            this.type = type;
            this.file = file;
            variableName = sanitizeResourceName(file.pathWithoutExtension());
        }
        
        public ResourceDescriptor(Class type, FileHandle file, String variableName) {
            this.type = type;
            this.file = file;
            this.variableName = variableName;
        }
    }
    
    private static String sanitizeResourceName(String name) {
        name = name.replaceAll("[\\\\/\\-\\s]", "_").replaceAll("['\"]", "");
        var splits = name.split("_");
        var builder = new StringBuilder(splits[2]);
        builder.append("_");
        if (splits.length >= 4) {
            builder.append(splits[3]);
        }
        
        for (int i = 4; i < splits.length; i++) {
            var split = splits[i];
            builder.append(Character.toUpperCase(split.charAt(0)));
            builder.append(split.substring(1));
        }
        
        return builder.toString();
    }
    
    private static String sanitizeVariableName(String name) {
        name = name.replaceAll("^[./]*", "").replaceAll("[\\\\/\\-\\s]", "_").replaceAll("['\"]", "");
        var splits = name.split("_");
        var builder = new StringBuilder(splits[0]);
        for (int i = 1; i < splits.length; i++) {
            var split = splits[i];
            builder.append(Character.toUpperCase(split.charAt(0)));
            builder.append(split.substring(1));
        }
        
        return builder.toString();
    }
    
    private static String sanitizePath(String path) {
        return path.replaceAll("\\./assets/", "");
    }
    
    private static String upperCaseFirstLetter(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
    
    private static String camelCaseUpper(String string) {
        String[] words = string.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            builder.append(word);
        }
        return builder.toString();
    }
    
    private static String camelCaseLower(String string) {
        String[] words = string.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }
    
    private static class LameDuckAttachmentLoader implements AttachmentLoader {
        @Override
        public RegionAttachment newRegionAttachment(com.esotericsoftware.spine.Skin skin, String name, String path,
                                                    Sequence sequence) {
            return new RegionAttachment(name);
        }
    
        @Override
        public MeshAttachment newMeshAttachment(com.esotericsoftware.spine.Skin skin, String name, String path,
                                                Sequence sequence) {
            return new MeshAttachment(name);
        }
    
        @Override
        public BoundingBoxAttachment newBoundingBoxAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new BoundingBoxAttachment(name);
        }
    
        @Override
        public ClippingAttachment newClippingAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new ClippingAttachment(name);
        }
    
        @Override
        public PathAttachment newPathAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new PathAttachment(name);
        }
    
        @Override
        public PointAttachment newPointAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new PointAttachment(name);
        }
    }
    
    public static TypeSpec readDataFile(FileHandle file) {
        var typeSpecBuilder = TypeSpec.classBuilder(upperCaseFirstLetter(sanitizeVariableName(file.nameWithoutExtension())))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        var jsonReader = new JsonReader();
        var root = jsonReader.parse(file);
        
        var iter = root.iterator();
        while (iter.hasNext()) {
            var value = iter.next();
            if (value.isBoolean()) {
                var fieldSpec = FieldSpec.builder(Boolean.TYPE, sanitizeVariableName(value.name), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer("$L", value.asBoolean())
                        .build();
                typeSpecBuilder.addField(fieldSpec);
            } else if (value.isDouble()) {
                var fieldSpec = FieldSpec.builder(Float.TYPE, sanitizeVariableName(value.name), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer("$Lf", value.asFloat())
                        .build();
                typeSpecBuilder.addField(fieldSpec);
            } else if (value.isLong()) {
                var fieldSpec = FieldSpec.builder(Integer.TYPE, sanitizeVariableName(value.name), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer("$L", (int) value.asLong())
                        .build();
                typeSpecBuilder.addField(fieldSpec);
            } else if (value.isString()) {
                var fieldSpec = FieldSpec.builder(String.class, sanitizeVariableName(value.name), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer("$S", value.asString())
                        .build();
                typeSpecBuilder.addField(fieldSpec);
            }
        }
        
        return typeSpecBuilder.build();
    }
}