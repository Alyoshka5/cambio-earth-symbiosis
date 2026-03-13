package com.cambio_earth.symbiosis.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.JwtService;
import com.cambio_earth.symbiosis.services.SessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private ParticipationRepository participationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;

    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(HttpServletRequest request, Model model) {
        // Get user from JWT token in cookie
        User user = getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        
        // Get user's existing participations to pre-select their ranked sessions
        List<Participation> userParticipations = participationRepository.findByUserId(user.getId());
        List<Long> selectedSessionIds = new ArrayList<>();
        for (Participation participation : userParticipations) {
            selectedSessionIds.add(participation.getSession().getId());
        }
        
        model.addAttribute("sessions", breakoutSessions);
        model.addAttribute("isAdmin", user != null && user.getRole().equals(Role.ADMIN));
        model.addAttribute("selectedSessionIds", selectedSessionIds);
        model.addAttribute("user", user);
        return "sessions/breakoutRoomPreferences";
    }

    // register user for sessions
    @PostMapping("/sessions/register")
    public String registerUser(
            @RequestParam(required = false) List<Long> sessionIds,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        // Get user from JWT token in cookie
        User user = getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        if (sessionIds == null || sessionIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one session.");
            return "redirect:/breakout";
        }

        try {
            // First, remove any existing participations for this user
            List<Participation> existingParticipations = participationRepository.findByUserId(user.getId());
            participationRepository.deleteAll(existingParticipations);
            
            // Add new participations for selected sessions
            for (Long sessionId : sessionIds) {
                Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
                if (sessionOpt.isPresent()) {
                    Session session = sessionOpt.get();
                    Participation participation = new Participation(user, session);
                    participationRepository.save(participation);
                }
            }
            
            return "redirect:/sessions/thankYou";
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            redirectAttributes.addFlashAttribute("error", "Unable to save your preferences. Please try again.");
            return "redirect:/breakout";
        }
    }

    @GetMapping("/sessions/thankYou")
    public String getThankYouPage(HttpServletRequest request, Model model) {
        User user = getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", user);
        return "sessions/thankYou";
    }

    // unregister user from a specific session
    @PostMapping("/sessions/unregister")
    @ResponseBody
    public String unregisterUser(
            @RequestParam Long sessionId,
            HttpServletRequest request
    ) {
        User user = getUserFromRequest(request);
        if (user == null) {
            return "User not authenticated";
        }
        
        try {
            Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                Optional<Participation> participationOpt = participationRepository.findFirstBySessionAndUser(session, user);
                if (participationOpt.isPresent()) {
                    participationRepository.delete(participationOpt.get());
                    return "Successfully unregistered from session";
                }
            }
            return "Session not found or not registered";
        } catch (Exception e) {
            return "Error unregistering from session";
        }
    }

    @GetMapping("/sessions/schedule")
    public String getSchedulePage(HttpServletRequest request, Model model) {
        User user = getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        Map<String, List<Session>> schedule = sessionService.getUserSchedule(user);
        model.addAttribute("schedule", schedule);
        model.addAttribute("isAdmin", user.getRole().equals(Role.ADMIN));

        return "sessions/eventSchedule";
    }
    
    // Helper method to extract user from JWT cookie
    private User getUserFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt-token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    try {
                        String username = jwtService.extractUsername(token);
                        Optional<User> userOpt = userRepository.findByEmail(username);
                        if (userOpt.isPresent() && jwtService.isTokenValid(token, userOpt.get())) {
                            return userOpt.get();
                        }
                    } catch (Exception e) {
                        // Token invalid or expired
                        return null;
                    }
                }
            }
        }
        return null;
    }
}