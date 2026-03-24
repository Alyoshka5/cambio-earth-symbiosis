package com.cambio_earth.symbiosis.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="eventSettings")
public class LaunchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean started;

    // Constructors
    public LaunchEvent() {}
    public LaunchEvent(boolean started) {
        this.started = started;
    }

    // Getter
    public Long getId() { 
        return id; 
    }
    public boolean isStarted() {
        return started;
    }

    // Setter
    public void setStarted(boolean started) {
        this.started = started;
    }

}