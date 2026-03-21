package com.cambio_earth.symbiosis.models;

import jakarta.persistence.*;

@Entity
@Table(name = "venue_maps")
public class VenueMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String floorLevel;   // e.g. "Floor 3", "Main Lobby"
    private String filePath;     // path to stored file on disk
    private String fileType;     // "PDF", "JPEG", "PNG"
    private boolean published;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFloorLevel() { return floorLevel; }
    public void setFloorLevel(String floorLevel) { this.floorLevel = floorLevel; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}