package com.example.fase1_grupob.controller;


import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.service.ImageService;
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
import java.util.List;
import java.util.Objects;


import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class APIController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");


    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;

    public APIController(PostService postService, UserService userService, ImageService imageService){
        this.postService = postService;
        this.userService = userService;
        this.imageService = imageService;
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
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();
            return ResponseEntity.ok(post);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(value = "/posts")
    public ResponseEntity<Post> createPost( Post post,  MultipartFile image)throws IOException {

        this.postService.save(post, 1L, image);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();

        Files.createDirectories(IMAGES_FOLDER);
        Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());
        image.transferTo(imagePath);

        return ResponseEntity.created(location).body(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> replacePost(@PathVariable long id, Post newPost) {
        if (this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();

            newPost.setImageName(post.getImageName());
            newPost.setCategories(post.getCategories());
            newPost.setId(id);
            newPost.setDescription(post.getDescription());
            this.postService.save(newPost, 1L);

            return ResponseEntity.ok(post);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable long id) throws IOException {
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();

            this.postService.deleteById(id);

            if (post.getImageName() != null) {
                Path imgPath = IMAGES_FOLDER.resolve(post.getImageName());
                File img = imgPath.toFile();
                img.delete();
            }

            return ResponseEntity.ok(post);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/posts/{id}/like")
    public ResponseEntity<Post> giveLike( @PathVariable long id)throws IOException {
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();
            post.setLikes(post.getLikes() + 1);
            return ResponseEntity.ok(post);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping( "/posts/{id}/comment")
    public ResponseEntity<Post> writeComment( @PathVariable long id, Comment comment)throws IOException {
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();

            if(this.userService.findById(1).isPresent()) {
                comment.setUsername(this.userService.findById(1).get().getUsername());
                comment.setUserId((long) 1);
                post.addComment(comment);
            }
            return ResponseEntity.ok(post);

        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping( "/user")
    public ResponseEntity<UserP> updateUserData (String username, String description, MultipartFile image)throws IOException {
        if (this.userService.findById(1).isPresent()) {
            if (image != null && !image.isEmpty()) {
                Files.createDirectories(IMAGES_FOLDER);

                this.userService.findById(1).get().setProfilePhotoName(Objects.requireNonNull(image.getOriginalFilename()));


                Path imagePath = IMAGES_FOLDER.resolve(this.userService.findById(1).get().getProfilePhotoName());
                image.transferTo(imagePath);
            }
            this.userService.findById(1).get().updateUsername(username);
            this.userService.findById(1).get().updateDescription(description);
            return ResponseEntity.ok(this.userService.findById(1).get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/searchBar")
    public ResponseEntity<Collection<Post>> searchAPI (List<String> category)throws IOException {

        if(this.postService.filteredPosts(category).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else if(category.isEmpty()){
            return ResponseEntity.ok(this.postService.findAll());
        }

        return ResponseEntity.ok(this.postService.filteredPosts(category));
    }
}
