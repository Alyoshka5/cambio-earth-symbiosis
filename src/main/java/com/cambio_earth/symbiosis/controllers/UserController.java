package com.cambio_earth.symbiosis.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cambio_earth.symbiosis.dto.LoginUserDto;
import com.cambio_earth.symbiosis.dto.RegisterUserDto;
import com.cambio_earth.symbiosis.dto.VerifyUserDto;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
    @Autowired UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public UserController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    // TODO: move /breakout get mapping to different controller
    @GetMapping("/breakout")
    public String getBreakoutPreferencesPage() {
        return "breakout-room-preferences";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/auth/signup";
    }
    

    
    @GetMapping("/auth/signup")
    public String getSignUpPage() {
        return "signUp";
    }
    
    @PostMapping("/auth/signup")
    public String register(@ModelAttribute RegisterUserDto registerUserDto) {
        authenticationService.signup(registerUserDto);
        return "redirect:/auth/verify?email=" + registerUserDto.getEmail();
    }
    
    @GetMapping("/auth/login")
    public String getLoginPage() {
        return "login";
    }
    
    @PostMapping("/auth/login")
    public String authenticate(@ModelAttribute LoginUserDto loginUserDto, HttpServletResponse response){
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);
    
            Cookie cookie = new Cookie("jwt-token", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            return "redirect:/breakout";
        } catch (RuntimeException e) {
            return "login";
        }
    }

    @GetMapping("/auth/verify")
    public String getVerificationPage(Model model, @RequestParam(name = "email", required = false) String email) {
        model.addAttribute("email", email);
        return "verificationCode";
    }

    @PostMapping("/auth/verify")
    public String verifyCode(Model model, @ModelAttribute VerifyUserDto verifyUserDto, HttpServletResponse response) {
        try {
            authenticationService.verifyUser(verifyUserDto);

            Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String jwtToken = jwtService.generateToken(user);

                Cookie cookie = new Cookie("jwt-token", jwtToken);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/breakout";
            }
            return "redirect:/auth/signin";
        } catch (RuntimeException e) {
            model.addAttribute("email", verifyUserDto.getEmail());
            return "verificationCode";
        }
    }

    @PostMapping("/auth/resend")
    public String resendVerificationCode(Model model, @RequestParam String email) {
        model.addAttribute("email", email);
        try {
            authenticationService.resendVerificationCode(email);
            return "verificationCode";
        } catch (RuntimeException e) {
            return "verificationCode";
        }
    }

    @PostMapping("/auth/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return "redirect:/auth/login";
    }
}