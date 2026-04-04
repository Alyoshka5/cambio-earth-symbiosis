package com.cambio_earth.symbiosis.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface MissionRepository extends JpaRepository<Mission,Long>{
    List<Mission> findAll();
    Optional<Mission> findById(Long id);
    Optional<Mission> findByTitle(String title);
}