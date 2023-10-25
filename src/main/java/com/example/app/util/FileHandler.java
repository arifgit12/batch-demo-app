package com.example.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileHandler {

    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    public static File getFile(String sourceDirectory) {
        logger.info("Directory Name " + sourceDirectory);
        File sourceDir = new File(sourceDirectory);

        if (sourceDir.exists() && sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    return file;
                }
            }
        }
        return null;
    }

    public static void moveFile(File source, File destination) {
        try {
            Path sourcePath = source.toPath();
            Path destinationPath = destination.toPath();

            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File moved {} successfully", destination.getName());
        } catch (IOException e) {
            logger.error("Failed to move the file: {}", e.getMessage());
        }
    }
}
