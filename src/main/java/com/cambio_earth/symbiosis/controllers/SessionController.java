package com.cambio_earth.symbiosis.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.models.BreakoutBlockRanking;
import com.cambio_earth.symbiosis.models.BreakoutBlockRankingRepository;
import com.cambio_earth.symbiosis.models.LauanchEventRepository;
import com.cambio_earth.symbiosis.models.LaunchEvent;
import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    @Autowired
    private LauanchEventRepository launchEventRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean hasSubmittedPreferences(User user) {
        List<BreakoutBlockRanking> rankings = breakoutBlockRankingRepository.findByUser(user);
        return rankings != null && !rankings.isEmpty();
    }

    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<LaunchEvent> events = launchEventRepository.findAll();
        if (!events.isEmpty() && events.get(0).isStarted()) {
            return "redirect:/home";
        }
        
        if (hasSubmittedPreferences(user)) {
            redirectAttributes.addFlashAttribute("info", "You have already submitted your preferences.");
            return "redirect:/sessions/thankYou";
        }
        
        List<Session> breakoutSessions = sessionService.getBreakoutSessions();
        
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

    @PostMapping("/sessions/register")
    public String registerUser(
            @RequestParam(required = false) List<Long> sessionIds,
            @RequestParam(required = false) List<Integer> rankings,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

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
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Unable to save your preferences. Please try again.");
            return "redirect:/breakout";
        }
    }

    @GetMapping("/sessions/thankYou")
    public String getThankYouPage(HttpServletRequest request, Model model, HttpServletResponse response) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        model.addAttribute("user", user);
        return "sessions/thankYou";
    }

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

        if (user.getRole().equals(Role.ADMIN)) {
            // Get list of sessions that admin user is registered for
            List<Session> adminRegisteredSessions = sessionService.getUserRegisteredSessions(user);
            model.addAttribute("adminRegisteredSessions", adminRegisteredSessions);
        }

        List<LaunchEvent> events = launchEventRepository.findAll();
        boolean eventNotLaunched = events.isEmpty() || !events.get(0).isStarted();
        model.addAttribute("eventNotLaunched", eventNotLaunched);

        return "sessions/eventSchedule";
    }

    @GetMapping("/sessions/user/{userId}/sessions")
    @ResponseBody
    public List<Map<String, Object>> getUserSessions(@PathVariable Long userId, HttpServletRequest request) {
        User currUser = authenticationService.getUserFromRequest(request);
        List<Map<String, Object>> response = new ArrayList<>();
        
        if (currUser == null || currUser.getRole() != Role.ADMIN) {
            return response;
        }
        
        User targetUser = userRepository.findById(userId).orElse(null);
        if (targetUser == null) {
            return response;
        }
        
        List<Participation> participations = participationRepository.findByUserId(targetUser.getId());
        for (Participation participation : participations) {
            Session session = participation.getSession();
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("id", session.getId());
            sessionInfo.put("title", session.getTitle());
            sessionInfo.put("startDateTime", session.getStartDateTime() != null ? session.getStartDateTime().toString() : "TBA");
            sessionInfo.put("endDateTime", session.getEndDateTime() != null ? session.getEndDateTime().toString() : null);
            response.add(sessionInfo);
        }
        
        return response;
    }

    @GetMapping("/sessions/user/{userId}/available-sessions")
    @ResponseBody
    public List<Map<String, Object>> getAvailableSessions(@PathVariable Long userId, HttpServletRequest request) {
        User currUser = authenticationService.getUserFromRequest(request);
        List<Map<String, Object>> response = new ArrayList<>();
        
        if (currUser == null || currUser.getRole() != Role.ADMIN) {
            return response;
        }
        
        User targetUser = userRepository.findById(userId).orElse(null);
        if (targetUser == null) {
            return response;
        }
        
        List<Participation> userParticipations = participationRepository.findByUserId(targetUser.getId());
        List<Long> registeredSessionIds = new ArrayList<>();
        for (Participation p : userParticipations) {
            registeredSessionIds.add(p.getSession().getId());
        }
        
        List<Session> allSessions = sessionRepository.findAll();
        for (Session session : allSessions) {
            if (!registeredSessionIds.contains(session.getId())) {
                Map<String, Object> sessionInfo = new HashMap<>();
                sessionInfo.put("id", session.getId());
                sessionInfo.put("title", session.getTitle());
                sessionInfo.put("startDateTime", session.getStartDateTime() != null ? session.getStartDateTime().toString() : "TBA");
                sessionInfo.put("endDateTime", session.getEndDateTime() != null ? session.getEndDateTime().toString() : null);
                response.add(sessionInfo);
            }
        }
        
        return response;
    }

    @PostMapping("/sessions/participants/{userId}/add/{sessionId}")
    @ResponseBody
    public Map<String, Object> addUserToSession(@PathVariable Long userId, @PathVariable Long sessionId, HttpServletRequest request) {
        User currUser = authenticationService.getUserFromRequest(request);
        Map<String, Object> response = new HashMap<>();
        
        if (currUser == null || currUser.getRole() != Role.ADMIN) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }
        
        User targetUser = userRepository.findById(userId).orElse(null);
        if (targetUser == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }
        
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            response.put("success", false);
            response.put("message", "Session not found");
            return response;
        }
        
        Optional<Participation> existingParticipation = participationRepository.findFirstBySessionAndUser(session, targetUser);
        if (existingParticipation.isPresent()) {
            response.put("success", false);
            response.put("message", "User already registered for this session");
            return response;
        }
        
        Participation participation = new Participation();
        participation.setSession(session);
        participation.setUser(targetUser);
        participationRepository.save(participation);
        
        response.put("success", true);
        response.put("message", "User added to session successfully");
        return response;
    }

    @PostMapping("/sessions/participants/{userId}/remove/{sessionId}")
    @ResponseBody
    public Map<String, Object> removeUserFromSpecificSession(@PathVariable Long userId, @PathVariable Long sessionId, HttpServletRequest request) {
        User currUser = authenticationService.getUserFromRequest(request);
        Map<String, Object> response = new HashMap<>();
        
        if (currUser == null || currUser.getRole() != Role.ADMIN) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }
        
        User targetUser = userRepository.findById(userId).orElse(null);
        if (targetUser == null) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }
        
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            response.put("success", false);
            response.put("message", "Session not found");
            return response;
        }
        
        Optional<Participation> participation = participationRepository.findFirstBySessionAndUser(session, targetUser);
        if (participation.isPresent()) {
            participationRepository.delete(participation.get());
            response.put("success", true);
            response.put("message", "User removed from session successfully");
        } else {
            response.put("success", false);
            response.put("message", "User not registered for this session");
        }
        
        return response;
    }
}