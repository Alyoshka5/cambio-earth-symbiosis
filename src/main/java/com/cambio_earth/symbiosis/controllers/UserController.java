package com.cambio_earth.symbiosis.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cambio_earth.symbiosis.dto.LoginUserDto;
import com.cambio_earth.symbiosis.dto.ProfileDto;
import com.cambio_earth.symbiosis.dto.RegisterUserDto;
import com.cambio_earth.symbiosis.dto.VerifyUserDto;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.EventService;
import com.cambio_earth.symbiosis.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
   
    @Autowired
    UserRepository userRepository;
   
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;

    public UserController(JwtService jwtService, AuthenticationService authenticationService, PasswordEncoder passwordEncoder, EventService eventService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/signup")
    public String getSignUpPage() {
        return "signUp";
    }

    @PostMapping("/auth/signup")
    public String register(@ModelAttribute RegisterUserDto registerUserDto, Model model) {
        String email = registerUserDto.getEmail();

        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@cambioearth\\.com$")) {
            model.addAttribute("error", "Not Valid Information.");
            return "signUp";
        }

        if (!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords must match.");
            return "signUp";
        }

        try {
            authenticationService.signup(registerUserDto);
            return "redirect:/auth/verify?email=" + registerUserDto.getEmail();
        } catch (RuntimeException e) {
            model.addAttribute("error", "Unable to create account.");
            return "signUp";
        }
    }

    @GetMapping("/auth/login")
    public String getLoginPage(HttpServletResponse response, @RequestParam(required = false) String logout) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return "login";
    }

    @PostMapping("/auth/login")
    public String authenticate(@ModelAttribute LoginUserDto loginUserDto,
                            HttpServletResponse response,
                            Model model) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);

            Cookie cookie = new Cookie("jwt-token", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
           
            if (eventService.isEventLaunched()) {
                return "redirect:/home";
            }

            return "redirect:/breakout";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Invalid email or password.");
            model.addAttribute("email", loginUserDto.getEmail());
            return "login";
        }
    }

    @GetMapping("/auth/verify")
    public String getVerificationPage(Model model, @RequestParam(name = "email", required = false) String email) {
        model.addAttribute("email", email);
        return "verificationCode";
    }

    @PostMapping("/auth/verify")
    public String verifyCode(Model model,
                            @ModelAttribute VerifyUserDto verifyUserDto,
                            HttpServletResponse response) {
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

            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("email", verifyUserDto.getEmail());
            model.addAttribute("error", "Invalid verification code.");
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
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
       
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
       
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
       
        return "redirect:/logout";
    }
   
    @GetMapping("/logout")
    public String logoutPage(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return "logout";
    }

    @GetMapping("/profile/{uid}")
    public String getProfilePage(@PathVariable Long uid, HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
       
        User currUser = authenticationService.getUserFromRequest(request);
        User profileOwner = userRepository.findById(uid).orElse(null);

        if (currUser == null || profileOwner == null) {
            return "redirect:/auth/login";
        }

        List<Post> userPosts = profileOwner.getPosts();
        userPosts.sort(Comparator.comparing(Post::getCreatedAt));

        if (currUser.getRole().equals(Role.ADMIN) || currUser.getId().equals(uid)) {
            model.addAttribute("currUserCanDeletePosts", true);
        }
       
        model.addAttribute("currentUser", currUser);
        model.addAttribute("profileOwner", profileOwner);
        model.addAttribute("posts", userPosts);

        return "profile";
    }

    @GetMapping("/profile")
    public String getCurrentUserProfilePage(HttpServletRequest request, Model model) {
        User currUser = authenticationService.getUserFromRequest(request);
        return "redirect:/profile/" + currUser.getId();
    }

    @GetMapping("/profile/edit")
    public String getProfileEditForm(HttpServletRequest request, Model model) {
        User currUser = authenticationService.getUserFromRequest(request);

        if (currUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", currUser);
        return "profileEditForm";
    }

    @PostMapping("/profile/edit")
    public String saveProfileInfo(HttpServletRequest request, Model model, ProfileDto profileDto) {
        User currUser = authenticationService.getUserFromRequest(request);

        if (currUser == null) {
            System.out.println("TRUE");
            return "redirect:/auth/login";
        }

        boolean validChanges = true;

        String firstName = profileDto.getFirstName();
        if (firstName.length() < 2 || firstName.length() > 100) {
            model.addAttribute("firstNameError", "First name must be between 2 and 100 characters");
            validChanges = false;
        }
        currUser.setFirstName(firstName);

        String lastName = profileDto.getLastName();
        if (lastName.length() < 2 || lastName.length() > 100) {
            model.addAttribute("lastNameError", "Last name must be between 2 and 100 characters");
            validChanges = false;
        }
        currUser.setLastName(lastName);

        if (!profileDto.getCurrentPassword().equals("") || !profileDto.getNewPassword().equals("") || !profileDto.getConfirmNewPassword().equals("")) {
            LoginUserDto authenticationDto = new LoginUserDto();
            authenticationDto.setEmail(currUser.getEmail());
            authenticationDto.setPassword(profileDto.getCurrentPassword());
            try {
                authenticationService.authenticate(authenticationDto);
                if (profileDto.getNewPassword().length() >= 8) {
                    if (profileDto.getNewPassword().equals(profileDto.getConfirmNewPassword())) {
                        currUser.setPassword(passwordEncoder.encode(profileDto.getNewPassword()));
                    } else {
                        model.addAttribute("confirmNewPasswordError", "Passwords must match");
                        validChanges = false;
                    }
                } else {
                    model.addAttribute("newPasswordError", "Password must be at least 8 characters long");
                    validChanges = false;
                }
            } catch (RuntimeException e) {
                model.addAttribute("currentPasswordError", "Incorrect Password");
                validChanges = false;
            }
        }

        if (validChanges) {
            userRepository.save(currUser);
            return "redirect:/profile";
        } else {
            model.addAttribute("user", currUser);
            return "profileEditForm";
        }
    }
   
    @GetMapping("/navigation")
    public String getNavigationPage(HttpServletRequest request, Model model) {
        User currUser = authenticationService.getUserFromRequest(request);
        if (currUser == null) return "redirect:/auth/login";

        model.addAttribute("currentUser", currUser);
        model.addAttribute("user", currUser);
        model.addAttribute("isAdmin", currUser.getRole() == Role.ADMIN);
        return "maps";
    }

    @GetMapping("/participants")
    public String getParticipantsPage(HttpServletRequest request, Model model) {
        User currUser = authenticationService.getUserFromRequest(request);
        if (currUser == null) return "redirect:/auth/login";
   
        List<User> participants = userRepository.findAll();
   
        model.addAttribute("currentUser", currUser);
        model.addAttribute("participants", participants);
        model.addAttribute("isAdmin", currUser.getRole() == Role.ADMIN);
   
        return "participants";
    }

    @PostMapping("/participants/{userId}/set-admin")
    @ResponseBody
    public java.util.Map<String, Object> setUserAsAdmin(@PathVariable Long userId, HttpServletRequest request) {
        User currUser = authenticationService.getUserFromRequest(request);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
       
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
       
        targetUser.setRole(Role.ADMIN);
        userRepository.save(targetUser);
       
        response.put("success", true);
        response.put("message", "User set as admin successfully");
        return response;
    }
}