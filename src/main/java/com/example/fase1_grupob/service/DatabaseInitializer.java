package com.example.fase1_grupob.service;

import java.io.IOException;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() throws IOException{
        //Create Users
        UserP u1 = userService.findById(1).get();
        
        //Create posts
        Post p1 = new Post();
        Post p2 = new Post();

        postService.save(p1, null, null, "testing", "This is the first post", "Hello!!");
        p1.setImageName("image_88f7214b-fa78-4bae-8be5-ed43af8b9ad4_test1.jpg");
        postService.save(p2, null, null, "testing2", "This is a test post", "Example title");
        p2.setImageName("image_0df5d1a8-360c-43a4-8670-8700cdd2f106_test2.jpg");
        

        //Create some comments
        p1.addComment(new Comment((long) 1, "This is the first message", "Pepe"));
        p2.addComment(new Comment((long) 1, "Hello!!", "Pepe"));
        p1.addLike(u1);

        //Add files
        p1.setAdditionalInformationFile("test.pdf");

        postService.save(p1, null);
        postService.save(p2, null); 
    }
}
