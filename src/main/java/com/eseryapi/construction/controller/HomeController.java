package com.eseryapi.construction.controller;

import com.eseryapi.construction.service.PartnerService;
import com.eseryapi.construction.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

@Controller
public class HomeController {

    private static final int FOUNDING_YEAR = 2007;

    private final ProjectService projectService;
    private final PartnerService partnerService;

    public HomeController(ProjectService projectService, PartnerService partnerService) {
        this.projectService = projectService;
        this.partnerService = partnerService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("yearsOfExperience", Year.now().getValue() - FOUNDING_YEAR);
        model.addAttribute("recentProjects", projectService.findAll().stream().limit(3).toList());
        model.addAttribute("projectCount", projectService.count());
        model.addAttribute("partners", partnerService.findAll());
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("yearsOfExperience", Year.now().getValue() - FOUNDING_YEAR);
        return "about";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/partners")
    public String partners(Model model) {
        model.addAttribute("partners", partnerService.findAll());
        return "partners";
    }
}
