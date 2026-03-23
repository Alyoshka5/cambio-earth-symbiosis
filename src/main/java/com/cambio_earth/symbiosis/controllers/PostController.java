package com.cambio_earth.symbiosis.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.PostRepository;
import com.cambio_earth.symbiosis.models.Role;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;
import com.cambio_earth.symbiosis.services.AuthenticationService;
import com.cambio_earth.symbiosis.services.PostService;

import jakarta.servlet.http.HttpServletRequest;




@Controller
public class PostController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    AuthenticationService authenticationService;

    // Load the add post details form page
    @GetMapping("posts/form")
    public String getPostFormPage() {
        return "/addPost";
    }
    
    // Process adding a new post
    @PostMapping("posts/add")
    public String addPost(@RequestParam Map<String, String> inputs, Model model, @AuthenticationPrincipal User currentUser) {
        String newTitle = inputs.get("title");
        String newCaption = inputs.get("caption");
        String newImg = inputs.get("img");

        // Error handle
        boolean errors = false;
        if (newTitle == null || newTitle.isBlank()) {
            model.addAttribute("titleErr", "A title must be entered");
            errors = true;
        }
        if (newCaption == null || newCaption.isBlank()) {
            model.addAttribute("captionErr", "A caption must be entered");
            errors = true;
        }
        if (currentUser == null) {
            model.addAttribute("err", "Could not remove post. User no longer exists.");
            errors = true;
        }
        if (newImg == null || newImg.isBlank()) {
            model.addAttribute("imgErr", "An image must be provided");
            errors = true;
        } else {

            // Error handle incorrect image format
            List<String> imgFormats = List.of("jpg", "jpeg", "png", "webp");
            int dotIndex = newImg.lastIndexOf('.');

            // No extension found at all
            if (dotIndex == -1) {
                model.addAttribute("imgFormatErr", "Invalid file type. Allowed types: jpg, jpeg, png, webp");
                errors = true;
            } else {
                String extension = newImg.substring(dotIndex + 1).toLowerCase();
                if (imgFormats.contains(extension) == false) {
                    model.addAttribute("imgFormatErr", "Invalid file type. Allowed types: jpg, jpeg, png, webp");
                    errors = true;
                }
            }
            if (!newImg.startsWith("https://res.cloudinary.com/")) {
                model.addAttribute("imgError", "Invalid image source.");
                errors = true;
            }
        }
        if (errors) {
            return "/addPost";
        }
        // Add the post to the database
        Post newPost = new Post(currentUser, newImg, newTitle, newCaption);
        postRepository.save(newPost);
        return "redirect:/home";
    }
    
    
    @PostMapping("/posts/{postId}/like")
    @ResponseBody
    public void likePost(HttpServletRequest request, @PathVariable Long postId) {
        User user = authenticationService.getUserFromRequest(request);

        postService.toggleLike(postId, user.getId());
    }

    // Process deleteing a post
    @PostMapping("/posts/delete/{postId}")
    public String deletePost(@PathVariable Long postId, @RequestParam(value = "redirectUrl", defaultValue = "/home") String redirectTo, Model model, RedirectAttributes redirectAttributes) {

        // Find the user who made the post to return to their profile
        Post unwantedPost = postRepository.findById(postId).orElse(null);
        Optional<User> userWithPost = userRepository.findByPostId(postId);
        User user = userWithPost.orElse(null);
        String redirectUrl = "redirect:/home";

        // Error handle the profile or post no longer existing
        if (unwantedPost == null) {
            if (user == null) {
                redirectAttributes.addFlashAttribute("profileErr", "Could not delete post. User no longer exists");
                return "redirect:/home";
            } 
            else {
                redirectAttributes.addFlashAttribute("deleteErr", "Could not delete post. Post doesn't exist");
                if (redirectTo.equals("/profile")) {
                    redirectUrl = "redirect:/profile/" + user.getId();
                }
                return redirectUrl;
            }
        }

        if (redirectTo.equals("/profile")) {
            redirectUrl = "redirect:/profile/" + user.getId();
        }

        try {
            postRepository.delete(unwantedPost);
        } catch (Exception e) {
            model.addAttribute("deleteErr", "Could not delete post: " + e.getMessage());
            return redirectUrl;
        }

        redirectAttributes.addFlashAttribute("successful", "Post was deleted.");
        return redirectUrl;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, HttpServletRequest request) {

        // Add the current user to the model to check if the current user owns a posts on the home page
        User currUser = authenticationService.getUserFromRequest(request);
        if (currUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("currUser", currUser);

        // Determine if the current user is an admin
        boolean canDeleteAll = currUser.getRole().equals(Role.ADMIN);
        model.addAttribute("currUserCanDeletePosts", canDeleteAll);

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", posts);

        return "homePage"; // this loads homePage.html
    }
    
}
