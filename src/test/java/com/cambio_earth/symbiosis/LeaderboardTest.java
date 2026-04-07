package com.cambio_earth.symbiosis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import com.cambio_earth.symbiosis.controllers.RankingController;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.PostRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.PostService;
import com.cambio_earth.symbiosis.services.RankingService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LeaderboardTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthenticationService authenticationService;
    
    @Mock
    private RankingService mockRankingService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RankingService rankingService;

    @InjectMocks
    private RankingController rankingController;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        rankingController = new RankingController(mockRankingService, authenticationService);
    }

    // helper 
    private User makeUser(Long id, String name, Long points) {
        User u = new User();
        u.setId(id);
        u.setFirstName(name);
        u.setLastName("Test");
        u.setEmail(name + "@test.com");
        u.setPassword("pass12345");
        u.setPoints(points);
        return u;
    }

    private Post makePost(Long id) {
        Post p = new Post();
        p.setId(id);
        return p;
    }

    //RANKING SERVICE

    @Test
    void usersAreSortedByPointsDescending() {
        User a = makeUser(1L, "Alice", 10L);
        User b = makeUser(2L, "Bob", 30L);
        User c = makeUser(3L, "Charlie", 20L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(a, b, c));

        List<User> result = rankingService.getRankedUsers();

        assertEquals("Bob", result.get(0).getFirstName());
        assertEquals("Charlie", result.get(1).getFirstName());
        assertEquals("Alice", result.get(2).getFirstName());
    }

    @Test
    void tieBreaksAlphabetically() {
        User a = makeUser(1L, "Charlie", 20L);
        User b = makeUser(2L, "Alice", 20L);
        User c = makeUser(3L, "Bob", 20L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(a, b, c));

        List<User> result = rankingService.getRankedUsers();

        assertEquals("Alice", result.get(0).getFirstName());
        assertEquals("Bob", result.get(1).getFirstName());
        assertEquals("Charlie", result.get(2).getFirstName());
    }

    @Test
    void getUserRankWorksCorrectly() {
        User a = makeUser(1L, "Alice", 50L);
        User b = makeUser(2L, "Bob", 30L);

        List<User> list = Arrays.asList(a, b);

        int rank = rankingService.getUserRank(b, list);

        assertEquals(2, rank);
    }

    // CONTROLLER
    @Test
    void rankingPageLoadsCorrectly() {
        User user = makeUser(1L, "Alice", 50L);
        List<User> list = Arrays.asList(user);

        when(authenticationService.getUserFromRequest(request)).thenReturn(user);
        when(mockRankingService.getRankedUsers()).thenReturn(list);
        when(mockRankingService.getUserRank(user, list)).thenReturn(1);

        String view = rankingController.showRankings(request, model);

        assertEquals("ranking", view);
        verify(model).addAttribute("users", list);
        verify(model).addAttribute("currentUser", user);
        verify(model).addAttribute("rank", 1);
    }

    @Test
    void rankingPageHandlesNoUser() {
        when(authenticationService.getUserFromRequest(request)).thenReturn(null);
        when(rankingService.getRankedUsers()).thenReturn(Arrays.asList());

        String view = rankingController.showRankings(request, model);

        assertEquals("ranking", view);
        verify(model).addAttribute("rank", "N/A");
    }

    //  POST LIKE / POINTS 

    @Test
    void likingPostAddsPoint() {
        User user = makeUser(1L, "Alice", 10L);
        Post post = makePost(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(100)).thenReturn(Optional.of(post));
        when(postRepository.deleteLike(100L, 1L)).thenReturn(0);
        when(postRepository.insertLike(100L, 1L)).thenReturn(1);
        when(postRepository.insertLikePointAward(100L, 1L)).thenReturn(1);
        when(postRepository.findById(100)).thenReturn(Optional.of(post));

        postService.toggleLike(100L, 1L);

        assertEquals(11L, user.getPoints());
        verify(userRepository).save(user);
    }

    @Test
    void unlikingDoesNotAddPoint() {
        User user = makeUser(1L, "Alice", 10L);
        Post post = makePost(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(100)).thenReturn(Optional.of(post));
        when(postRepository.deleteLike(100L, 1L)).thenReturn(1);

        postService.toggleLike(100L, 1L);

        assertEquals(10L, user.getPoints());
        verify(userRepository, never()).save(user);
    }

    @Test
    void duplicateLikeDoesNotGiveExtraPoint() {
        User user = makeUser(1L, "Alice", 10L);
        Post post = makePost(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(100)).thenReturn(Optional.of(post));
        when(postRepository.deleteLike(100L, 1L)).thenReturn(0);
        when(postRepository.insertLike(100L, 1L)).thenReturn(1);
        when(postRepository.insertLikePointAward(100L, 1L)).thenReturn(0);

        postService.toggleLike(100L, 1L);

        assertEquals(10L, user.getPoints());
        verify(userRepository, never()).save(user);
    }

    @Test
    void returnsNullIfUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertNull(postService.toggleLike(100L, 1L));
    }

    @Test
    void returnsNullIfPostMissing() {
        User user = makeUser(1L, "Alice", 10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(100)).thenReturn(Optional.empty());

        assertNull(postService.toggleLike(100L, 1L));
    }
}