package com.cambio_earth.symbiosis.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.services.SessionService;

@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;

    // teammate's current in-memory signup/remove map
    private Map<String, List<String>> sessionRegistrations = new HashMap<>();

    // your part: breakout page should show real breakout sessions
    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(Model model) {
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        model.addAttribute("sessions", breakoutSessions);
        return "sessions/breakoutRoomPreferences";
    }

    // register user for sessions
    @PostMapping("/sessions/register")
    @ResponseBody
    public String registerUser(
            @RequestParam(required = false) List<String> sessionName,
            @RequestParam String email
    ) {
        if (sessionName == null || sessionName.isEmpty()) {
            return "No sessions selected.";
        }

        for (String name : sessionName) {
            sessionRegistrations.putIfAbsent(name, new ArrayList<>());

            List<String> users = sessionRegistrations.get(name);

            if (!users.contains(email)) {
                users.add(email);
            }
        }

        return "User registered successfully";
    }

    // unregister user
    @PostMapping("/sessions/unregister")
    @ResponseBody
    public String unregisterUser(
            @RequestParam(required = false) List<String> sessionName,
            @RequestParam String email
    ) {
        if (sessionName == null || sessionName.isEmpty()) {
            return "No sessions selected.";
        }

        for (String name : sessionName) {
            if (sessionRegistrations.containsKey(name)) {
                List<String> users = sessionRegistrations.get(name);
                users.remove(email);
            }
        }

        return "User removed from selected sessions";
    }
}