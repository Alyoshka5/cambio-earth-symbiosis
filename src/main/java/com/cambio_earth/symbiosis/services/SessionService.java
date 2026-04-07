package com.cambio_earth.symbiosis.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private SessionRepository sessionRepository;

    // retrieve schedule of sessions grouped by day
    public Map<String, List<Session>> getUserSchedule(User user) {
        List<Session> sessions;
        if (user.getRole().equals(Role.ADMIN)) {
            sessions = sessionRepository.findAll();
        } else {
            sessions = getUserRegisteredSessions(user);
        }
        
        Collections.sort(sessions);
        
        // Group sessions by date
        Map<String, List<Session>> scheduleDays = new LinkedHashMap<>();
        if (!sessions.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE-MMM dd");
            
            // Get first start datetime that isn't null
            LocalDateTime prevDateTime = null;
            for (Session session : sessions) {
                if (session.getStartDateTime() != null && session.getEndDateTime() != null) {
                    prevDateTime = session.getStartDateTime();
                    break;
                }
            }
            if (prevDateTime == null) return scheduleDays;
            
            List<Session> daySessions = new ArrayList<>();
            for (Session session: sessions) {
                if (session.getStartDateTime() == null || session.getEndDateTime() == null) continue;
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

    public List<Session> getUserRegisteredSessions(User user) {
        List<Participation> participations = participationRepository.findByUserId(user.getId());
        List<Session> sessions = new ArrayList<>();
        for (Participation participation : participations) {
            Optional<Session> optionalSession = sessionRepository.findById(participation.getSession().getId());
            if (optionalSession.isPresent()) {
                sessions.add(optionalSession.get());
            }
        }
        return sessions;
    }

    public List<Session> getBreakoutSessions() {
        return sessionRepository.findByIsBreakoutTrue();
    }

    public void registerUsersForMandatorySessions() {
        List<Session> mandatorySessions = sessionRepository.findByIsBreakoutFalse();
        Iterable<User> users = userRepository.findAll();
        Set<String> existingParticipations = participationRepository.findAll().stream()
            .map(p -> p.getUser().getId() + "_" + p.getSession().getId())
            .collect(Collectors.toSet());
        List<Participation> newParticipations = new ArrayList<>();
        
        for (User user : users) {
            registerUserForMandatorySessions(user, mandatorySessions, existingParticipations, newParticipations);
        }
        participationRepository.saveAll(newParticipations);
    }

    public List<Session> getSessionsMissingDateTimes() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
            .filter(s -> s.getStartDateTime() == null || s.getEndDateTime() == null)
            .collect(Collectors.toList());
    }

    public void registerUserForMandatorySessions(User user, List<Session> mandatorySessions, Set<String> existingParticipations, List<Participation> newParticipations) {
        for (Session session : mandatorySessions) {
            if (!existingParticipations.contains(user.getId() + "_" + session.getId())) {
                Participation participation = new Participation(user, session);
                newParticipations.add(participation);
                existingParticipations.add(user.getId() + "_" + session.getId());
            }
        }
    }

    public void registerUsersForBreakoutSessions() {
        List<User> users = userRepository.findAll();
        List<Participation> allParticipations = participationRepository.findAll();
        List<BreakoutBlockRanking> allRankings = rankingRepository.findAll();
        List<Session> breakoutSessions = getBreakoutSessions();

        Map<Long, List<BreakoutBlockRanking>> rankingsByUser = allRankings.stream()
            .sorted(Comparator.comparing(BreakoutBlockRanking::getRank))
            .collect(Collectors.groupingBy(ranking -> ranking.getUser().getId()));

        Set<String> existingParticipations = allParticipations.stream()
            .map(p -> p.getUser().getId() + "_" + p.getSession().getId())
            .collect(Collectors.toSet());

        // Calculate how many users are already registered for each sessions (should be 0 but could have been manually added)
        Map<Long, Integer> sessionParticipationCounts = new HashMap<>();
        for (Session session : breakoutSessions) {
            long count = allParticipations.stream().filter(participation -> participation.getSession().getId().equals(session.getId())).count();
            sessionParticipationCounts.put(session.getId(), (int) count);
        }
        
        // Group sessions by the startDateTime property
        Map<LocalDateTime, List<Session>> groupedBreakoutSessions = breakoutSessions.stream()
            .collect(Collectors.groupingBy(Session::getStartDateTime));

        List<Participation> newParticipations = new ArrayList<>();

        for (List<Session> currentBreakoutSessions : groupedBreakoutSessions.values()) {
            Collections.shuffle(users); // Shuffle users to distribute priority fairly
            for (User user : users) {
                List<BreakoutBlockRanking> userRankings = rankingsByUser.getOrDefault(user.getId(), List.of());
                boolean continueToNextUser = registerUserForBreakoutSessions(user, userRankings, currentBreakoutSessions, existingParticipations, newParticipations, sessionParticipationCounts);
                if (!continueToNextUser) break; // All current sessions at full capacity
            }
        }

        participationRepository.saveAll(newParticipations);
    }

    public boolean registerUserForBreakoutSessions(
        User user, 
        List<BreakoutBlockRanking> userRankings,
        List<Session> currentBreakoutSessions,
        Set<String> existingParticipations,
        List<Participation> newParticipations,
        Map<Long, Integer> sessionParticipationCounts
    ) {
        boolean userRegistered = false;
        
        // Check if user is already registered for a session in the timeslot
        if (currentBreakoutSessions.stream().anyMatch(session -> existingParticipations.contains(user.getId() + "_" + session.getId()))) {
            return true;
        }

        // Register user based on ranking
        for (BreakoutBlockRanking ranking : userRankings) {
            Session targetSession = ranking.getSession();
            if (currentBreakoutSessions.contains(targetSession) && sessionParticipationCounts.get(targetSession.getId()) < targetSession.getCapacity()) {
                Participation participation = new Participation(user, targetSession);
                Integer sessionParticipationCount = sessionParticipationCounts.get(targetSession.getId());
                
                newParticipations.add(participation);
                sessionParticipationCounts.put(targetSession.getId(), sessionParticipationCount + 1);
                existingParticipations.add(user.getId() + "_" + targetSession.getId());

                userRegistered = true;
                break;
            }
        }

        if (!userRegistered) {
            Session leastParticipatedSession = currentBreakoutSessions.stream()
                    .filter(s -> sessionParticipationCounts.get(s.getId()) < s.getCapacity())
                    .min(Comparator.comparingDouble(s -> (double) sessionParticipationCounts.get(s.getId()) / s.getCapacity()))
                    .orElse(null);

            if (leastParticipatedSession != null) {
                Participation participation = new Participation(user, leastParticipatedSession);
                Integer sessionParticipationCount = sessionParticipationCounts.get(leastParticipatedSession.getId());
                
                newParticipations.add(participation);
                sessionParticipationCounts.put(leastParticipatedSession.getId(), sessionParticipationCount + 1);
                existingParticipations.add(user.getId() + "_" + leastParticipatedSession.getId());
            } else {
                return false;
            }
        }

        return true;
    }

    public void registerUserAfterLaunch(User user) {
        // Data setup
        List<Session> mandatorySessions = sessionRepository.findByIsBreakoutFalse();
        Set<String> existingParticipations = new HashSet<>();

        List<Participation> allParticipations = participationRepository.findAll();
        List<BreakoutBlockRanking> allRankings = rankingRepository.findAll();
        List<Session> breakoutSessions = getBreakoutSessions();

        Map<Long, List<BreakoutBlockRanking>> rankingsByUser = allRankings.stream()
            .sorted(Comparator.comparing(BreakoutBlockRanking::getRank))
            .collect(Collectors.groupingBy(ranking -> ranking.getUser().getId()));

        // Calculate how many users are already registered for each sessions (should be 0 but could have been manually added)
        Map<Long, Integer> sessionParticipationCounts = new HashMap<>();
        for (Session session : breakoutSessions) {
            long count = allParticipations.stream().filter(participation -> participation.getSession().getId().equals(session.getId())).count();
            sessionParticipationCounts.put(session.getId(), (int) count);
        }
        
        // Group sessions by the startDateTime property
        Map<LocalDateTime, List<Session>> groupedBreakoutSessions = breakoutSessions.stream()
            .collect(Collectors.groupingBy(Session::getStartDateTime));

        List<Participation> newParticipations = new ArrayList<>();

        // Register user
        registerUserForMandatorySessions(user, mandatorySessions, existingParticipations, newParticipations);
        for (List<Session> currentBreakoutSessions : groupedBreakoutSessions.values()) {
            List<BreakoutBlockRanking> userRankings = rankingsByUser.getOrDefault(user.getId(), List.of());
            registerUserForBreakoutSessions(user, userRankings, currentBreakoutSessions, existingParticipations, newParticipations, sessionParticipationCounts);
        }

        participationRepository.saveAll(newParticipations);
    }
}