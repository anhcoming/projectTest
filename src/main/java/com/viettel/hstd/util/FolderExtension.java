package com.viettel.hstd.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties
public class FolderExtension {

    @Value("${app.store.path-store-media}")
    String uploadStore;

    public String getUploadFolder() {
        final Path fileStorageLocation = Paths.get(uploadStore)
                .toAbsolutePath().normalize();
        try {
            if (!Files.exists(fileStorageLocation))
                Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("file.directory_create");
        }
        return uploadStore;
    }

    public String getFileName(String filePath) {
        String fileTemp = filePath.replaceAll(uploadStore, "").replaceAll("/", "-");
        if (fileTemp.startsWith("-")) {
            return fileTemp.substring(1);
        }
        return fileTemp;
    }
}
