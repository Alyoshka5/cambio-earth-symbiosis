package com.cambio_earth.symbiosis.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;

@Service
public class SessionService {

    @Autowired
    private ParticipationRepository participationRepository;
    
    @Autowired
    private SessionRepository sessionRepository;

    // Get all breakout sessions
    public List<Session> getBreakoutSessions() {
        return sessionRepository.findByIsBreakoutTrue();
    }

    /* Register a user for multiple sessions */
    @Transactional
    public void registerUserForSessions(User user, List<Long> sessionIds) {
        
        // Clear existing registrations
        participationRepository.deleteByUserId(user.getId());
        
        // Register for new sessions
        for (Long sessionId : sessionIds) {
            Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
            
            Participation participation = new Participation(user, session);
            participationRepository.save(participation);
        }
    }

    /* Get all sessions a user is registered for */
    public List<Session> getUserSessions(User user) {
        return participationRepository.findByUser_Id(user.getId())
            .stream()
            .map(Participation::getSession)
            .collect(Collectors.toList());
    }

    /* Get all users registered in a session */
    public List<User> getUsersInSession(Long sessionId) {
        return participationRepository.findBySession_Id(sessionId)
            .stream()
            .map(Participation::getUser)
            .collect(Collectors.toList());
    }
    
    /* Check if user is registered for a session */
    public boolean isUserRegistered(Long userId, Long sessionId) {
        return participationRepository.existsByUser_IdAndSession_Id(userId, sessionId);
    }
}