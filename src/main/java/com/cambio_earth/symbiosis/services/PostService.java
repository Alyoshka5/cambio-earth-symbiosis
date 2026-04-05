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

        if (optionalUser.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();

            if (post.getLikedBy().contains(user)) {
                post.getLikedBy().remove(user);
            } else {
                post.getLikedBy().add(user);

                if (!user.getLikePointAwardedPosts().contains(post)) {
                    user.setPoints(user.getPoints() + 1);
                    user.getLikePointAwardedPosts().add(post);
                    userRepository.save(user);
                }
            }

            return postRepository.save(post);
        }

        return null;
    }
}