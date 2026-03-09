package com.cambio_earth.symbiosis.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;

@Controller
public class AdminSessionController {

    @Autowired
    SessionRepository sessionRepository;

    // adminSessionForm.html 

    // Show blank form (create new)
    @GetMapping("/admin/sessions/new")
    public String getNewSessionForm(Model model) {
        model.addAttribute("session", new Session());
        return "sessions/adminSessionForm";
    }

    // Show pre-filled form (edit existing)
    @GetMapping("/admin/sessions/{id}/edit")
    public String getEditSessionForm(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("session", session);
        return "sessions/adminSessionForm";
    }

    // Handle form submission (create or update)
    @PostMapping("/admin/sessions/save")
    public String saveSession(
            @ModelAttribute Session session,
            @RequestParam String speakersRaw,
            @RequestParam(required = false) String sessionDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        if (speakersRaw != null && !speakersRaw.isBlank()) {
            session.setSpeakers(Arrays.asList(speakersRaw.split(",")));
        } else {
            session.setSpeakers(new ArrayList<>());
        }
        if (sessionDate != null && !sessionDate.isBlank() && startTime != null && !startTime.isBlank()) {
            session.setStartDateTime(LocalDateTime.of(LocalDate.parse(sessionDate), LocalTime.parse(startTime)));
        }
        if (sessionDate != null && !sessionDate.isBlank() && endTime != null && !endTime.isBlank()) {
            session.setEndDateTime(LocalDateTime.of(LocalDate.parse(sessionDate), LocalTime.parse(endTime)));
        }
        sessionRepository.save(session);
        return "redirect:/sessions/" + session.getId();
    }

    // AddDetails.html

    // Show session detail page
    @GetMapping("/sessions/{id}")
    public String getSessionDetails(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("session", session);
        return "sessions/addDetails";
    }

    // Delete (admin only)
    @PostMapping("/admin/sessions/{id}/delete")
    public String deleteSession(@PathVariable Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/sessions";
    }
}