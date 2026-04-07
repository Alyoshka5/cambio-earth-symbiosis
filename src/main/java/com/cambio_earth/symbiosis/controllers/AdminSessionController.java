package com.cambio_earth.symbiosis.controllers;

import com.cambio_earth.symbiosis.services.EventService;
import com.cambio_earth.symbiosis.services.SessionService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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

    private final EventService eventService;

    private final SessionService sessionService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ParticipationRepository participationRepository;

    public AdminSessionController(SessionService sessionService, EventService eventService) {
        this.sessionService = sessionService;
        this.eventService = eventService;
    }

    // Show blank form (create new)
    @GetMapping("/admin/sessions/new")
    public String getNewSessionForm(Model model) {
        Session session = new Session();
        session.setCapacity(100); // Default value for breakout sessions
        model.addAttribute("eventSession", session);

        // Navigation bar control 
        model.addAttribute("showSidebar", true);
        model.addAttribute("currentPage", "schedule");

        return "sessions/AdminSessionForm";
    }

    // Show pre-filled form (edit existing)
    @GetMapping("/admin/sessions/{id}/edit")
    public String getEditSessionForm(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        if (!session.isBreakout()) {
            session.setCapacity(100); // Default capacity for if session is set to breakout
        }
        model.addAttribute("eventSession", session);

        model.addAttribute("showSidebar", true);
        model.addAttribute("currentPage", "schedule");

        return "sessions/AdminSessionForm";
    }

    // Handle form submission (create or update)
    @PostMapping("/admin/sessions/save")
    public String saveSession(
            @ModelAttribute Session formSessionData,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String speakersRaw,
            @RequestParam(required = false) Integer capacity
        ) {

        Session session;

        if (formSessionData.getId() == null) {
            session = new Session();
        } else {
            Optional<Session> optionalSession = sessionRepository.findById(formSessionData.getId());
            if (optionalSession.isPresent()) {
                session = optionalSession.get();
            } else {
                session = new Session();
            }
        }

        // Validate data
        if (!formSessionData.getTitle().equals("")) {
            session.setTitle(formSessionData.getTitle());
        } else {
            session.setTitle("Default Title");
        }

        session.setDescription(formSessionData.getDescription());
        session.setLocation(formSessionData.getLocation());

        if (speakersRaw != null && !speakersRaw.isBlank()) {
            session.setSpeakers(new ArrayList<>(Arrays.asList(speakersRaw.split(","))));
        } else {
            session.setSpeakers(new ArrayList<>());
        }

        if (date != null && !date.isBlank() && startTime != null && !startTime.isBlank()) {
            session.setStartDateTime(LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(startTime)));
        } else {
            session.setStartDateTime(null);
        }

        if (date != null && !date.isBlank() && endTime != null && !endTime.isBlank()) {
            session.setEndDateTime(LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(endTime)));
        } else {
            session.setEndDateTime(null);
        }

        session.setBreakout(formSessionData.isBreakout());

        if (formSessionData.isBreakout()) {
            if (capacity != null && capacity > 0) {
                session.setCapacity(capacity);
            } else {
                session.setCapacity(100); // Default capacity if invalid capacity 
            }
        } else {
            session.setCapacity(null); // clear it if not a breakout
        }

        sessionRepository.save(session);
        return "redirect:/sessions/" + session.getId();
    }

    // Show session detail page
    @GetMapping("/sessions/{id}")
    public String getSessionDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("eventSession", session);

        boolean isAdmin = currentUser != null && currentUser.getRole() == Role.ADMIN;
        model.addAttribute("isAdmin", isAdmin);

        // Navigation bar control (only show for admin)
        if (isAdmin) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("currentPage", "schedule");
        } else {
            model.addAttribute("showSidebar", false);
        }

        return "sessions/sessionDetails";
    }

    // Remove a user from a session
    @PostMapping("/remove/{uid}/fromSession/{sid}")
    public String removeUserFromSession(@PathVariable Long uid, @PathVariable Long sid, RedirectAttributes redirectAttributes) {
        
        try {
            Session session = sessionRepository.findById(sid).orElseThrow();
            User user = userRepository.findById(uid).orElseThrow();
            
            Optional<Participation> participation = participationRepository.findFirstBySessionAndUser(session, user);

            if (participation.isPresent()) {
                participationRepository.delete(participation.get());
            }

        } catch (Exception err) {
            redirectAttributes.addFlashAttribute("err", "Could not remove user from the session: " + err.getMessage());
        }
        
        return "redirect:/sessions/" + sid;
    }

    // Delete (admin only)
    @PostMapping("/admin/sessions/{id}/delete")
    public String deleteSession(@PathVariable("id") Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/sessions/schedule";
    }

    @PostMapping("/launch")
    public String launchEvent(RedirectAttributes redirectAttributes) {
        sessionService.registerUsersForMandatorySessions();
        sessionService.registerUsersForBreakoutSessions();

        if (eventService.launchEvent()) {
            redirectAttributes.addFlashAttribute("launchSuccess", "Event has successfully been launched!");
        } else {
            redirectAttributes.addFlashAttribute("launchErr", "Event could not be launched. Please try again.");
        }
        return "redirect:/sessions/schedule";
    }
}