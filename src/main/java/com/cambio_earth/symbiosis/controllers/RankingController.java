package com.cambio_earth.symbiosis.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.RankingService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RankingController {

    private final RankingService rankingService;
    private final AuthenticationService authenticationService;

    public RankingController(RankingService rankingService, AuthenticationService authenticationService) {
        this.rankingService = rankingService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/rankings")
    public String showRankings(HttpServletRequest request, Model model) {
        User currUser = authenticationService.getUserFromRequest(request);

        List<User> rankedUsers = new ArrayList<>();
        try {
            rankedUsers = rankingService.getRankedUsers();
        } catch (Exception e) {
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("currentUser", null);
            model.addAttribute("rank", "N/A");
            model.addAttribute("errorMessage", "Be the first to earn points!");
            return "ranking";
        }

        List<User> topUsers = rankedUsers.subList(0, Math.min(10, rankedUsers.size()));

        model.addAttribute("users", topUsers);

        if (currUser != null) {
            int rank = rankingService.getUserRank(currUser, rankedUsers);
            model.addAttribute("currentUser", currUser);
            model.addAttribute("rank", rank);
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("rank", "N/A");
        }

        return "ranking";
    }
}