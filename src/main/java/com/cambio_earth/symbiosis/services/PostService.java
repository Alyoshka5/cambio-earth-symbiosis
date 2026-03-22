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
    public void toggleLike(Long postId, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalUser.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();

            if (post.getLikedBy().contains(user)) {
                post.getLikedBy().remove(user);
            } else {
                post.getLikedBy().add(user);
            }
        }
    }
}