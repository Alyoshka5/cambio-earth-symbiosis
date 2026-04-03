package com.cambio_earth.symbiosis.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;

@Service
public class RankingService {

    private final UserRepository userRepository;

    public RankingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getRankedUsers() {
        List<User> users = userRepository.findAll();

        users.sort(
                Comparator.comparing(User::getPoints).reversed()
                        .thenComparing(user -> user.getFirstName().toLowerCase())
        );

        return users;
    }

    public int getUserRank(User currentUser, List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(currentUser.getId())) {
                return i + 1;
            }
        }
        return -1;
    }
}