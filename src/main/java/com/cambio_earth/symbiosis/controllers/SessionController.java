package com.cambio_earth.symbiosis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;

@Controller
public class SessionController {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

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
                
                // Save the new participation
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
}