package com.eseryapi.construction.controller.admin;

import com.eseryapi.construction.service.ContactService;
import com.eseryapi.construction.service.PartnerService;
import com.eseryapi.construction.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProjectService projectService;
    private final PartnerService partnerService;
    private final ContactService contactService;

    public AdminController(ProjectService projectService, PartnerService partnerService, ContactService contactService) {
        this.projectService = projectService;
        this.partnerService = partnerService;
        this.contactService = contactService;
    }

    @GetMapping("")
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("projectCount", projectService.count());
        model.addAttribute("partnerCount", partnerService.findAll().size());
        model.addAttribute("unreadMessages", contactService.countUnread());
        model.addAttribute("recentMessages", contactService.findAll().stream().limit(5).toList());
        return "admin/dashboard";
    }
}
