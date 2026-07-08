package com.eseryapi.construction.controller.admin;

import com.eseryapi.construction.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {

    private final ContactService contactService;

    public AdminMessageController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("messages", contactService.findAll());
        return "admin/messages";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.markRead(id);
        return "redirect:/admin/messages";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Mesaj silindi.");
        return "redirect:/admin/messages";
    }
}
