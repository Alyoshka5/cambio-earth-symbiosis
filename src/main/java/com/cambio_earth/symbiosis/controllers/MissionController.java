package com.cambio_earth.symbiosis.controllers;
import org.springframework.stereotype.Controller;

import com.cambio_earth.symbiosis.models.*;

import jakarta.servlet.http.HttpServletRequest;
import com.cambio_earth.symbiosis.services.AuthenticationService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MissionController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CompletedMissionsRepository completedMissionsRepository;

    @Autowired
    MissionRepository missionRepository;
   
    @Autowired
    AuthenticationService authenticationService;

    // Load the missions page
    @GetMapping("/missions")
    public String getMissionsPage(Model model, HttpServletRequest request) {
       
        // Get current user
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Get all missions and the ones the current user has already claimed
        List<Mission> allMissions = missionRepository.findAll();
        List<CompletedMissions> claimedMissions = completedMissionsRepository.findByUser(user);
        Set<Long> claimedMissionsIds = new HashSet<>();
        for (CompletedMissions finishedMission : claimedMissions) {
            claimedMissionsIds.add(finishedMission.getMission().getId());
        }

        // Build a MissionViewModel for each mission
        List<MissionViewModel> missionCards = new ArrayList<>();
        for (Mission currMission : allMissions) {
            long progress;

            if (currMission.getMissionType() == MissionType.LIKES) {
                progress = user.getNumberOfLikedPosts();
            } else {
                progress = user.getNumberOfPostsCreated();
            }

            boolean isClaimed = claimedMissionsIds.contains(currMission.getId());
            MissionViewModel card = new MissionViewModel(currMission, progress, isClaimed);
            missionCards.add(card);
        }

        model.addAttribute("missionCards", missionCards);
        model.addAttribute("currentUser", user);
        System.out.println(missionCards);
        return "missions";
    }

}