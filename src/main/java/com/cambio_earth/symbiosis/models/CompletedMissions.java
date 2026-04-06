package com.cambio_earth.symbiosis.models;

import jakarta.persistence.*;

@Entity
@Table(name="completedMissions")
public class CompletedMissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    // Constructors
    public CompletedMissions() {};
    public CompletedMissions(User user, Mission misison) {
        this.user = user;
        this.mission = misison;
    }

    // Setters
    public void setId(Long id) {
    this.id = id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setMission(Mission mission) {
        this.mission = mission;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public Mission getMission() {
        return mission;
    }
    
}