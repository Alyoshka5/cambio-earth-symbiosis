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

    // Set this in application.properties: map.upload.dir=uploads/maps
    @Value("${map.upload.dir:uploads/maps}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
        "application/pdf", "image/jpeg", "image/png"
    );

    // ── Add ──────────────────────────────────────────────────────────────────

    public VenueMap addMap(MultipartFile file, String title, String floorLevel) throws IOException {
        validateFile(file);

        String filePath = saveFile(file);

        VenueMap map = new VenueMap();
        map.setTitle(title);
        map.setFloorLevel(floorLevel);
        map.setFilePath(filePath);
        map.setFileType(file.getContentType());
        map.setPublished(false);

        return venueMapRepository.save(map);
    }

    // ── Edit ─────────────────────────────────────────────────────────────────

    public VenueMap updateMap(Long id, MultipartFile file, String title, String floorLevel) throws IOException {
        VenueMap map = venueMapRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Map can no longer be found."));

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            deleteFile(map.getFilePath());   // remove old file from disk
            String newFilePath = saveFile(file);
            map.setFilePath(newFilePath);
            map.setFileType(file.getContentType());
        } else if (file != null && file.isEmpty()) {
            // File field was cleared but no new file provided
            throw new IllegalArgumentException("A map file must be provided.");
        }

        if (title != null && !title.isBlank()) map.setTitle(title);
        if (floorLevel != null && !floorLevel.isBlank()) map.setFloorLevel(floorLevel);
        map.setPublished(false); // unpublish until admin publishes again

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

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A map file must be provided.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type was provided.");
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }

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