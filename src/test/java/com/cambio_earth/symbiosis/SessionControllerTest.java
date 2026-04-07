package com.cambio_earth.symbiosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.SessionController;
import com.cambio_earth.symbiosis.models.BreakoutBlockRankingRepository;
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

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private BreakoutBlockRankingRepository breakoutBlockRankingRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private LauanchEventRepository launchEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = new SessionController();
        
        try {
            java.lang.reflect.Field sessionServiceField = SessionController.class.getDeclaredField("sessionService");
            sessionServiceField.setAccessible(true);
            sessionServiceField.set(sessionController, sessionService);
            
            java.lang.reflect.Field sessionRepositoryField = SessionController.class.getDeclaredField("sessionRepository");
            sessionRepositoryField.setAccessible(true);
            sessionRepositoryField.set(sessionController, sessionRepository);
            
            java.lang.reflect.Field breakoutBlockRankingRepositoryField = SessionController.class.getDeclaredField("breakoutBlockRankingRepository");
            breakoutBlockRankingRepositoryField.setAccessible(true);
            breakoutBlockRankingRepositoryField.set(sessionController, breakoutBlockRankingRepository);
            
            java.lang.reflect.Field participationRepositoryField = SessionController.class.getDeclaredField("participationRepository");
            participationRepositoryField.setAccessible(true);
            participationRepositoryField.set(sessionController, participationRepository);
            
            java.lang.reflect.Field authenticationServiceField = SessionController.class.getDeclaredField("authenticationService");
            authenticationServiceField.setAccessible(true);
            authenticationServiceField.set(sessionController, authenticationService);
            
            java.lang.reflect.Field launchEventRepositoryField = SessionController.class.getDeclaredField("launchEventRepository");
            launchEventRepositoryField.setAccessible(true);
            launchEventRepositoryField.set(sessionController, launchEventRepository);
            
            java.lang.reflect.Field userRepositoryField = SessionController.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(sessionController, userRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetUserSessions_Success_ReturnsSessions() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session1 = new Session();
        session1.setId(1L);
        session1.setTitle("Breakout Session A");
        
        Session session2 = new Session();
        session2.setId(2L);
        session2.setTitle("Breakout Session B");
        
        Participation participation1 = new Participation();
        participation1.setSession(session1);
        participation1.setUser(targetUser);
        
        Participation participation2 = new Participation();
        participation2.setSession(session2);
        participation2.setUser(targetUser);
        
        List<Participation> participations = Arrays.asList(participation1, participation2);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(participationRepository.findByUserId(targetUser.getId())).thenReturn(participations);
        
        List<Map<String, Object>> result = sessionController.getUserSessions(2L, request);
        
        assertEquals(2, result.size());
        assertEquals("Breakout Session A", result.get(0).get("title"));
        assertEquals("Breakout Session B", result.get(1).get("title"));
    }
    
    @Test
    void testGetAvailableSessions_Success_ReturnsUnregisteredSessions() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session1 = new Session();
        session1.setId(1L);
        session1.setTitle("Session 1");
        
        Session session2 = new Session();
        session2.setId(2L);
        session2.setTitle("Session 2");
        
        Session session3 = new Session();
        session3.setId(3L);
        session3.setTitle("Session 3");
        
        Participation participation1 = new Participation();
        participation1.setSession(session1);
        participation1.setUser(targetUser);
        
        List<Participation> userParticipations = Arrays.asList(participation1);
        List<Session> allSessions = Arrays.asList(session1, session2, session3);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(participationRepository.findByUserId(targetUser.getId())).thenReturn(userParticipations);
        when(sessionRepository.findAll()).thenReturn(allSessions);
        
        List<Map<String, Object>> result = sessionController.getAvailableSessions(2L, request);
        
        assertEquals(2, result.size());
        assertEquals("Session 2", result.get(0).get("title"));
        assertEquals("Session 3", result.get(1).get("title"));
    }
    
    @Test
    void testAddUserToSession_Success_UserAdded() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session = new Session();
        session.setId(1L);
        session.setTitle("Breakout Session A");
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(participationRepository.findFirstBySessionAndUser(session, targetUser)).thenReturn(Optional.empty());
        
        Map<String, Object> result = sessionController.addUserToSession(2L, 1L, request);
        
        assertEquals(true, result.get("success"));
        verify(participationRepository).save(any(Participation.class));
    }
    
    @Test
    void testAddUserToSession_AlreadyRegistered_ReturnsError() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session = new Session();
        session.setId(1L);
        
        Participation existingParticipation = new Participation();
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(participationRepository.findFirstBySessionAndUser(session, targetUser)).thenReturn(Optional.of(existingParticipation));
        
        Map<String, Object> result = sessionController.addUserToSession(2L, 1L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("User already registered for this session", result.get("message"));
        verify(participationRepository, never()).save(any(Participation.class));
    }
    
    @Test
    void testGetAvailableSessions_NonAdmin_ReturnsEmpty() {
        User nonAdminUser = new User();
        nonAdminUser.setId(1L);
        nonAdminUser.setRole(Role.USER);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(nonAdminUser);
        
        List<Map<String, Object>> result = sessionController.getAvailableSessions(2L, request);
        
        assertEquals(0, result.size());
        verify(participationRepository, never()).findByUserId(any());
    }
    
    @Test
    void testAddUserToSession_NonAdminUser_ReturnsUnauthorized() {
        User nonAdminUser = new User();
        nonAdminUser.setId(1L);
        nonAdminUser.setRole(Role.USER);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(nonAdminUser);
        
        Map<String, Object> result = sessionController.addUserToSession(2L, 1L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("Unauthorized", result.get("message"));
        verify(participationRepository, never()).save(any(Participation.class));
    }
    
    @Test
    void testRemoveUserFromSpecificSession_Success_UserRemoved() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session = new Session();
        session.setId(1L);
        session.setTitle("Breakout Session A");
        
        Participation participation = new Participation();
        participation.setId(1L);
        participation.setSession(session);
        participation.setUser(targetUser);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(participationRepository.findFirstBySessionAndUser(session, targetUser)).thenReturn(Optional.of(participation));
        
        Map<String, Object> result = sessionController.removeUserFromSpecificSession(2L, 1L, request);
        
        assertEquals(true, result.get("success"));
        verify(participationRepository).delete(participation);
    }
    
    @Test
    void testRemoveUserFromSpecificSession_UserNotRegistered_ReturnsError() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        
        User targetUser = new User();
        targetUser.setId(2L);
        
        Session session = new Session();
        session.setId(1L);
        
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(participationRepository.findFirstBySessionAndUser(session, targetUser)).thenReturn(Optional.empty());
        
        Map<String, Object> result = sessionController.removeUserFromSpecificSession(2L, 1L, request);
        
        assertEquals(false, result.get("success"));
        assertEquals("User not registered for this session", result.get("message"));
        verify(participationRepository, never()).delete(any());
    }
}