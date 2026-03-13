package com.cambio_earth.symbiosis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.services.SessionService;

@Controller
public class SessionController {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(@AuthenticationPrincipal User user, Model model) {
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        model.addAttribute("sessions", breakoutSessions);
        model.addAttribute("isAdmin", user.getRole().equals(Role.ADMIN));
        return "sessions/breakoutRoomPreferences";
    }

    @PostMapping("/sessions/register")
    @ResponseBody
    public String registerUser(
            @RequestParam(value = "sessionId", required = false) List<Long> sessionIds,
            @AuthenticationPrincipal User currentUser
    ) {
        System.out.println("Received session registration request");
        System.out.println("Session IDs: " + sessionIds);
        System.out.println("Current user: " + currentUser);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return "No sessions selected";
        }

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        if (user == null) {
            return "User not found";
        }

        try {
            // First, clear all existing registrations for this user
            List<Participation> existingParticipations = participationRepository.findByUser_Id(user.getId());
            System.out.println("Deleting " + existingParticipations.size() + " existing registrations");
            participationRepository.deleteAll(existingParticipations);

            // Then save the new selections
            int savedCount = 0;
            for (Long id : sessionIds) {
                Session session = sessionRepository.findById(id).orElse(null);
                
                if (session == null) {
                    System.out.println("Session not found with ID: " + id);
                    continue;
                }
                
                Participation participation = new Participation(user, session);
                participationRepository.save(participation);
                savedCount++;
                System.out.println("Saved participation for session: " + id);
            }

            return "Successfully registered for " + savedCount + " sessions";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error saving registrations: " + e.getMessage();
        }
    }

    @GetMapping("/sessions/thankYou")
    public String getThankYouPage() {
        return "sessions/thankYou";
    }

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
            // This would need to be implemented with database
            // For now, it's a placeholder
            System.out.println("Unregistering user from session: " + name);
        }
        return "User unregistered successfully";
    }
}