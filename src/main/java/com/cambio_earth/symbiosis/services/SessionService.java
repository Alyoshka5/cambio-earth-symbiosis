package com.cambio_earth.symbiosis.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.User;

@Service
public class SessionService {

    // Map of session name - users registered
    private Map<String, Set<User>> sessionRegistrations = new HashMap<>();


    /* Register a user for a session */
    public void registerUser(String sessionName, User user) {

        // create session entry if it doesn't exist
        sessionRegistrations.putIfAbsent(sessionName, new HashSet<>());

        // add the user
        sessionRegistrations.get(sessionName).add(user);
    }


    /* Remove a user from a session */
    public void unregisterUser(String sessionName, User user) {

        if (sessionRegistrations.containsKey(sessionName)) {
            sessionRegistrations.get(sessionName).remove(user);
        }
    }


    /* Get all users registered in a session */
    public Set<User> getUsersInSession(String sessionName) {
        return sessionRegistrations.getOrDefault(sessionName, new HashSet<>());
    }
}