package com.cambio_earth.symbiosis.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean enabled;

    private String verificationCode;
    
    private LocalDateTime verificationCodeExpiresAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BreakoutBlockRanking> sessionRankings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> likedPosts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompletedMissions> missionsComplete = new ArrayList<>(); 

    @ManyToMany
    @JoinTable(
        name = "liked_points_awarded",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> likePointAwardedPosts = new HashSet<>();
    private Long points = (long)0;

    // Constructors
    public User() {}

    public User(
            @NotBlank(message = "First name is required") @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters") String firstName,
            @NotBlank(message = "Last name is required") @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters") String lastName,
            @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password must be at least 8 characters") String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Set<Post> getLikePointAwardedPosts() {
        return likePointAwardedPosts;
    }

    public void setLikePointAwardedPosts(Set<Post> likePointAwardedPosts) {
        this.likePointAwardedPosts = likePointAwardedPosts;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean getEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public LocalDateTime getVerificationCodeExpiresAt() { return verificationCodeExpiresAt; }
    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) { this.verificationCodeExpiresAt = verificationCodeExpiresAt; }

    public Set<Participation> getParticipations() { return participations; }
    public void setParticipations(Set<Participation> participations) { this.participations = participations; }

    public List<BreakoutBlockRanking> getSessionRankings() { return sessionRankings; }
    public void setSessionRankings(List<BreakoutBlockRanking> sessionRankings) { this.sessionRankings = sessionRankings; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public Set<Post> getLikedPosts() { return likedPosts; }
    public void setLikedPosts(Set<Post> likedPosts) { this.likedPosts = likedPosts; }

    public Long getPoints() {return points;}
    public void setPoints(Long points) {this.points = points;}


    // Authentication
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return enabled; }

    // Helper functions
    public int getNumberOfLikedPosts() { return likedPosts.size(); }
    public int getNumberOfPostsCreated() { return posts.size(); }

    public boolean hasAwardedLikePointsFor(Post post) {
        return likePointAwardedPosts.stream()
                .anyMatch(p -> p.getId().equals(post.getId()));
    }

    public void markLikePointsAwardedFor(Post post) {
        likePointAwardedPosts.add(post);
    }
}

