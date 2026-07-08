package com.eseryapi.construction.controller;

import com.eseryapi.construction.model.Project;
import com.eseryapi.construction.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("projects", projectService.findByCategory(category));
        model.addAttribute("categories", projectService.distinctCategories());
        model.addAttribute("activeCategory", (category == null || category.isBlank()) ? "All" : category);
        return "projects";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return projectService.findById(id)
                .map(project -> {
                    model.addAttribute("project", project);
                    return "project-detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "That project could not be found.");
                    return "redirect:/projects";
                });
    }
}
