package com.cambio_earth.symbiosis.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.Participation;
import com.cambio_earth.symbiosis.models.ParticipationRepository;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ParticipationRepository participationRepository;

    public Map<LocalDate, List<Session>> getUserSchedule(User user) {
        List<Participation> participations = participationRepository.findByUserId(user.getId());
        List<Session> sessions = new ArrayList<>();
        for (Participation participation : participations) {
            Optional<Session> optionalSession = sessionRepository.findById(participation.getSession().getId());
            if (optionalSession.isPresent()) {
                sessions.add(optionalSession.get());
            }
        }

        Collections.sort(sessions);

        // Group sessions by date
        Map<LocalDate, List<Session>> scheduleDays = new HashMap<>();
        if (!sessions.isEmpty()) {
            LocalDateTime prevDateTime = sessions.get(0).getDatetime();

            List<Session> daySessions = new ArrayList<>();
            for (Session session: sessions) {
                if (prevDateTime.toLocalDate().equals(session.getDatetime().toLocalDate())) {
                    daySessions.add(session);
                } else {
                    scheduleDays.put(prevDateTime.toLocalDate(), daySessions);
                    prevDateTime = session.getDatetime();
                    daySessions = new ArrayList<>();
                    daySessions.add(session);
                }
            }
            scheduleDays.put(prevDateTime.toLocalDate(), daySessions); // Add last day
        }

        return scheduleDays;
    }
}