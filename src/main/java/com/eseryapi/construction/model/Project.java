package com.eseryapi.construction.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /** e.g. Residential, Commercial, Facade & Curtain Wall, Infrastructure */
    private String category;

    /** Free-text location, e.g. "Yasamal District, Baku, Azerbaijan" */
    private String location;

    /** Optional coordinates so the project can be pinned on a map later. */
    private Double latitude;
    private Double longitude;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String description;

    @Column(name = "project_year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.COMPLETED;

    /** Optional: total facade / floor area in square meters. */
    private Integer areaSqm;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC, id ASC")
    private List<ProjectImage> images = new ArrayList<>();

    public Project() {
    }

    // ---- getters / setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Integer getAreaSqm() {
        return areaSqm;
    }

    public void setAreaSqm(Integer areaSqm) {
        this.areaSqm = areaSqm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProjectImage> getImages() {
        return images;
    }

    public void setImages(List<ProjectImage> images) {
        this.images = images;
    }

    /** Convenience for templates: first image, or null if none uploaded yet. */
    @Transient
    public ProjectImage getCoverImage() {
        return images.isEmpty() ? null : images.get(0);
    }

    public void addImage(ProjectImage image) {
        images.add(image);
        image.setProject(this);
    }
}
