package com.gls.construction.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(name = "project_type") private String projectType;
    private String location;
    @Column(name = "built_up_area") private Integer builtUpArea;
    @Column(columnDefinition = "TEXT") private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ProjectStatus status = ProjectStatus.ONGOING;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt = LocalDateTime.now();
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference private List<ProjectMedia> media = new ArrayList<>();
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference private List<ProgressUpdate> progressUpdates = new ArrayList<>();
    @PreUpdate public void touch() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String v) { projectType = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { location = v; }
    public Integer getBuiltUpArea() { return builtUpArea; }
    public void setBuiltUpArea(Integer v) { builtUpArea = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus v) { status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ProjectMedia> getMedia() { return media; }
    public List<ProgressUpdate> getProgressUpdates() { return progressUpdates; }
}