package com.cambio_earth.symbiosis.controllers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.models.CompletedMissions;
import com.cambio_earth.symbiosis.models.CompletedMissionsRepository;
import com.cambio_earth.symbiosis.models.Mission;
import com.cambio_earth.symbiosis.models.MissionRepository;
import com.cambio_earth.symbiosis.models.MissionType;
import com.cambio_earth.symbiosis.models.MissionViewModel;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

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
    
    // Process mission completion
    @PostMapping("/missions/claim")
    public String processClaimingMission(@RequestParam Long missionId, RedirectAttributes model, HttpServletRequest request) {

        // Get current user
        User currUser = authenticationService.getUserFromRequest(request);
        if (currUser == null) {
            return "redirect:/auth/login";
        }

        Mission mission = missionRepository.findById(missionId).orElse(null);
        if (mission == null) {
            model.addFlashAttribute("missionErr", "Mission could not be found.");
            return "redirect:/missions";
        }

        // Give the user mission rewards and add the mission to the completedMissions database
        CompletedMissions missionCompleted = new CompletedMissions(currUser, mission);

        try {
            completedMissionsRepository.save(missionCompleted);

            // Get the user directly from the database and update the user with new points attribute
            User user = userRepository.findById(currUser.getId()).orElse(null);
            if (user == null) {
                return "redirect:/login";
            }
            user.setPoints(user.getPoints() + mission.getPoints());
            userRepository.save(user);

        } catch (Exception e) {
            model.addFlashAttribute("missionErr", "Mission could not be set as complete.");
        }

        return "redirect:/missions";
    }

}