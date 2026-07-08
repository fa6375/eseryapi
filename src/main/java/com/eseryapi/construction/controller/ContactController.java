package com.eseryapi.construction.controller;

import com.eseryapi.construction.model.ContactMessage;
import com.eseryapi.construction.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contactForm(Model model) {
        if (!model.containsAttribute("contactMessage")) {
            model.addAttribute("contactMessage", new ContactMessage());
        }
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactMessage") ContactMessage contactMessage,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact";
        }
        contactService.save(contactMessage);
        redirectAttributes.addFlashAttribute("success",
                "Təşəkkür edirik, " + contactMessage.getName() + " — mesajınız göndərildi. Tezliklə sizinlə əlaqə saxlayacağıq.");
        return "redirect:/contact";
    }
}
