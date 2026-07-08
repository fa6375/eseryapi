package com.eseryapi.construction.service;

import com.eseryapi.construction.model.Project;
import com.eseryapi.construction.model.ProjectImage;
import com.eseryapi.construction.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;

    public ProjectService(ProjectRepository projectRepository, FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Project> findAll() {
        return projectRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Project> findByCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("All")) {
            return findAll();
        }
        return projectRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public Project save(Project incoming, List<MultipartFile> newImages) {
        Project target;
        if (incoming.getId() != null) {
            // Load the managed entity and copy scalar fields onto it, rather than persisting the
            // detached object built by form binding directly — this keeps the existing (lazy-loaded)
            // images collection intact and avoids merging a half-built object graph.
            target = projectRepository.findById(incoming.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + incoming.getId()));
            target.setTitle(incoming.getTitle());
            target.setCategory(incoming.getCategory());
            target.setLocation(incoming.getLocation());
            target.setLatitude(incoming.getLatitude());
            target.setLongitude(incoming.getLongitude());
            target.setDescription(incoming.getDescription());
            target.setYear(incoming.getYear());
            target.setStatus(incoming.getStatus());
            target.setAreaSqm(incoming.getAreaSqm());
        } else {
            target = incoming;
        }

        Project saved = projectRepository.save(target);
        if (newImages != null) {
            int order = saved.getImages().size();
            for (MultipartFile file : newImages) {
                if (file != null && !file.isEmpty()) {
                    String path = fileStorageService.store(file, "projects");
                    ProjectImage image = new ProjectImage(path);
                    image.setDisplayOrder(order++);
                    saved.addImage(image);
                }
            }
            saved = projectRepository.save(saved);
        }
        return saved;
    }

    @Transactional
    public void removeImage(Long projectId, Long imageId) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.getImages().removeIf(img -> {
                boolean match = img.getId().equals(imageId);
                if (match) {
                    fileStorageService.delete(img.getImagePath());
                }
                return match;
            });
            projectRepository.save(project);
        });
    }

    @Transactional
    public void deleteById(Long id) {
        projectRepository.findById(id).ifPresent(project -> {
            project.getImages().forEach(img -> fileStorageService.delete(img.getImagePath()));
            projectRepository.delete(project);
        });
    }

    public long count() {
        return projectRepository.count();
    }

    /** Distinct category labels currently in use, for building filter tabs. */
    public List<String> distinctCategories() {
        return projectRepository.findAll().stream()
                .map(Project::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .toList();
    }
}
