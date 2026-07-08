package com.eseryapi.construction.service;

import com.eseryapi.construction.model.Partner;
import com.eseryapi.construction.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final FileStorageService fileStorageService;

    public PartnerService(PartnerRepository partnerRepository, FileStorageService fileStorageService) {
        this.partnerRepository = partnerRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Partner> findAll() {
        return partnerRepository.findAllByOrderByDisplayOrderAscIdAsc();
    }

    public Optional<Partner> findById(Long id) {
        return partnerRepository.findById(id);
    }

    @Transactional
    public Partner save(Partner partner, MultipartFile logo) {
        if (logo != null && !logo.isEmpty()) {
            if (partner.getLogoPath() != null) {
                fileStorageService.delete(partner.getLogoPath());
            }
            partner.setLogoPath(fileStorageService.store(logo, "partners"));
        }
        return partnerRepository.save(partner);
    }

    @Transactional
    public void deleteById(Long id) {
        partnerRepository.findById(id).ifPresent(partner -> {
            fileStorageService.delete(partner.getLogoPath());
            partnerRepository.delete(partner);
        });
    }
}
