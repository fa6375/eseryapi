package com.eseryapi.construction.controller.admin;

import com.eseryapi.construction.model.Partner;
import com.eseryapi.construction.service.PartnerService;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/partners")
public class AdminPartnerController {

    private final PartnerService partnerService;

    public AdminPartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("partners", partnerService.findAll());
        return "admin/partners";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("partner", new Partner());
        model.addAttribute("isEdit", false);
        return "admin/partner-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return partnerService.findById(id)
                .map(partner -> {
                    model.addAttribute("partner", partner);
                    model.addAttribute("isEdit", true);
                    return "admin/partner-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Tərəfdaş tapılmadı.");
                    return "redirect:/admin/partners";
                });
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Partner partner,
                        @RequestParam(value = "logo", required = false) MultipartFile logo,
                        RedirectAttributes redirectAttributes) {
        if (partner.getId() != null) {
            partnerService.findById(partner.getId()).ifPresent(existing -> {
                if (logo == null || logo.isEmpty()) {
                    partner.setLogoPath(existing.getLogoPath());
                }
            });
        }
        partnerService.save(partner, logo);
        redirectAttributes.addFlashAttribute("success", "Tərəfdaş \"" + partner.getName() + "\" uğurla saxlanıldı.");
        return "redirect:/admin/partners";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        partnerService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Tərəfdaş silindi.");
        return "redirect:/admin/partners";
    }
}
