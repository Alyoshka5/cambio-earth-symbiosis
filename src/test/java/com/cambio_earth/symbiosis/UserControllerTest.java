package com.cambio_earth.symbiosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.UserController;
import com.cambio_earth.symbiosis.dto.LoginUserDto;
import com.cambio_earth.symbiosis.dto.ProfileDto;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.EventService;
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
    private EventService eventService;

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

        userController = new UserController(jwtService, authenticationService, passwordEncoder, eventService);

        try {
            java.lang.reflect.Field userRepoField = UserController.class.getDeclaredField("userRepository");
            userRepoField.setAccessible(true);
            userRepoField.set(userController, userRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        testUser = new User();
        testUser.setId(1L);

        profileOwner = new User();
        profileOwner.setId(2L);

        testPost1 = new Post();
        testPost1.setId(1L);
        testPost1.setUser(profileOwner);
        testPost1.setCreatedAt(java.time.LocalDateTime.now());

        testPost2 = new Post();
        testPost2.setId(2L);
        testPost2.setUser(profileOwner);
        testPost2.setCreatedAt(java.time.LocalDateTime.now());
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

    @Test
    void testViewEditProfile_UserNotLoggedIn_RedirectsToLogin() {

        when(authenticationService.getUserFromRequest(request)).thenReturn(null);

        String result = userController.getProfileEditForm(request, model);

        assertEquals("redirect:/auth/login", result);
    }

    @Test
    void testUpdateProfileFirstName_Success_SavesChanges() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("Macintosh");
        profileDto.setCurrentPassword("");
        profileDto.setNewPassword("");
        profileDto.setConfirmNewPassword("");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Bob");
        testUser.setLastName("Macintosh");

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("redirect:/profile", result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateProfileFirstName_FirstNameTooShort_RedirectToForm() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("J");
        profileDto.setLastName("Macintosh");
        profileDto.setCurrentPassword("");
        profileDto.setNewPassword("");
        profileDto.setConfirmNewPassword("");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Bob");
        testUser.setLastName("Macintosh");

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("profileEditForm", result);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void testUpdateProfileLastName_Success_SavesChanges() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("Doe");
        profileDto.setCurrentPassword("");
        profileDto.setNewPassword("");
        profileDto.setConfirmNewPassword("");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Joe");
        testUser.setLastName("Macintosh");

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("redirect:/profile", result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateProfileLastName_LastNameTooShort_RedirectToForm() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("D");
        profileDto.setCurrentPassword("");
        profileDto.setNewPassword("");
        profileDto.setConfirmNewPassword("");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Joe");
        testUser.setLastName("Macintosh");

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("profileEditForm", result);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void testUpdateProfilePassword_Success_SavesChanges() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("Macintosh");
        profileDto.setCurrentPassword("currentPassword");
        profileDto.setNewPassword("newPassword");
        profileDto.setConfirmNewPassword("newPassword");

        when(passwordEncoder.encode("currentPassword")).thenReturn("encryptedPassword");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Joe");
        testUser.setLastName("Macintosh");
        testUser.setPassword(passwordEncoder.encode("currentPassword"));

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("redirect:/profile", result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateProfilePassword_IncorrectPassword_RedirectToForm() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("Macintosh");
        profileDto.setCurrentPassword("wrongPassword");
        profileDto.setNewPassword("newPassword");
        profileDto.setConfirmNewPassword("newPassword");

        LoginUserDto authenticationDto = new LoginUserDto();
        authenticationDto.setEmail("joem@gmail.com");
        authenticationDto.setPassword(profileDto.getCurrentPassword());

        when(passwordEncoder.encode("currentPassword")).thenReturn("encryptedPassword");
        when(passwordEncoder.encode("wrongPassword")).thenReturn("encryptedWrongPassword");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Joe");
        testUser.setLastName("Macintosh");
        testUser.setPassword(passwordEncoder.encode("currentPassword"));

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(null);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("profileEditForm", result);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void testUpdateProfilePassword_PasswordsDontMatch_RedirectToForm() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setFirstName("Joe");
        profileDto.setLastName("Macintosh");
        profileDto.setCurrentPassword("currentPassword");
        profileDto.setNewPassword("newPassword");
        profileDto.setConfirmNewPassword("newMismatchedPassword");

        LoginUserDto authenticationDto = new LoginUserDto();
        authenticationDto.setEmail("joem@gmail.com");
        authenticationDto.setPassword(profileDto.getCurrentPassword());

        when(passwordEncoder.encode("currentPassword")).thenReturn("encryptedPassword");

        testUser = new User();
        testUser.setEmail("joem@gmail.com");
        testUser.setFirstName("Joe");
        testUser.setLastName("Macintosh");
        testUser.setPassword(passwordEncoder.encode("currentPassword"));

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(testUser);

        String result = userController.saveProfileInfo(request, model, profileDto);

        assertEquals("profileEditForm", result);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void testSetUserAsAdmin_Success_UserRoleUpdated() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setRole(Role.USER);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        
        java.util.Map<String, Object> result = userController.setUserAsAdmin(2L, request);
        
        assertEquals(true, result.get("success"));
        assertEquals(Role.ADMIN, targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void testSetUserAsAdmin_UserNotFound_ReturnsError() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        java.util.Map<String, Object> result = userController.setUserAsAdmin(999L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("User not found", result.get("message"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSetUserAsAdmin_NonAdminUser_ReturnsUnauthorized() {
        User nonAdminUser = new User();
        nonAdminUser.setId(1L);
        nonAdminUser.setRole(Role.USER);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(nonAdminUser);
        
        java.util.Map<String, Object> result = userController.setUserAsAdmin(2L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("Unauthorized", result.get("message"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSetUserAsAdmin_UserNotLoggedIn_ReturnsUnauthorized() {
        when(authenticationService.getUserFromRequest(request)).thenReturn(null);
        
        java.util.Map<String, Object> result = userController.setUserAsAdmin(2L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("Unauthorized", result.get("message"));
        verify(userRepository, never()).save(any());
    }
}