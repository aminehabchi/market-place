package com.example.media.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
@EnableFilesystemStores
public class ApplicationConfig {

    // Temporary folder for storing files (you can change to a fixed path)
    @Bean
    public File filesystemRoot() {
        try {
            return Files.createTempDirectory("spring-content").toFile();
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to create temp directory", ioe);
        }
    }

    // FileSystemResourceLoader bean for Spring Content FS
    @Bean
    public FileSystemResourceLoader fileSystemResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }

}