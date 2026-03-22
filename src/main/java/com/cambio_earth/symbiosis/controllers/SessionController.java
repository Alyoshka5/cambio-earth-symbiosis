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

import com.cambio_earth.symbiosis.models.BreakoutBlockRanking;
import com.cambio_earth.symbiosis.models.BreakoutBlockRankingRepository;
import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BreakoutBlockRankingRepository breakoutBlockRankingRepository;
    
    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private AuthenticationService authenticationService;


    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(HttpServletRequest request, Model model) {
        // Get user from JWT token in cookie
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        
        // Get user's existing participations to pre-select their ranked sessions
        List<BreakoutBlockRanking> userRankings = breakoutBlockRankingRepository.findByUser(user);
        List<Long> selectedSessionIds = new ArrayList<>();
        for (BreakoutBlockRanking ranking : userRankings) {
            selectedSessionIds.add(ranking.getSession().getId());
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
            @RequestParam(required = false) List<Integer> rankings,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        // Get user from JWT token in cookie
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        if (sessionIds == null || sessionIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one session.");
            return "redirect:/breakout";
        }

        if (rankings == null || rankings.isEmpty() || rankings.size() != sessionIds.size()) {
            redirectAttributes.addFlashAttribute("error", "Invalid breakout rankings submitted.");
            return "redirect:/breakout";
        }

        try {
            // First, remove any existing participations for this user
            List<BreakoutBlockRanking> existingRankings = breakoutBlockRankingRepository.findByUser(user);
            breakoutBlockRankingRepository.deleteAll(existingRankings);

            for (int i = 0; i < sessionIds.size(); i++) {
                Long sessionId = sessionIds.get(i);
                Integer rank = rankings.get(i);

                Optional<Session> sessionOpt = sessionRepository.findById(sessionId);
                if (sessionOpt.isPresent()) {
                    Session session = sessionOpt.get();
                    BreakoutBlockRanking breakoutRanking = new BreakoutBlockRanking(user, session, rank);
                    breakoutBlockRankingRepository.save(breakoutRanking);
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
        User user = authenticationService.getUserFromRequest(request);
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
        User user = authenticationService.getUserFromRequest(request);
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
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        Map<String, List<Session>> schedule = sessionService.getUserSchedule(user);
        model.addAttribute("schedule", schedule);
        model.addAttribute("isAdmin", user.getRole().equals(Role.ADMIN));

        return "sessions/eventSchedule";
    }

    @PostMapping("/launch")
    public String launchEvent() {
        sessionService.registerUsersForMandatorySessions();
        sessionService.registerUsersForBreakoutSessions();

        return "redirect:/sessions/schedule";
    }
    
}