package com.cambio_earth.symbiosis.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;

@Service
public class SessionService {
    private Map<String, Set<User>> sessionRegistrations = new HashMap<>();

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    private SessionRepository sessionRepository;

    // retrieve schedule of sessions grouped by day
    public Map<String, List<Session>> getUserSchedule(User user) {
        List<Participation> participations = participationRepository.findByUserId(user.getId());
        List<Session> sessions;
        if (user.getRole().equals(Role.ADMIN)) {
            sessions = sessionRepository.findAll();
        } else {
            sessions = new ArrayList<>();
            for (Participation participation : participations) {
                Optional<Session> optionalSession = sessionRepository.findById(participation.getSession().getId());
                if (optionalSession.isPresent()) {
                    sessions.add(optionalSession.get());
                }
            }
        }

        Collections.sort(sessions);

        // Group sessions by date
        Map<String, List<Session>> scheduleDays = new HashMap<>();
        if (!sessions.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE-MMM dd");

            LocalDateTime prevDateTime = sessions.get(0).getStartDateTime();

            List<Session> daySessions = new ArrayList<>();
            for (Session session: sessions) {
                if (prevDateTime.toLocalDate().equals(session.getStartDateTime().toLocalDate())) {
                    daySessions.add(session);
                } else {

                    scheduleDays.put(prevDateTime.format(formatter), daySessions);
                    prevDateTime = session.getStartDateTime();
                    daySessions = new ArrayList<>();
                    daySessions.add(session);
                }
            }
            scheduleDays.put(prevDateTime.format(formatter), daySessions); // Add last day
        }

        return scheduleDays;
    }

    // Register a user for a session
    public void registerUser(String sessionName, User user) {
        sessionRegistrations.putIfAbsent(sessionName, new HashSet<>());
        sessionRegistrations.get(sessionName).add(user);
    }

    // Remove a user from a session
    public void unregisterUser(String sessionName, User user) {
        if (sessionRegistrations.containsKey(sessionName)) {
            sessionRegistrations.get(sessionName).remove(user);
        }
    }

    // Get all users registered in a session
    public Set<User> getUsersInSession(String sessionName) {
        return sessionRegistrations.getOrDefault(sessionName, new HashSet<>());
    }

    // your part: fetch real breakout sessions from DB
    public List<Session> getBreakoutSessions() {
        return sessionRepository.findByIsBreakoutTrue();
    }

//     public Session getSessionById(Long id) {
//     return sessionRepository.findById(id).orElseThrow();
// }
}