package com.eseryapi.construction.repository;

import com.eseryapi.construction.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findAllByOrderByDisplayOrderAscIdAsc();
}
