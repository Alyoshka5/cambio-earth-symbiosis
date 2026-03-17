package com.cambio_earth.symbiosis.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.Session;
import com.cambio_earth.symbiosis.models.SessionRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;

@Service
public class SessionService {

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    // retrieve schedule of sessions grouped by day
    public Map<String, List<Session>> getUserSchedule(User user) {
        List<Participation> participations = participationRepository.findByUserId(user.getId());
        List<Session> sessions;
        if (user.getRole().equals(Role.ADMIN)) {
            sessions = sessionRepository.findAll();
        } else {
            sessions = new ArrayList<>();
            for (Participation participation : participations) {
                Optional<Session> optionalSession = sessionRepository.findById(participation.getSession().getId());
                if (optionalSession.isPresent()) {
                    sessions.add(optionalSession.get());
                }
            }
        }

        Collections.sort(sessions);

        // Group sessions by date
        Map<String, List<Session>> scheduleDays = new HashMap<>();
        if (!sessions.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE-MMM dd");

            LocalDateTime prevDateTime = sessions.get(0).getStartDateTime();

            List<Session> daySessions = new ArrayList<>();
            for (Session session: sessions) {
                if (prevDateTime.toLocalDate().equals(session.getStartDateTime().toLocalDate())) {
                    daySessions.add(session);
                } else {

                    scheduleDays.put(prevDateTime.format(formatter), daySessions);
                    prevDateTime = session.getStartDateTime();
                    daySessions = new ArrayList<>();
                    daySessions.add(session);
                }
            }
            scheduleDays.put(prevDateTime.format(formatter), daySessions); // Add last day
        }

        return scheduleDays;
    }

    public List<Session> getBreakoutSessions() {
        return sessionRepository.findByIsBreakoutTrue();
    }

    public void registerUsersForMandatorySessions() {
        List<Session> mandatorySessions = sessionRepository.findByIsBreakoutFalse();
        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            for (Session session : mandatorySessions) {
                Participation participation = new Participation(user, session);
                participationRepository.save(participation);
            }
        }
    }
}