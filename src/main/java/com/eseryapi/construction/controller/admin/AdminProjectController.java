package com.eseryapi.construction.controller.admin;

import com.eseryapi.construction.model.Project;
import com.eseryapi.construction.model.ProjectStatus;
import com.eseryapi.construction.service.ProjectService;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** Lets optional numeric fields (year, area, coordinates) be submitted blank without a binding error. */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "admin/projects";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("isEdit", false);
        return "admin/project-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return projectService.findById(id)
                .map(project -> {
                    model.addAttribute("project", project);
                    model.addAttribute("statuses", ProjectStatus.values());
                    model.addAttribute("isEdit", true);
                    return "admin/project-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Layihə tapılmadı.");
                    return "redirect:/admin/projects";
                });
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Project project,
                        @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
                        RedirectAttributes redirectAttributes) {
        projectService.save(project, newImages);
        redirectAttributes.addFlashAttribute("success", "Layihə \"" + project.getTitle() + "\" uğurla saxlanıldı.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Layihə silindi.");
        return "redirect:/admin/projects";
    }

    @PostMapping("/{projectId}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long projectId, @PathVariable Long imageId,
                               RedirectAttributes redirectAttributes) {
        projectService.removeImage(projectId, imageId);
        redirectAttributes.addFlashAttribute("success", "Şəkil silindi.");
        return "redirect:/admin/projects/" + projectId + "/edit";
    }
}
