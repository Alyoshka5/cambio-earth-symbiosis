package com.cambio_earth.symbiosis.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CompletedMissionsRepository extends JpaRepository<CompletedMissions,Long>{
    List<CompletedMissions> findByUser(User user);
    Optional<CompletedMissions> findByUserAndId(User user, Long id);
}