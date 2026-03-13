package com.cambio_earth.symbiosis.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;


@Controller
public class AdminSessionController {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ParticipationRepository participationRepository;

    // AdminSessionForm.html 

    // Show blank form (create new)
    @GetMapping("/admin/sessions/new")
    public String getNewSessionForm(Model model) {
        model.addAttribute("eventSession", new Session());
        return "sessions/adminSessionForm";
    }

    // Show pre-filled form (edit existing)
    @GetMapping("/admin/sessions/{id}/edit")
    public String getEditSessionForm(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("eventSession", session);
        return "sessions/adminSessionForm";
    }

    // Handle form submission (create or update)
    @PostMapping("/admin/sessions/save")
    public String saveSession(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String speakersRaw,
            @RequestParam(required = false) boolean breakout,
            @RequestParam(required = false) Long id
        ) {

        Session session;
        if (id == null) { // Creating new session
            session = new Session();
        } else { // Updating existing session
            Optional<Session> optionalSession = sessionRepository.findById(id);
            if (optionalSession.isPresent()) {
                session = optionalSession.get();
            } else {
                session = new Session();
            }
        }

        if (title != null && !title.equals("")) {
            session.setTitle(title);
        } else {
            session.setTitle("Session");
        }
        if (description != null && !description.equals("")) {
            session.setDescription(description);
        } else {
            session.setDescription("");
        }
        if (location != null && !location.equals("")) {
            session.setLocation(location);
        } else {
            session.setLocation("");
        }
        if (speakersRaw != null && !speakersRaw.isBlank()) {
            session.setSpeakers(Arrays.asList(speakersRaw.split(",")));
        } else {
            session.setSpeakers(new ArrayList<>());
        }
        if (date != null && !date.isBlank() && startTime != null && !startTime.isBlank()) {
            session.setStartDateTime(LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(startTime)));
        } else {
            session.setStartDateTime(LocalDateTime.now());
        }
        if (date != null && !date.isBlank() && endTime != null && !endTime.isBlank()) {
            session.setEndDateTime(LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(endTime)));
        } else {
            session.setEndDateTime(LocalDateTime.now());
        }
        session.setBreakout(breakout);

        sessionRepository.save(session);
        return "redirect:/sessions/" + session.getId();
    }

    // AddDetails.html

    // Show session detail page
    @GetMapping("/sessions/{id}")
    public String getSessionDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("eventSession", session);

        // Add an attribute for checking if the current logged in user is an admin
        boolean isAdmin = currentUser != null && currentUser.getRole() == Role.ADMIN;
        model.addAttribute("isAdmin", isAdmin);
        System.out.println(">>> currentUser is: " + currentUser);
        System.out.println(">>> session title is: " + session.getTitle());

        return "sessions/sessionDetails";
    }

    // Remove a user from a session
    @PostMapping("/remove/{uid}/fromSession/{sid}")
    public String removeUserFromSession(@PathVariable Long uid, @PathVariable Long sid, RedirectAttributes redirectAttributes) {
        
        try {
            // Get the session and user object
            Session session = sessionRepository.findById(sid).orElseThrow();
            User user = userRepository.findById(uid).orElseThrow();
            
            // Find the session the desired user is in within the participation table
            Optional<Participation> participation = participationRepository.findFirstBySessionAndUser(session, user);

            // Delete the session the user is in if found in the participations table
            if (participation.isPresent()) {
                participationRepository.delete(participation.get());
            }
        } catch (Exception err) {
            redirectAttributes.addFlashAttribute("err", "Could not remove user from the session: " + err.getMessage());
            System.out.println(">>> Could not remove user from the session" + err.getMessage());
        }
        
        return "redirect:/sessions/" + sid;
    }

    // Delete (admin only)
    @PostMapping("/admin/sessions/{id}/delete")
    public String deleteSession(@PathVariable Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/sessions/schedule";
    }
}