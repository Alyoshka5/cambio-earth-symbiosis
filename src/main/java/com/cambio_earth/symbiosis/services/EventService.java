package com.cambio_earth.symbiosis.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.LauanchEventRepository;
import com.cambio_earth.symbiosis.models.LaunchEvent;

@Service
public class EventService {
    @Autowired
    private LauanchEventRepository launchEventRepository;

    // Check if the event was launched, and create an event if one does not exist
    public boolean isEventLaunched() {
        List<LaunchEvent> events = launchEventRepository.findAll();
        if (events.isEmpty()) {
            LaunchEvent event = new LaunchEvent(false);
            launchEventRepository.save(event);
            return false;
        }
        
        return events.get(0).isStarted();
    }

    public boolean launchEvent() {
        if (isEventLaunched()) {
            return false;
        }
        
        try {
            LaunchEvent event = launchEventRepository.findAll().stream().findFirst().orElse(null);
            if (event != null) {
                event.setStarted(true);
                launchEventRepository.save(event);
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            return false;
        }
    }
}