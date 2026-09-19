package com.gls.construction.controllers;

import com.gls.construction.models.*;
import com.gls.construction.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/api/projects")
public class UploadController {
    private final ProjectRepository projects;
    private final Path root;
    public UploadController(ProjectRepository projects, @Value("${app.upload-dir:uploads}") String uploadDirectory) {
        this.projects = projects;
        this.root = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }
    @PostMapping("/{id}/media")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestParam MultipartFile file, @RequestParam MediaType mediaType, @RequestParam MediaCategory category, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return ResponseEntity.status(401).body(Map.of("error", "Admin login required"));
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (file.isEmpty() || file.getSize() > 25_000_000 || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) return ResponseEntity.badRequest().body(Map.of("error", "Only image/video files up to 25MB are allowed"));
        return projects.findById(id).map(project -> {
            try {
                Files.createDirectories(root);
                String name = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
                Files.copy(file.getInputStream(), root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                ProjectMedia media = new ProjectMedia(); media.setProject(project); media.setMediaType(mediaType); media.setCategory(category); media.setMediaUrl("/uploads/" + name); project.getMedia().add(media);
                return ResponseEntity.ok(projects.save(project));
            } catch (IOException ex) { return ResponseEntity.internalServerError().body(Map.of("error", "Upload failed")); }
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/progress-upload")
    public ResponseEntity<?> uploadProgress(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam LocalDate updateDate,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile video,
            HttpSession session) {
        if (session.getAttribute("adminUser") == null) return ResponseEntity.status(401).body(Map.of("error", "Admin login required"));
        if (title.isBlank()) return ResponseEntity.badRequest().body(Map.of("error", "Update title is required"));
        if (image != null && !validFile(image, "image/")) return ResponseEntity.badRequest().body(Map.of("error", "Please choose a valid image up to 25MB"));
        if (video != null && !validFile(video, "video/")) return ResponseEntity.badRequest().body(Map.of("error", "Please choose a valid video up to 25MB"));
        return projects.findById(id).map(project -> {
            try {
                ProgressUpdate update = new ProgressUpdate();
                update.setProject(project);
                update.setTitle(title.trim());
                update.setDescription(description.trim());
                update.setUpdateDate(updateDate);
                if (image != null && !image.isEmpty()) update.setImageUrl(saveFile(image));
                if (video != null && !video.isEmpty()) update.setVideoUrl(saveFile(video));
                project.getProgressUpdates().add(update);
                return ResponseEntity.ok(projects.save(project));
            } catch (IOException ex) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Progress upload failed"));
            }
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean validFile(MultipartFile file, String expectedType) {
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        return !file.isEmpty() && file.getSize() <= 25_000_000 && contentType.startsWith(expectedType);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Files.createDirectories(root);
        String name = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
        Files.copy(file.getInputStream(), root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + name;
    }
}