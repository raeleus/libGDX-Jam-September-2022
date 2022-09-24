package com.ray3k.template.listupdater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ListUpdaterLite {
    private static String levelFolderPath = "C:/onedrive/workspace/Jam Games/libGDX Jam September 2022/design/output/levels";
    
    public static void main(String args[]) throws IOException {
        if (levelFolderPath != null) {
            var sourcePath = Paths.get(levelFolderPath);
            Files.walk(sourcePath).forEach(source -> {
                        Path destination = Paths.get("assets/levels/", source.toString().substring(levelFolderPath.length()));
                        try {
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
