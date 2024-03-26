package com.example.fase1_grupob.controller;


import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fase1_grupob.service.PostService;
import com.example.fase1_grupob.service.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;


import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class APIController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");


    private final PostService postService;
    private final UserService userService;

    public APIController(PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Collection<Post>> getAllPosts(){
        if(this.postService.findAll().isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(this.postService.findAll());
        }
    }


    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPost(@PathVariable long id) {

        Post post = this.postService.findById(id);

        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(value = "/posts")
    public ResponseEntity<Post> createPost( Post post,  MultipartFile image)throws IOException {

        post.setImageName("image" + this.postService.getNextId() + ".jpg");
        this.postService.save(post);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();

        Files.createDirectories(IMAGES_FOLDER);
        Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());
        image.transferTo(imagePath);

        return ResponseEntity.created(location).body(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> replacePost(@PathVariable long id, Post newPost) {

        Post post = this.postService.findById(id);

        if (post != null) {
            newPost.setImageName("image" + post.getId() + ".jpg");
            newPost.setCategories(post.getCategories().get(0));
            newPost.setId(id);
            this.postService.save(newPost);

            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable long id) throws IOException {

        Post post = this.postService.findById(id);

        if (post != null) {
            this.postService.deleteById(id);

            if(post.getImageName() != null) {
                Path imgPath = IMAGES_FOLDER.resolve(post.getImageName());
                File img = imgPath.toFile();
                img.delete();
            }

            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/posts/{id}/like")
    public ResponseEntity<Post> giveLike( @PathVariable long id)throws IOException {
        Post post = this.postService.findById(id);

        if (post != null) {
            post.setLikes(post.getLikes() + 1);
            return ResponseEntity.ok(post);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping( "/posts/{id}/comment")
    public ResponseEntity<Post> writeComment( @PathVariable long id, Comment comment)throws IOException {
        Post post = this.postService.findById(id);
        
        if (post != null) {
            comment.setUsername(this.userService.findById(1).getUsername());
            comment.setUserId((long) 1);
            post.addComment(comment);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping( "/user")
    public ResponseEntity<User> updateUserData (String username, String description, MultipartFile image)throws IOException {

        if(image != null && !image.isEmpty()) {
            Files.createDirectories(IMAGES_FOLDER);

            this.userService.findById(1).setProfilePhotoName("profphoto" + 1 + ".jpg");

            Path imagePath = IMAGES_FOLDER.resolve(this.userService.findById(1).getProfilePhotoName());
            image.transferTo(imagePath);
        }
            this.userService.findById(1).updateUsername(username);
            this.userService.findById(1).updateDescription(description);
            return ResponseEntity.ok(this.userService.findById(1));
}

    @PostMapping( "/searchBar")
    public ResponseEntity<Collection<Post>> searchAPI (String category)throws IOException {

        if(this.postService.filteredPosts(category).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else if(category.isEmpty()){
            return ResponseEntity.ok(this.postService.findAll());
        }

        return ResponseEntity.ok(this.postService.filteredPosts(category));
    }
}
