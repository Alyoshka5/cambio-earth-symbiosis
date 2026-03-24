package com.cambio_earth.symbiosis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.VenueMapController;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.VenueMap;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.VenueMapService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VenueMapControllerTest {

    @Mock
    private VenueMapService venueMapService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private VenueMapController venueMapController;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@cambioearth.com");
        adminUser.setRole(Role.ADMIN);
    }

    @Test
    void testAddMap_ValidFile_Success() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "map.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String result = venueMapController.addMap(request, validFile, "Main Lobby Map", "Floor 1", model);

        assertEquals("redirect:/maps", result);
        verify(venueMapService, times(1)).addMap(validFile, "Main Lobby Map", "Floor 1");
    }

    @Test
    void testAddMap_InvalidFileType_ErrorDisplayed() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "map.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        doThrow(new IllegalArgumentException("Invalid file type"))
                .when(venueMapService).addMap(invalidFile, "Test Map", "Floor 1");

        String result = venueMapController.addMap(request, invalidFile, "Test Map", "Floor 1", model);

        assertEquals("addMap", result);
        verify(model).addAttribute(eq("error"), anyString());
    }

    @Test
    void testAddMap_EmptyFile_ErrorDisplayed() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        doThrow(new IllegalArgumentException("A map file must be provided."))
                .when(venueMapService).addMap(emptyFile, "Test Map", "Floor 1");

        String result = venueMapController.addMap(request, emptyFile, "Test Map", "Floor 1", model);

        assertEquals("addMap", result);
        verify(model).addAttribute(eq("error"), anyString());
    }

    @Test
    void testUpdateMap_ValidFile_Success() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "map.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String result = venueMapController.updateMap(request, 1L, validFile, "Updated Map Title", "Floor 2", model);

        assertEquals("redirect:/maps", result);
        verify(venueMapService, times(1))
                .updateMap(eq(1L), any(), eq("Updated Map Title"), eq("Floor 2"));
    }

    @Test
    void testUpdateMap_InvalidFileType_ErrorDisplayed() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "map.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        when(venueMapService.getMapById(1L)).thenReturn(new VenueMap());

        doThrow(new IllegalArgumentException("Invalid file type"))
                .when(venueMapService)
                .updateMap(eq(1L), eq(invalidFile), eq("Updated Map"), eq("Floor 2"));

        String result = venueMapController.updateMap(request, 1L, invalidFile, "Updated Map", "Floor 2", model);

        assertEquals("editMap", result);
        verify(model).addAttribute(eq("error"), anyString());
    }

    @Test
    void testDeleteMap_MapExists_Success() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        doNothing().when(venueMapService).deleteMap(1L);

        String result = venueMapController.deleteMap(request, 1L, model);

        assertEquals("redirect:/maps", result);
        verify(venueMapService, times(1)).deleteMap(1L);
    }

    @Test
    void testDeleteMap_MapDoesNotExist_ErrorDisplayed() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        doThrow(new RuntimeException("Map can no longer be found."))
                .when(venueMapService).deleteMap(1L);

        String result = venueMapController.deleteMap(request, 1L, model);

        assertEquals("redirect:/maps", result);
        verify(model).addAttribute(eq("error"), anyString());
    }

    @Test
    void testPublishChanges_Success() throws Exception {
        when(authenticationService.getUserFromRequest(request)).thenReturn(adminUser);

        doNothing().when(venueMapService).publishChanges();

        String result = venueMapController.publishChanges(request, model);

        assertEquals("redirect:/maps", result);
        verify(venueMapService, times(1)).publishChanges();
    }
}