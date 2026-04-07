package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String title;
    private String caption;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy = new HashSet<>();

    @ManyToMany(mappedBy = "likePointAwardedPosts")
    private Set<User> likePointAwardedByUsers = new HashSet<>();

    // Constructors
    public Post() {}

    public Post(String imgInput, String titleInput, String captionInput) {
        img = imgInput;
        title = titleInput;
        caption = captionInput;
        createdAt = LocalDateTime.now();
    }

    public Post(User userInput, String imgInput, String titleInput, String captionInput) {
        user = userInput;
        img = imgInput;
        title = titleInput;
        caption = captionInput;
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return this.id;
    }

    public String getImg() {
        return this.img;
    }
    
    public String getTitle() {
        return this.title;
    }

    public String getCaption() {
        return this.caption;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public User getUser() {
        return this.user;
    }

    public Set<User> getLikedBy() {
        return this.likedBy;
    }

    public Set<User> getLikePointAwardedByUsers() {
        return this.likePointAwardedByUsers;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLikedBy(Set<User> likedBy) {
        this.likedBy = likedBy;
    }

    // Helper methods
   public String toJSON(User user) {
    String formattedCreatedAt = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    String json = "{\"id\": %d, \"title\": \"%s\", \"caption\": \"%s\", \"img\": \"%s\", \"likes\": %d, \"createdAt\": \"%s\", \"liked\": %b}";

    return String.format(json, id, title, caption, img, likedBy.size(), formattedCreatedAt, likedBy.contains(user));
}
}
