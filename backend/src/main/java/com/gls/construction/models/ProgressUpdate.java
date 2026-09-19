package com.gls.construction.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "progress_updates")
public class ProgressUpdate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id", nullable = false) @JsonBackReference private Project project;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "update_date", nullable = false) private LocalDate updateDate;
    @Column(name = "image_url") private String imageUrl;
    @Column(name = "video_url") private String videoUrl;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId() { return id; }
    public Project getProject() { return project; }
    public void setProject(Project v) { project = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public LocalDate getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDate v) { updateDate = v; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String v) { imageUrl = v; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String v) { videoUrl = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}