package com.cambio_earth.symbiosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.UserController;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserController userController;

    private User testUser;
    private User profileOwner;
    private Post testPost1;
    private Post testPost2;

    @BeforeEach
    void setUp() {

        userController = new UserController(jwtService, authenticationService, passwordEncoder);

        try {
            java.lang.reflect.Field userRepoField = UserController.class.getDeclaredField("userRepository");
            userRepoField.setAccessible(true);
            userRepoField.set(userController, userRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // setup users
        testUser = new User();
        testUser.setId(1L);

        profileOwner = new User();
        profileOwner.setId(2L);

        // setup posts
        testPost1 = new Post();
        testPost1.setId(1L);
        testPost1.setUser(profileOwner);
        testPost1.setCreatedAt(java.time.LocalDateTime.now()); // 🔥 FIX

        testPost2 = new Post();
        testPost2.setId(2L);
        testPost2.setUser(profileOwner);
        testPost2.setCreatedAt(java.time.LocalDateTime.now()); // 🔥 FIX
    }

    @Test
    void testViewProfile_Success_UserNameAndPostsDisplayed() {

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(profileOwner));

        profileOwner.setPosts(new ArrayList<>(Arrays.asList(testPost1, testPost2)));

        String result = userController.getProfilePage(2L, request, response, model);

        assertEquals("profile", result);
        verify(model).addAttribute(eq("profileOwner"), eq(profileOwner));
        verify(model).addAttribute(eq("posts"), anyList());
        verify(model).addAttribute(eq("currentUser"), eq(testUser));
    }

    @Test
    void testViewProfile_UserHasNoPosts_NoPostsDisplayed() {

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(profileOwner));

        profileOwner.setPosts(new ArrayList<>());

        String result = userController.getProfilePage(2L, request, response, model);

        assertEquals("profile", result);
        verify(model).addAttribute(eq("profileOwner"), eq(profileOwner));
    }

    @Test
    void testViewProfile_UserNotLoggedIn_RedirectsToLogin() {

        when(authenticationService.getUserFromRequest(request)).thenReturn(null);

        String result = userController.getProfilePage(1L, request, response, model);

        assertEquals("redirect:/auth/login", result);

    }

    @Test
    void testViewProfile_ProfileOwnerNotFound_RedirectsToLogin() {

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        String result = userController.getProfilePage(999L, request, response, model);

        assertEquals("redirect:/auth/login", result);
    }
}