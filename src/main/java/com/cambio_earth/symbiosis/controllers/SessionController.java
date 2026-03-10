package com.cambio_earth.symbiosis.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.services.SessionService;

@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;

    private Map<String, List<String>> sessionRegistrations = new HashMap<>();

   @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(@AuthenticationPrincipal User user, Model model) {
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        model.addAttribute("sessions", breakoutSessions);
        model.addAttribute("isAdmin", user.getRole().equals(Role.ADMIN));
        model.addAttribute("isAdmin", user != null && user.getRole().equals(Role.ADMIN));
        return "sessions/breakoutRoomPreferences";
    }

    // register user for sessions
    @PostMapping("/sessions/register")
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

        return "redirect:/sessions/thankYou";
    }

    @GetMapping("/sessions/thankYou")
    public String getThankYouPage() {
        return "sessions/thankYou";
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

    @GetMapping("/sessions/schedule")
    public String getSchedulePage(@AuthenticationPrincipal User user, Model model) {
        Map<String, List<Session>> schedule = sessionService.getUserSchedule(user);
        model.addAttribute("schedule", schedule);
        model.addAttribute("isAdmin", user.getRole().equals(Role.ADMIN));

        return "sessions/eventSchedule";
    }
}