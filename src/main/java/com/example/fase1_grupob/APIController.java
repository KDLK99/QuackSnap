package com.example.fase1_grupob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
public class APIController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");


    private final PostService postService;
    private final UserService userService;

    public APIController(PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public Collection<Post> getAllPosts(){
        return  this.postService.findAll();
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


    @PostMapping(value = "/post")
    public ResponseEntity<Post> createPost( Post post,  MultipartFile image)throws IOException {

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

    @PostMapping(value = "/post/{id}")
    public ResponseEntity<Post> giveLike( @PathVariable long id)throws IOException {
        Post post = this.postService.findById(id);

        if (post != null) {
            post.setLikes(post.getLikes() + 1);
            return ResponseEntity.ok(post);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping(value = "/commentpost/{id}")
    public ResponseEntity<Post> writeComment( @PathVariable long id, String comment)throws IOException {
        Post post = this.postService.findById(id);
        
        if (post != null) {
            Comment comment1 = new Comment((long) 1, comment, this.userService.findById(1).getUsername());
            post.addComment(comment1);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
