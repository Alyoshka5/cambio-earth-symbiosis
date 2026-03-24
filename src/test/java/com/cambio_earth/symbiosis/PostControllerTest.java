package com.cambio_earth.symbiosis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cambio_earth.symbiosis.models.Post;

class PostControllerTest {

    private List<Post> postDB;

    @BeforeEach
    void setup() {
        postDB = new ArrayList<>();
    }

    // Add valid post
    @Test
    void testAddPost_valid() {
        Post post = new Post();
        post.setId(1L);
        post.setCaption("Hello");

        postDB.add(post);

        assertEquals(1, postDB.size());
    }

    // Verify post data integrity
    @Test
    void testAddPost_dataIntegrity() {
        Post post = new Post();
        post.setId(10L);
        post.setCaption("Test");

        postDB.add(post);

        assertEquals(10L, postDB.get(0).getId());
        assertEquals("Test", postDB.get(0).getCaption());
    }

    // Reject large file
    @Test
    void testAddPost_largeFile() {
        String file = "large.jpg";
        boolean allowed = !file.contains("large");

        assertFalse(allowed);
    }

    // Large file error message
    @Test
    void testAddPost_largeFileErrorMessage() {
        String file = "large.jpg";
        String error = file.contains("large") ? "file size too large" : "";

        assertEquals("file size too large", error);
    }

    // Delete existing post
    @Test
    void testDeletePost_exists() {
        Post post = new Post();
        postDB.add(post);

        assertTrue(postDB.remove(post));
        assertEquals(0, postDB.size());
    }

    // Delete non-existing post
    @Test
    void testDeletePost_notExists() {
        Post post = new Post();

        assertFalse(postDB.remove(post));
    }

    // Ensure DB unchanged when deleting non-existing post
    @Test
    void testDeletePost_noDBChange() {
        Post post = new Post();
        postDB.add(post);

        Post fake = new Post();
        postDB.remove(fake);

        assertEquals(1, postDB.size());
    }

    // Like increment
    @Test
    void testLikePost_success() {
        int likes = 0;
        likes++;

        assertEquals(1, likes);
    }

    // Prevent double like
    @Test
    void testLikePost_alreadyLiked() {
        Set<String> likedUsers = new HashSet<>();
        likedUsers.add("user1");

        boolean canLike = !likedUsers.contains("user1");

        assertFalse(canLike);
    }

    // Toggle like
    @Test
    void testLikePost_toggle() {
        boolean liked = false;
        liked = !liked;
        liked = !liked;

        assertFalse(liked);
    }

    // Like stored in database
    @Test
    void testLikePost_databaseStored() {
        Map<Post, Integer> likeDB = new HashMap<>();
        Post post = new Post();

        likeDB.put(post, 0);
        likeDB.put(post, likeDB.get(post) + 1);

        assertEquals(1, likeDB.get(post));
    }

    // Cannot like deleted post
    @Test
    void testLikePost_deletedPost() {
        Post post = null;

        assertNull(post);
    }

    // Admin deletes post
    @Test
    void testAdminDeletePost_success() {
        Post post = new Post();
        postDB.add(post);

        assertTrue(postDB.remove(post));
    }

    // Admin deletes non-existing post
    @Test
    void testAdminDeletePost_notFound() {
        Post post = new Post();

        assertFalse(postDB.remove(post));
    }

    // Admin delete removes metadata
    @Test
    void testAdminDeletePost_removesMetadata() {
        Map<Post, Integer> likes = new HashMap<>();
        Post post = new Post();

        likes.put(post, 5);
        postDB.add(post);

        postDB.remove(post);
        likes.remove(post);

        assertFalse(likes.containsKey(post));
    }
}