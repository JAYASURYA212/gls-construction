package com.gls.construction.controllers;

import com.gls.construction.models.*;
import com.gls.construction.repositories.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository projects;
    public ProjectController(ProjectRepository projects) { this.projects = projects; }
    @GetMapping public List<Project> all() { return projects.findAllByOrderByCreatedAtDesc(); }
    @GetMapping("/{id}") public ResponseEntity<Project> one(@PathVariable Long id) { return projects.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PostMapping public ResponseEntity<?> create(@RequestBody Project project, HttpSession session) { if (!admin(session)) return unauthorized(); return ResponseEntity.ok(projects.save(project)); }
    @PutMapping("/{id}") public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Project input, HttpSession session) {
        if (!admin(session)) return unauthorized();
        return projects.findById(id).map(project -> { project.setName(input.getName()); project.setProjectType(input.getProjectType()); project.setLocation(input.getLocation()); project.setBuiltUpArea(input.getBuiltUpArea()); project.setDescription(input.getDescription()); project.setStatus(input.getStatus()); return ResponseEntity.ok(projects.save(project)); }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) { if (!admin(session)) return unauthorized(); projects.deleteById(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/progress") public ResponseEntity<?> progress(@PathVariable Long id, @RequestBody ProgressUpdate input, HttpSession session) {
        if (!admin(session)) return unauthorized();
        return projects.findById(id).map(project -> { input.setProject(project); project.getProgressUpdates().add(input); return ResponseEntity.ok(projects.save(project)); }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    private boolean admin(HttpSession session) { return session.getAttribute("adminUser") != null; }
    private ResponseEntity<Map<String, String>> unauthorized() { return ResponseEntity.status(401).body(Map.of("error", "Admin login required")); }
}