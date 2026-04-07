package com.cambio_earth.symbiosis;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.AdminSessionController;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.EventService;
import com.cambio_earth.symbiosis.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class AdminSessionControllerTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SessionService sessionService;

    @Mock
    private EventService eventService;

    @Mock
    private Model model;
    private AdminSessionController adminSessionController;
    private Session breakoutSession;

    @BeforeEach
    void setUp() {
        // Create controller with mocked services (constructor injection)
        adminSessionController = new AdminSessionController(sessionService, eventService);
        // Manually inject the mocked repositories (field injection)
        ReflectionTestUtils.setField(adminSessionController, "sessionRepository", sessionRepository);
        ReflectionTestUtils.setField(adminSessionController, "userRepository", userRepository);
        ReflectionTestUtils.setField(adminSessionController, "participationRepository", participationRepository);

        breakoutSession = new Session();
        breakoutSession.setId(1L);
        breakoutSession.setTitle("Breakout Session A");
        breakoutSession.setBreakout(true);
        breakoutSession.setCapacity(50);
        breakoutSession.setStartDateTime(LocalDateTime.now().plusDays(1));
        breakoutSession.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(1));

    }

    @Test
    void testCreateSession_WithCapacity_Success() {
        // Treat as a new session (no ID) – forces save instead of findById
        breakoutSession.setId(null);
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });
        String result = adminSessionController.saveSession(
                breakoutSession, "2024-12-15", "10:00", "11:00", "Speaker 1,Speaker 2", 50
        );
        assertEquals("redirect:/sessions/1", result);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testDeleteSession_Success() {
        doNothing().when(sessionRepository).deleteById(1L);
        String result = adminSessionController.deleteSession(1L);
        assertEquals("redirect:/sessions/schedule", result);
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetNewSessionForm_Success() {
        String result = adminSessionController.getNewSessionForm(model);
        assertEquals("sessions/AdminSessionForm", result);
        verify(model).addAttribute(eq("eventSession"), any(Session.class));
    }

    @Test
    void testGetEditSessionForm_Success() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(breakoutSession));
        String result = adminSessionController.getEditSessionForm(1L, model);
        assertEquals("sessions/AdminSessionForm", result);
        verify(model).addAttribute(eq("eventSession"), eq(breakoutSession));
    }
}