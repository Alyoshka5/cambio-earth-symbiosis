package com.cambio_earth.symbiosis.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findBySession(Session session);
    List<Participation> findBySessionAndUser(Session session, User user);
    Optional<Participation> findFirstBySessionAndUser(Session session, User user);
}
