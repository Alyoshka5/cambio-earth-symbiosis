package com.cambio_earth.symbiosis.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Controller
public class AdminSessionController {

    @Autowired
    SessionRepository sessionRepository;

    // AdminSessionForm.html 

    // Show blank form (create new)
    @GetMapping("/admin/sessions/new")
    public String getNewSessionForm(Model model) {
        model.addAttribute("session", new Session());
        return "AdminSessionForm";
    }

    // Show pre-filled form (edit existing)
    @GetMapping("/admin/sessions/{id}/edit")
    public String getEditSessionForm(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("session", session);
        return "AdminSessionForm";
    }

    // Handle form submission (create or update)
    @PostMapping("/admin/sessions/save")
    public String saveSession(
            @ModelAttribute Session session,
            @RequestParam String speakersRaw,
            @RequestParam String sessionDate,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        session.setSpeakers(speakersRaw.split(","));
        session.setStartDateTime(LocalDateTime.of(LocalDate.parse(sessionDate), LocalTime.parse(startTime)));
        session.setEndDateTime(LocalDateTime.of(LocalDate.parse(sessionDate), LocalTime.parse(endTime)));
        sessionRepository.save(session);
        return "redirect:/sessions/" + session.getId();
    }

    // AddDetails.html

    // Show session detail page
    @GetMapping("/sessions/{id}")
    public String getSessionDetails(@PathVariable Long id, Model model) {
        Session session = sessionRepository.findById(id).orElseThrow();
        model.addAttribute("session", session);
        return "addDetails";
    }

    // Delete (admin only)
    @PostMapping("/admin/sessions/{id}/delete")
    public String deleteSession(@PathVariable Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/sessions";
    }
}