package com.cambio_earth.symbiosis.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;

@Service
public class SessionService {

    // teammate's signup/remove logic
    private Map<String, Set<User>> sessionRegistrations = new HashMap<>();

    @Autowired
    private SessionRepository sessionRepository;

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
}