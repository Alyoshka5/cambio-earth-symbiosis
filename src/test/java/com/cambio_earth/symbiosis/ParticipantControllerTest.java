package com.cambio_earth.symbiosis;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.cambio_earth.symbiosis.controllers.SessionController;
import com.cambio_earth.symbiosis.controllers.UserController;
import com.cambio_earth.symbiosis.models.LauanchEventRepository;
import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class ParticipantControllerTest {

    private MockMvc mockMvcSession;
    private MockMvc mockMvcUser;

    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private ParticipationRepository participationRepository;
    @Mock private LauanchEventRepository launchEventRepository;
    @Mock private AuthenticationService authenticationService;
    @Mock private SessionService sessionService;

    @InjectMocks private SessionController sessionController;
    @InjectMocks private UserController userController;

    private User adminUser;
    private User targetUser;
    private Session testSession;

    @BeforeEach
        void setUp() {
            // 1. Manually inject repositories as before
            ReflectionTestUtils.setField(userController, "userRepository", userRepository);
            ReflectionTestUtils.setField(sessionController, "userRepository", userRepository);
            ReflectionTestUtils.setField(sessionController, "sessionRepository", sessionRepository);
            ReflectionTestUtils.setField(sessionController, "participationRepository", participationRepository);
            ReflectionTestUtils.setField(sessionController, "launchEventRepository", launchEventRepository);

            // 2. Create the FIRST resolver for the Session Controller
            InternalResourceViewResolver sessionResolver = new InternalResourceViewResolver();
            sessionResolver.setPrefix("/templates/");
            sessionResolver.setSuffix(".html");

            mockMvcSession = MockMvcBuilders.standaloneSetup(sessionController)
                                            .setViewResolvers(sessionResolver)
                                            .build();

            // 3. Create a SECOND, NEW resolver for the User Controller
            // This prevents the "Cannot reinitialize with different application context" error
            InternalResourceViewResolver userResolver = new InternalResourceViewResolver();
            userResolver.setPrefix("/templates/");
            userResolver.setSuffix(".html");

            mockMvcUser = MockMvcBuilders.standaloneSetup(userController)
                                        .setViewResolvers(userResolver)
                                        .build();

            // 4. Initialize your mock data
            adminUser = new User();
            adminUser.setId(1L);
            adminUser.setRole(Role.ADMIN);

            targetUser = new User();
            targetUser.setId(2L);
            targetUser.setRole(Role.USER);
            targetUser.setEnabled(true);

            testSession = new Session();
            testSession.setId(100L);
            testSession.setTitle("Test Session");
        }

    @Test
    void testGetParticipantsPage_AsAdmin_ShowsAdminAttributes() throws Exception {
        when(authenticationService.getUserFromRequest(any())).thenReturn(adminUser);
        when(userRepository.findByEnabled(true)).thenReturn(new ArrayList<>());

        mockMvcUser.perform(get("/participants"))
               .andExpect(status().isOk())
               .andExpect(view().name("participants"))
               .andExpect(model().attribute("isAdmin", true));
    }

    @Test
    void testRemoveUserFromSpecificSession_Success() throws Exception {
        when(authenticationService.getUserFromRequest(any())).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        
        Participation p = new Participation(targetUser, testSession);
        when(participationRepository.findFirstBySessionAndUser(testSession, targetUser))
                .thenReturn(Optional.of(p));

        mockMvcSession.perform(post("/sessions/participants/2/remove/100"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));

        verify(participationRepository).delete(p);
    }

    @Test
    void testSetUserAsAdmin_RoleUpdated() throws Exception {
        when(authenticationService.getUserFromRequest(any())).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        mockMvcUser.perform(post("/participants/2/set-admin"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));

        assertEquals(Role.ADMIN, targetUser.getRole());
        verify(userRepository).save(targetUser);
    }

    @Test
    void testSchedulePage_EmptySessions_Works() throws Exception {
        when(authenticationService.getUserFromRequest(any())).thenReturn(targetUser);
        when(sessionService.getUserSchedule(any())).thenReturn(new java.util.HashMap<>());
        when(launchEventRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvcSession.perform(get("/sessions/schedule"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("schedule"));
    }
}