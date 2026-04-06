package com.cambio_earth.symbiosis.models;

import jakarta.persistence.*;

@Entity
@Table(name="missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long points;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private MissionType missionType; // POSTS or POINTS related
    private int completionReq; // Number of posts or likes required to complete the mission

    // Constructors
    public Mission() {};
    public Mission(Long points, String title, String description, MissionType missionType, int completionReq) {
        this.points = points;
        this.title = title;
        this.description = description;
        this.missionType = missionType;
        this.completionReq = completionReq;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setPoints(Long points) {
        this.points = points;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }
    public void setCompletionReq(int completionReq) {
        this.completionReq = completionReq;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public Long getPoints() {
        return points;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public MissionType getMissionType() {
        return missionType;
    }
    public int getCompletionReq() {
        return completionReq;
    }
    
}