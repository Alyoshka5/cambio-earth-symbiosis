package com.cambio_earth.symbiosis.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.services.SessionService;


@Controller
public class SessionController {
    @Autowired
    private SessionService sessionService;

    @GetMapping("/schedule")
    public String getSchedulePage(@AuthenticationPrincipal User user, Model model) {
        Map<LocalDate, List<Session>> schedule = sessionService.getUserSchedule(user);
        model.addAttribute("schedule", schedule);

        return "sessions/eventSchedule";
    }
    
}
