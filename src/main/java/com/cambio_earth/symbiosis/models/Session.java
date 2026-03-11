package com.cambio_earth.symbiosis.models;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="sessions")
public class Session implements Comparable<Session> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters")
    private String title;

    private String location;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private String description;

    @ElementCollection
    @CollectionTable(name = "session_speakers", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "speaker")
    private List<String> speakers = new ArrayList<>();

    private boolean isBreakout;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BreakoutBlockRanking> sessionRankings = new ArrayList<>();

    // Constructors
    public Session() {}

    public Session(
            @NotBlank(message = "Title is required") @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters") String title,
            String location, LocalDateTime startDateTime, LocalDateTime endDateTime, String description, List<String> speakers,
            @NotBlank(message = "Must specify if session is a breakout session") boolean isBreakout) {
        this.title = title;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
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

   public LocalDateTime getStartDateTime() { return startDateTime; }
   public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

   public LocalDateTime getEndDateTime() { return endDateTime; }
   public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
    }

    public boolean isBreakout() {
        return isBreakout;
    }

    public void setBreakout(boolean isBreakout) {
        this.isBreakout = isBreakout;
    }

    public Set<Participation> getParticipations() {
        return participations;
    }

    public void setParticipations(Set<Participation> participations) {
        this.participations = participations;
    }

    public List<BreakoutBlockRanking> getSessionRankings() {
        return sessionRankings;
    }

    public void setSessionRankings(List<BreakoutBlockRanking> sessionRankings) {
        this.sessionRankings = sessionRankings;
    }

    // Helper methods
    public String getTimeRange() {
        return formatTime(startDateTime) + " - " + formatTime(endDateTime);
    }
    
    private String formatTime(LocalDateTime dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
        Date convertedDateTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        String formattedDate = formatter.format(convertedDateTime);

        if (formattedDate.charAt(0) == '0') {
            formattedDate = formattedDate.substring(1);
        }
        return formattedDate.replaceAll("[.]", "");
    }

    public String getSpeakersString() {
        if (speakers == null || speakers.isEmpty()) {
            return "";
        }
        return String.join(", ", speakers);
    }

    // Comparable
    @Override
    public int compareTo(Session otherSession) {
        return this.getStartDateTime().compareTo(otherSession.getStartDateTime());
    }
}

