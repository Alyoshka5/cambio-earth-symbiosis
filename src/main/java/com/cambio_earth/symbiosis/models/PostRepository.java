package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByCreatedAt(LocalDateTime createdAt);
    Optional<Post> findById(int postId);

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query(value = "SELECT EXISTS (SELECT 1 FROM post_likes WHERE post_id = :postId AND user_id = :userId)", nativeQuery = true)
    boolean existsLike(@Param("postId") Long postId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM post_likes WHERE post_id = :postId AND user_id = :userId", nativeQuery = true)
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);

    @Modifying
    @Query(value = """
        INSERT INTO post_likes (post_id, user_id)
        VALUES (:postId, :userId)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    int insertLike(@Param("postId") Long postId, @Param("userId") Long userId);

    @Modifying
    @Query(value = """
        INSERT INTO liked_points_awarded (post_id, user_id)
        VALUES (:postId, :userId)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    int insertLikePointAward(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM post_likes WHERE post_id = :postId", nativeQuery = true)
    int countLikes(@Param("postId") Long postId);
}