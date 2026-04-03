package com.cambio_earth.symbiosis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private AuthenticationService authenticationService;

    @ModelAttribute("currentUserId")
    public Long getCurrentUserId(HttpServletRequest request) {
        User user = authenticationService.getUserFromRequest(request);
        if (user != null) {
            return user.getId();
        }
        return null;
    }
}
