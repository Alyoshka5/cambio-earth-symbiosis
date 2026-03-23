package com.cambio_earth.symbiosis.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cambio_earth.symbiosis.models.Role;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.VenueMap;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.VenueMapService;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.IllegalArgumentException;

@Controller
@RequestMapping("/maps")
public class VenueMapController {

    @Autowired
    VenueMapService venueMapService;

    @Autowired
    AuthenticationService authenticationService;

    // ── View all maps (admin sees all, participants see published only) ────────

    @GetMapping
    public String getMapsPage(HttpServletRequest request, Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";

        List<VenueMap> maps = user.getRole() == Role.ADMIN
            ? venueMapService.getAllMaps()
            : venueMapService.getPublishedMaps();

        model.addAttribute("maps", maps);
        model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
        return "maps";
    }

    // ── Add map form ──────────────────────────────────────────────────────────

    @GetMapping("/add")
    public String getAddMapPage(HttpServletRequest request, Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/maps";

        return "addMap";
    }

    @PostMapping("/add")
    public String addMap(HttpServletRequest request,
                        @RequestParam(value = "fileUrl", required = false) String fileUrl,
                        @RequestParam(value = "fileType", required = false) String fileType,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "floorLevel", required = false) String floorLevel,
                        Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/maps";

        try {
            venueMapService.addMap(fileUrl, fileType, title, floorLevel);
            return "redirect:/maps";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "addMap";
        }
    }

    // ── Edit map form ─────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String getEditMapPage(HttpServletRequest request,
                                 @PathVariable Long id,
                                 Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/maps";

        try {
            VenueMap map = venueMapService.getMapById(id);
            model.addAttribute("map", map);
            return "editMap";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/maps";
        }
    }

   @PostMapping("/{id}/edit")
public String updateMap(HttpServletRequest request,
                        @PathVariable Long id,
                        @RequestParam(value = "fileUrl", required = false) String fileUrl,
                        @RequestParam(value = "fileType", required = false) String fileType,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "floorLevel", required = false) String floorLevel,
                        Model model) {
    User user = authenticationService.getUserFromRequest(request);
    if (user == null) return "redirect:/auth/login";
    if (user.getRole() != Role.ADMIN) return "redirect:/maps";

    try {
        venueMapService.updateMap(id, fileUrl, fileType, title, floorLevel);
        return "redirect:/maps";
    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("map", venueMapService.getMapById(id));
        return "editMap";
    }
}

    // ── Delete map ────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String deleteMap(HttpServletRequest request,
                            @PathVariable Long id,
                            Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/maps";

        try {
            venueMapService.deleteMap(id);
            return "redirect:/maps";
        } catch (RuntimeException e) {
            // Handles Story #5.5 error: map already deleted by another admin
            model.addAttribute("error", e.getMessage()); // "Map can no longer be found."
            return "redirect:/maps";
        }
    }

    // ── Publish changes ───────────────────────────────────────────────────────

    @PostMapping("/publish")
    public String publishChanges(HttpServletRequest request, Model model) {
        User user = authenticationService.getUserFromRequest(request);
        if (user == null) return "redirect:/auth/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/maps";

        venueMapService.publishChanges();
        model.addAttribute("publishSuccess", "Changes have been published and are visible to all participants.");
        return "redirect:/maps";
    }
}