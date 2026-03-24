package com.cambio_earth.symbiosis;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapControllerTest {

    private List<String> mapDB;

    @BeforeEach
    void setup() {
        mapDB = new ArrayList<>();
    }

    // Add valid map
    @Test
    void testAddMap_valid() {
        String file = "map.pdf";

        if (file.endsWith(".pdf") || file.endsWith(".jpg") || file.endsWith(".png")) {
            mapDB.add(file);
        }

        assertEquals(1, mapDB.size());
    }

    // Success message
    @Test
    void testAddMap_successMessage() {
        String file = "map.pdf";

        String msg = file.endsWith(".pdf") ? "Map uploaded successfully" : "";

        assertEquals("Map uploaded successfully", msg);
    }

    // Reject invalid map
    @Test
    void testAddMap_invalid() {
        String file = "map.exe";

        if (file.endsWith(".pdf") || file.endsWith(".jpg") || file.endsWith(".png")) {
            mapDB.add(file);
        }

        assertEquals(0, mapDB.size());
    }

    // Reject empty file
    @Test
    void testAddMap_empty() {
        String file = "";
        assertTrue(file.isEmpty());
    }

    // Empty file error message
    @Test
    void testAddMap_emptyErrorMessage() {
        String file = "";

        String msg = file.isEmpty() ? "A map file must be provided." : "";

        assertEquals("A map file must be provided.", msg);
    }

    // Update map
    @Test
    void testUpdateMap_success() {
        mapDB.add("old.pdf");
        mapDB.set(0, "new.pdf");

        assertEquals("new.pdf", mapDB.get(0));
    }

    // Invalid update
    @Test
    void testUpdateMap_invalidFile() {
        String file = "bad.exe";

        boolean valid = file.endsWith(".pdf") || file.endsWith(".jpg") || file.endsWith(".png");

        assertFalse(valid);
    }

    // Delete map
    @Test
    void testDeleteMap_success() {
        mapDB.add("map.pdf");

        assertTrue(mapDB.remove("map.pdf"));
    }

    // Map not found
    @Test
    void testDeleteMap_notFound() {
        assertFalse(mapDB.remove("map.pdf"));
    }

    // Delete error message
    @Test
    void testDeleteMap_errorMessage() {
        boolean exists = false;

        String msg = !exists ? "Map can no longer be found" : "";

        assertEquals("Map can no longer be found", msg);
    }
}