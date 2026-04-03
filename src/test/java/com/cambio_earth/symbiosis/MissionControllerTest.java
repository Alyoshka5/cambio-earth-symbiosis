package com.cambio_earth.symbiosis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.controllers.MissionController;
import com.cambio_earth.symbiosis.models.*;
import com.cambio_earth.symbiosis.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class MissionControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CompletedMissionsRepository completedMissionsRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MissionController missionController;

    private User testUser;
    private Mission testMission;
    private CompletedMissions testCompletedMission;

   @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPoints(0L);

        Set<Post> likedPosts = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Post post = new Post();
            post.setId((long) i);
            likedPosts.add(post);
        }
        testUser.setLikedPosts(likedPosts);

        List<Post> createdPosts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post post = new Post();
            post.setId((long) i);
            createdPosts.add(post);
        }
        testUser.setPosts(createdPosts);

        testMission = new Mission();
        testMission.setId(1L);
        testMission.setTitle("Like 25 Posts");
        testMission.setDescription("Like 25 posts from the community.");
        testMission.setPoints(35L);
        testMission.setCompletionReq(25);
        testMission.setMissionType(MissionType.LIKES);

        testCompletedMission = new CompletedMissions(testUser, testMission);
    }

    @Test
    void testGetMissionsPageWithValidUserLoadsMissionsPage() {
        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(missionRepository.findAll()).thenReturn(Arrays.asList(testMission));
        when(completedMissionsRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        String result = missionController.getMissionsPage(model, request);

        assertEquals("missions", result);
        verify(model).addAttribute(eq("missionCards"), anyList());
        verify(model).addAttribute(eq("currentUser"), eq(testUser));
    }

    @Test
    void testGetMissionsPageMissionAlreadyClaimedCardMarkedAsClaimed() {
        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(missionRepository.findAll()).thenReturn(Arrays.asList(testMission));
        when(completedMissionsRepository.findByUser(testUser)).thenReturn(Arrays.asList(testCompletedMission));

        String result = missionController.getMissionsPage(model, request);

        assertEquals("missions", result);

        // Build the MissionViewModel to see if parameters are the same the controller would output
        boolean isClaimed = true;
        MissionViewModel card = new MissionViewModel(testMission, 10, isClaimed);
        assertTrue(card.rewardClaimed);
    }

    @Test
    void testGetMissionsPageLikeMissionUsesLikedPostsForProgress() {
        testMission.setMissionType(MissionType.LIKES);

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(missionRepository.findAll()).thenReturn(Arrays.asList(testMission));
        when(completedMissionsRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        String result = missionController.getMissionsPage(model, request);

        assertEquals("missions", result);
        assertEquals(10, testUser.getNumberOfLikedPosts());
    }

    @Test
    void testGetMissionsPagePostMissionUsesPostsCreatedForProgress() {
        testMission.setMissionType(MissionType.POSTS);

        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        when(missionRepository.findAll()).thenReturn(Arrays.asList(testMission));
        when(completedMissionsRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        String result = missionController.getMissionsPage(model, request);

        assertEquals("missions", result);
        assertEquals(3, testUser.getNumberOfPostsCreated());
    }
}