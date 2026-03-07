package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters")
    private String title;

    private String location;

    private LocalDateTime datetime;

    private String description;

    private String[] speakers;

    @NotBlank(message = "Must specify if the session is a breakout session")
    private boolean isBreakout;

    // Constructors
    public Session() {}

    public Session(
            @NotBlank(message = "Title is required") @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters") String title,
            String location, LocalDateTime datetime, String description, String[] speakers,
            @NotBlank(message = "Must specify if session is a breakout session") boolean isBreakout) {
        this.title = title;
        this.location = location;
        this.datetime = datetime;
        this.description = description;
        this.speakers = speakers;
        this.isBreakout = isBreakout;
    }

    public Session(
            @NotBlank(message = "Title is required") @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters") String title,
            @NotBlank(message = "Must specify if session is a breakout session") boolean isBreakout) {
        this.title = title;
        this.isBreakout = isBreakout;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSpeakers() {
        return speakers;
    }

    public void setSpeakers(String[] speakers) {
        this.speakers = speakers;
    }

    public boolean isBreakout() {
        return isBreakout;
    }

    public void setBreakout(boolean isBreakout) {
        this.isBreakout = isBreakout;
    }
}

