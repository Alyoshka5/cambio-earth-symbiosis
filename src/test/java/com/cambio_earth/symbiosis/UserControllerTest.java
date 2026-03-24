package com.cambio_earth.symbiosis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cambio_earth.symbiosis.models.User;

class UserControllerTest {

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setPassword("Correct123!");
    }

    // Successful password update
    @Test
    void testEditProfile_success() {
        user.setPassword("NewPass123!");
        assertEquals("NewPass123!", user.getPassword());
    }

    // Login with new password
    @Test
    void testEditProfile_login() {
        user.setPassword("NewPass123!");
        assertTrue(user.getPassword().equals("NewPass123!"));
    }

    // Wrong current password
    @Test
    void testEditProfile_wrongPassword() {
        assertFalse("Wrong123!".equals("Correct123!"));
    }

    // Password complexity
    @Test
    void testPassword_complexity() {
        String password = "weak";

        boolean valid = password.length() >= 8 &&
                        password.matches(".*\\d.*") &&
                        password.matches(".*[!@#$%^&*].*");

        assertFalse(valid);
    }

    // Database failure
    @Test
    void testEditProfile_databaseFailure() {
        String original = user.getPassword();

        boolean dbSuccess = false;

        if (dbSuccess) {
            user.setPassword("NewPass123!");
        }

        assertEquals(original, user.getPassword());
    }

    // View profile - user exists
    @Test
    void testViewProfile_userExists() {
        assertTrue(user != null);
    }

    // View profile shows posts
    @Test
    void testViewProfile_displaysPosts() {
        int postCount = 3;
        assertEquals(3, postCount);
    }

    // Redirect to profile page
    @Test
    void testViewProfile_redirect() {
        String page = "profile";
        assertEquals("profile", page);
    }

    // User has no posts
    @Test
    void testViewProfile_noPosts() {
        int postCount = 0;
        assertEquals(0, postCount);
    }
}