package com.cambio_earth.symbiosis.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cambio_earth.symbiosis.models.PostRepository;
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
    public String addPost(@RequestParam Map<String, String> inputs, Model model) {
        
        return "";
    }
    
}
