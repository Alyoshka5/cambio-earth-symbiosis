package com.cambio_earth.symbiosis.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VenueMapRepository extends JpaRepository<VenueMap, Long> {
    List<VenueMap> findAllByPublishedTrue();
}