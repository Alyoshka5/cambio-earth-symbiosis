package com.cambio_earth.symbiosis.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.BreakoutBlockRanking;
import com.cambio_earth.symbiosis.models.BreakoutBlockRankingRepository;
import com.cambio_earth.symbiosis.models.LauanchEventRepository;
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
    BreakoutBlockRankingRepository rankingRepository;
        
    @Autowired
    private LauanchEventRepository launchEventRepository;

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
        List<Participation> participations = participationRepository.findAll();
        for (User user : users) {
            registerUserForMandatorySessions(user, mandatorySessions, participations);
        }
        participationRepository.saveAll(participations);
    }

    public void registerUserForMandatorySessions(User user, List<Session> mandatorySessions, List<Participation> participations) {
        for (Session session : mandatorySessions) {
            Participation participation = new Participation(user, session);
            if (!participations.contains(participation)) {
                participations.add(participation);
            }
        }
    }

    public void registerUsersForBreakoutSessions() {
        List<User> users = userRepository.findAll();

        List<Participation> participations = participationRepository.findAll();

        List<BreakoutBlockRanking> rankings = rankingRepository.findAll();
        rankings.sort(Comparator.comparing(BreakoutBlockRanking::getRank)); // Sort rankings so higher ranks are found first
        
        // Group sessions by the dateStartTime property
        List<Session> breakoutSessions = getBreakoutSessions();
        List<List<Session>> groupedBreakoutSessions = breakoutSessions.stream()
            .collect(Collectors.groupingBy(Session::getStartDateTime))
            .values()
            .stream()
            .collect(Collectors.toList());

        // Calculate how many users are already registered for each sessions (should be 0 but could have been manually added)
        Map<Session, Integer> sessionParticipationCounts = new HashMap<>();
        for (Session breakoutSession : breakoutSessions) {
            Integer sessionParticipationCount = participations.stream().filter(participation -> participation.getSession().getId().equals(breakoutSession.getId())).toList().size();
            sessionParticipationCounts.put(breakoutSession, sessionParticipationCount);
        }

        for (List<Session> currentBreakoutSessions : groupedBreakoutSessions) {
            Collections.shuffle(users); // Shuffle users to distribute priority fairly
            for (User user : users) {
                boolean continueToNextUser = registerUserForBreakoutSessions(user, rankings, currentBreakoutSessions, participations, groupedBreakoutSessions, sessionParticipationCounts);
                if (!continueToNextUser) break; // All current sessions at full capacity
            }
        }

        participationRepository.saveAll(participations);
    }

    public boolean registerUserForBreakoutSessions(
        User user, List<BreakoutBlockRanking> rankings,
        List<Session> currentBreakoutSessions,
        List<Participation> participations,
        List<List<Session>> groupedBreakoutSessions, 
        Map<Session, Integer> sessionParticipationCounts
    ) {
        // Find the user's rankings
        List<BreakoutBlockRanking> userRankings = rankings.stream().filter(ranking -> ranking.getUser().getId().equals(user.getId()) && currentBreakoutSessions.contains(ranking.getSession())).toList();
        boolean userRegistered = false;
        
        // Check if user is already registered for a session in the timeslot
        for (Session session : currentBreakoutSessions) {
            if (participations.contains(new Participation(user, session))) {
                userRegistered = true;
                break;
            }
        }
        if (userRegistered) return true;

        // Register user based on ranking
        for (BreakoutBlockRanking ranking : userRankings) {
            Integer sessionParticipationCount = sessionParticipationCounts.get(ranking.getSession());
            if (sessionParticipationCount < ranking.getSession().getCapacity()) {
                Participation participation = new Participation(user, ranking.getSession());
                participations.add(participation);
                sessionParticipationCounts.put(ranking.getSession(), sessionParticipationCount + 1);
                userRegistered = true;
                break;
            }
        }

        // Register user to least participated-in breakout session
        if (!userRegistered) {
            Session leastParticipatedSession = currentBreakoutSessions.get(0);
            double minParticipationRatio = (double) sessionParticipationCounts.get(leastParticipatedSession) / leastParticipatedSession.getCapacity();
            for (Session session: currentBreakoutSessions) {
                double sessionParticipationRatio = (double) sessionParticipationCounts.get(session) / session.getCapacity();
                if (sessionParticipationRatio < minParticipationRatio) {
                    leastParticipatedSession = session;
                    minParticipationRatio = sessionParticipationRatio;
                }
            }
            if (sessionParticipationCounts.get(leastParticipatedSession) < leastParticipatedSession.getCapacity()) { // If false, then all sessions are at full capacity
                Participation participation = new Participation(user, leastParticipatedSession);
                participations.add(participation);
                sessionParticipationCounts.put(leastParticipatedSession, sessionParticipationCounts.get(leastParticipatedSession) + 1);
            } else {
                return false; // Go to next group of breakout sessions since all current sessions are at full capacity
            }
        }

        return true;
    }

    public void registerUserAfterLaunch(User user) {
        // Data setup
        List<Session> mandatorySessions = sessionRepository.findByIsBreakoutFalse();
        List<Participation> participations = new ArrayList<>();
        List<BreakoutBlockRanking> rankings = new ArrayList<>();
        
        // Group sessions by the dateStartTime property
        List<Session> breakoutSessions = getBreakoutSessions();
        List<List<Session>> groupedBreakoutSessions = breakoutSessions.stream()
            .collect(Collectors.groupingBy(Session::getStartDateTime))
            .values()
            .stream()
            .collect(Collectors.toList());

        // Calculate how many users are already registered for each sessions (should be 0 but could have been manually added)
        Map<Session, Integer> sessionParticipationCounts = new HashMap<>();
        for (Session breakoutSession : breakoutSessions) {
            Integer sessionParticipationCount = participations.stream().filter(participation -> participation.getSession().getId().equals(breakoutSession.getId())).toList().size();
            sessionParticipationCounts.put(breakoutSession, sessionParticipationCount);
        }

        // Register user
        registerUserForMandatorySessions(user, mandatorySessions, participations);
        for (List<Session> currentBreakoutSessions : groupedBreakoutSessions) {
            registerUserForBreakoutSessions(user, rankings, currentBreakoutSessions, participations, groupedBreakoutSessions, sessionParticipationCounts);
        }

        participationRepository.saveAll(participations);
    }
}