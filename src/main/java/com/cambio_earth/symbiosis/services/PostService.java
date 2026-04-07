package com.cambio_earth.symbiosis.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.PostRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public Post toggleLike(Long postId, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Post> optionalPost = postRepository.findById(postId.intValue());

        if (optionalUser.isEmpty() || optionalPost.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        int deleted = postRepository.deleteLike(postId, userId);

        if (deleted == 0) {
            postRepository.insertLike(postId, userId);

            int awarded = postRepository.insertLikePointAward(postId, userId);
            if (awarded > 0) {
                user.setPoints(user.getPoints() + 1);
                userRepository.save(user);
            }
        }

        return postRepository.findById(postId.intValue()).orElse(null);
    }
}