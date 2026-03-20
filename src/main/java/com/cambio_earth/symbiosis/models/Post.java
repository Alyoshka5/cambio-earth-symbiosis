package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String title;
    private String caption;
    private int likes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public int getLikes() {
        return this.likes;
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

    public void setLikes(int likes) {
        this.likes = likes;
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
}
