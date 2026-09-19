package com.gls.construction.repositories;

import com.gls.construction.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findAllByOrderByCreatedAtDesc();
}