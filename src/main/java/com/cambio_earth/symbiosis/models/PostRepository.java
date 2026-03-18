package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByCreatedAt(LocalDateTime createdAt);

    // Get all posts by a specific user, newest first (descending order)
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
