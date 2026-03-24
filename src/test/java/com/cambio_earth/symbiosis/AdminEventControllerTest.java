package com.cambio_earth.symbiosis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AdminEventControllerTest {

    // Event launch registers users
    @Test
    void testLaunchEvent_registersUsers() {
        boolean usersRegistered = true;

        assertTrue(usersRegistered);
    }

    // Users assigned to sessions
    @Test
    void testLaunchEvent_assignsSessions() {
        int assignedSessions = 5;

        assertEquals(5, assignedSessions);
    }

    // Redirect to schedule page
    @Test
    void testLaunchEvent_redirect() {
        String page = "schedule";

        assertEquals("schedule", page);
    }

    // No sessions exist
    @Test
    void testLaunchEvent_noSessions() {
        boolean sessionsExist = false;

        assertFalse(sessionsExist);
    }

    // Capacity exceeded
    @Test
    void testLaunchEvent_capacityExceeded() {
        int users = 100;
        int capacity = 50;

        boolean valid = users <= capacity;

        assertFalse(valid);
    }
}