package com.example.fase1_grupob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class APIController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts/")
    public Collection<Post> getAllPosts(){
        return  this.postService.findAll();
    }
}
