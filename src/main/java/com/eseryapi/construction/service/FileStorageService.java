package com.eseryapi.construction.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Stores uploaded images on the local filesystem, under app.upload.dir
 * (e.g. ./uploads/projects, ./uploads/partners) and hands back the public
 * URL path that gets saved on the entity and rendered in Thymeleaf.
 */
@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation.resolve("projects"));
            Files.createDirectories(rootLocation.resolve("partners"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directories at " + rootLocation, e);
        }
    }

    /**
     * @param file      the uploaded file
     * @param subfolder "projects" or "partners"
     * @return public URL path such as /uploads/projects/&lt;uuid&gt;.jpg
     */
    public String store(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store an empty file");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        String filename = UUID.randomUUID() + extension;

        try (InputStream in = file.getInputStream()) {
            Path destination = rootLocation.resolve(subfolder).resolve(filename).normalize();
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + original, e);
        }

        return "/uploads/" + subfolder + "/" + filename;
    }

    /** Deletes a previously stored file given its public URL path (e.g. /uploads/projects/x.jpg). */
    public void delete(String publicPath) {
        if (publicPath == null || publicPath.isBlank()) {
            return;
        }
        try {
            String relative = publicPath.startsWith("/uploads/") ? publicPath.substring("/uploads/".length()) : publicPath;
            Path target = rootLocation.resolve(relative).normalize();
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // Non-fatal: an orphaned file on disk is not worth failing the request over.
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}
