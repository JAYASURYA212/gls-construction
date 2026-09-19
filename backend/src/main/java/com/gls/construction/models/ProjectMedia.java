package com.gls.construction.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "project_media")
public class ProjectMedia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id", nullable = false) @JsonBackReference private Project project;
    @Enumerated(EnumType.STRING) @Column(name = "media_type", nullable = false) private MediaType mediaType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MediaCategory category;
    private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "media_url", nullable = false) private String mediaUrl;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; }
    public Project getProject() { return project; }
    public void setProject(Project v) { project = v; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType v) { mediaType = v; }
    public MediaCategory getCategory() { return category; }
    public void setCategory(MediaCategory v) { category = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String v) { mediaUrl = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}