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
import com.cambio_earth.symbiosis.dto.VerifyUserDto;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.EventService;
import com.cambio_earth.symbiosis.services.JwtService;
import com.cambio_earth.symbiosis.services.SessionService;

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
    private SessionService sessionService;

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

        userController = new UserController(jwtService, authenticationService, passwordEncoder, eventService, sessionService);

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
    void testSignupAfterLaunchRegistration_EventNotLaunched_DoNotRegisterUser() {
        VerifyUserDto verifyUserDto = new VerifyUserDto();
        verifyUserDto.setEmail("joem@gmail.com");
        verifyUserDto.setFirst("1");
        verifyUserDto.setSecond("1");
        verifyUserDto.setThird("1");
        verifyUserDto.setFourth("1");
        verifyUserDto.setFifth("1");
        verifyUserDto.setSixth("1");

        User newUser = new User();
        newUser.setEmail("joem@gmail.com");

        when(userRepository.findByEmail(verifyUserDto.getEmail())).thenReturn(Optional.of(newUser));
        when(eventService.isEventLaunched()).thenReturn(false);

        userController.verifyCode(model, verifyUserDto, response);

        verify(sessionService, never()).registerUserAfterLaunch(newUser);
    }

    @Test
    void testSignupAfterLaunchRegistration_EventLaunched_RegisterUser() {
        VerifyUserDto verifyUserDto = new VerifyUserDto();
        verifyUserDto.setEmail("joem@gmail.com");
        verifyUserDto.setFirst("1");
        verifyUserDto.setSecond("1");
        verifyUserDto.setThird("1");
        verifyUserDto.setFourth("1");
        verifyUserDto.setFifth("1");
        verifyUserDto.setSixth("1");

        User newUser = new User();
        newUser.setEmail("joem@gmail.com");

        when(userRepository.findByEmail(verifyUserDto.getEmail())).thenReturn(Optional.of(newUser));
        when(eventService.isEventLaunched()).thenReturn(true);

        userController.verifyCode(model, verifyUserDto, response);

        verify(sessionService).registerUserAfterLaunch(newUser);
    }
}