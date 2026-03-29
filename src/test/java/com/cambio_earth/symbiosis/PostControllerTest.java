package com.cambio_earth.symbiosis;
import com.cambio_earth.symbiosis.controllers.PostController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.controllers.PostController;
import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.PostRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.PostService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PostController postController;


    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@cambioearth.com");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setCaption("Test Caption");
        testPost.setImg("https://res.cloudinary.com/test/image.jpg");
        testPost.setUser(testUser);
    }

    @Test
    void testAddPost_ValidPhoto_Success() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("title", "My Conference Photo");
        inputs.put("caption", "Having a great time!");
        inputs.put("img", "https://res.cloudinary.com/test/photo.jpg");

        String result = postController.addPost(inputs, model, testUser);

        assertEquals("redirect:/home", result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testAddPost_InvalidFileType_ErrorDisplayed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("title", "Invalid Type");
        inputs.put("caption", "This has wrong format");
        inputs.put("img", "https://example.com/image.gif");

        String result = postController.addPost(inputs, model, testUser);

        assertEquals("/addPost", result);
        verify(model).addAttribute(eq("imgFormatErr"), anyString());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testAddPost_EmptyTitle_ErrorDisplayed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("title", "");
        inputs.put("caption", "Valid caption");
        inputs.put("img", "https://res.cloudinary.com/test/image.jpg");

        String result = postController.addPost(inputs, model, testUser);

        assertEquals("/addPost", result);
        verify(model).addAttribute(eq("titleErr"), anyString());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testAddPost_EmptyCaption_ErrorDisplayed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("title", "Valid Title");
        inputs.put("caption", "");
        inputs.put("img", "https://res.cloudinary.com/test/image.jpg");

        String result = postController.addPost(inputs, model, testUser);

        assertEquals("/addPost", result);
        verify(model).addAttribute(eq("captionErr"), anyString());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testLikePost_Success() {
        when(authenticationService.getUserFromRequest(request)).thenReturn(testUser);
        doNothing().when(postService).toggleLike(1L, testUser.getId());

        postController.likePost(request, 1L);

        verify(postService, times(1)).toggleLike(1L, testUser.getId());
    }

    @Test
    void testDeletePost_PostExists_Success() {
        Map<String, String> inputs = new HashMap<>();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userRepository.findByPostId(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(postRepository).delete(testPost);

        String result = postController.deletePost(1L, "/profile" , model, redirectAttributes);

        assertEquals("redirect:/profile/1", result);
        verify(postRepository, times(1)).delete(testPost);
    }

    @Test
    void testDeletePost_PostDoesNotExist_ErrorDisplayed() {
        Map<String, String> inputs = new HashMap<>();

        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findByPostId(1L)).thenReturn(Optional.of(testUser));

        String result = postController.deletePost(1L, "/profile" , model, redirectAttributes);


        assertEquals("redirect:/profile/1", result);
        verify(redirectAttributes).addFlashAttribute(eq("deleteErr"), anyString());
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void testHomePage_DisplaysPosts() {
        
        when(postRepository.findAll()).thenReturn(java.util.Arrays.asList(testPost));

        String result = postController.showHomePage(model, request);

        assertEquals("homePage", result);
        verify(model).addAttribute(eq("posts"), anyList());
    }
}