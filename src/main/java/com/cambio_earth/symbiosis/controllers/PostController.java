package com.cambio_earth.symbiosis.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cambio_earth.symbiosis.models.Post;
import com.cambio_earth.symbiosis.models.PostRepository;
import com.cambio_earth.symbiosis.models.User;
import com.cambio_earth.symbiosis.models.UserRepository;



@Controller
public class PostController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

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
    
}
