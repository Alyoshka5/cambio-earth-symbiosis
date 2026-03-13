package com.cambio_earth.symbiosis.models;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // Find all participation records for a specific user
    List<Participation> findByUser_Id(Long userId);
    
    // Find all participation records for a specific user with session details loaded
    @Query("SELECT p FROM Participation p JOIN FETCH p.session WHERE p.user.id = :userId")
    List<Participation> findByUser_IdWithSession(@Param("userId") Long userId);

    // Find all participation records for a specific session
    List<Participation> findBySession_Id(Long sessionId);
    
    // Check if a user is already registered for a specific session
    boolean existsByUser_IdAndSession_Id(Long userId, Long sessionId);
    
    // Find a specific participation record by session and user (used for removing users)
    Optional<Participation> findFirstBySessionAndUser(Session session, User user);
    
    // Alternative method name if you prefer (either one works)
    Optional<Participation> findBySessionAndUser(Session session, User user);
    
    // Delete all participations for a user
    @Modifying
    @Transactional
    @Query("DELETE FROM Participation p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // Count number of participants in a session
    Long countBySession_Id(Long sessionId);
}