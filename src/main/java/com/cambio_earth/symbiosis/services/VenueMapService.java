package com.cambio_earth.symbiosis.services;

import com.cambio_earth.symbiosis.models.VenueMap;
import com.cambio_earth.symbiosis.models.VenueMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class VenueMapService {

    @Autowired
    private VenueMapRepository venueMapRepository;


    private static final List<String> ALLOWED_TYPES = List.of(
        "application/pdf", "image/jpeg", "image/png"
    );

    // ── Add ──────────────────────────────────────────────────────────────────

    public VenueMap addMap(String fileUrl, String fileType, String title, String floorLevel) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("A map file must be provided.");
        }
        if (!ALLOWED_TYPES.contains(fileType)) {
            throw new IllegalArgumentException("Invalid file type.");
        }

        VenueMap map = new VenueMap();
        map.setTitle(title);
        map.setFloorLevel(floorLevel);
        map.setFilePath(fileUrl);    // Cloudinary URL stored here
        map.setFileType(fileType);
        map.setPublished(false);

        return venueMapRepository.save(map);
    }

    // ── Edit ─────────────────────────────────────────────────────────────────

    public VenueMap updateMap(Long id, String fileUrl, String fileType, String title, String floorLevel) {
        VenueMap map = venueMapRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Map can no longer be found."));

        // Only update the file if admin uploaded a new one
        if (fileUrl != null && !fileUrl.isBlank()) {
            if (!ALLOWED_TYPES.contains(fileType)) {
                throw new IllegalArgumentException("Invalid file type.");
            }
            map.setFilePath(fileUrl);
            map.setFileType(fileType);
            map.setPublished(false);
        }

        if (title != null && !title.isBlank()) map.setTitle(title);
        if (floorLevel != null && !floorLevel.isBlank()) map.setFloorLevel(floorLevel);

        return venueMapRepository.save(map);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    public void deleteMap(Long id) {
        VenueMap map = venueMapRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Map can no longer be found."));

        deleteFile(map.getFilePath());
        venueMapRepository.delete(map);
    }

    // ── Publish ──────────────────────────────────────────────────────────────

    public void publishChanges() {
        List<VenueMap> unpublished = venueMapRepository.findAll()
            .stream()
            .filter(m -> !m.isPublished())
            .toList();

        for (VenueMap map : unpublished) {
            map.setPublished(true);
        }

        venueMapRepository.saveAll(unpublished);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public List<VenueMap> getAllMaps() {
        return venueMapRepository.findAll();
    }

    public List<VenueMap> getPublishedMaps() {
        return venueMapRepository.findAllByPublishedTrue();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────



    private void deleteFile(String filePath) {
        if (filePath == null) return;
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            // Log warning but don't block the operation
            System.err.println("Warning: could not delete map file at " + filePath);
        }
    }

    public VenueMap getMapById(Long id) {
        return venueMapRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Map can no longer be found."));
    }
}