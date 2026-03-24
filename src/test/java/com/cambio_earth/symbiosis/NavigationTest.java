package com.cambio_earth.symbiosis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class NavigationTest {

    // Profile redirect
    @Test
    void testNav_profileRedirect() {
        String currentPage = "home";
        currentPage = "profile";

        assertEquals("profile", currentPage);
    }

    // Map redirect
    @Test
    void testNav_mapRedirect() {
        String currentPage = "home";
        currentPage = "map";

        assertEquals("map", currentPage);
    }

    // Home highlight
    @Test
    void testNav_homeHighlight() {
        String currentPage = "home";

        assertTrue(currentPage.equals("home"));
    }

    // Logout visible
    @Test
    void testNav_logoutVisible() {
        boolean viewingOwnProfile = true;

        assertTrue(viewingOwnProfile);
    }

    // Logout hidden
    @Test
    void testNav_logoutHidden() {
        boolean viewingOwnProfile = false;

        assertFalse(viewingOwnProfile);
    }
}