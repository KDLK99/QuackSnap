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

    @Autowired
    private PostService postService;

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
    public ResponseEntity<Post> replacePost(@PathVariable long id, @RequestBody Post newPost) {

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

    /*@PostMapping(value = "/posts/{id}", consumes = {"application/json"})
    public ResponseEntity<Object> uploadPost(@PathVariable long id, @RequestParam MultipartFile image, @RequestBody String[] imageCategory,
    @RequestBody String imageDesc, @RequestBody String postTitle, @RequestBody int likes, @RequestBody Comment[] comments) throws IOException {

        //Post post = this.postService.findById(id);
        Post post;

        //if (post != null) {

            URI location = fromCurrentRequest().build().toUri();

            post.setImageName(location.toString()); // ?

            post.setId(id);
            post.setDescription(imageDesc);
            post.setTitle(postTitle);
            post.setCategoriesAPI(imageCategory);
            post.setLikes(likes);
            post.setComments(comments);
            this.postService.save(post);

            Files.createDirectories(IMAGES_FOLDER);
            Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());
            image.transferTo(imagePath);


            return ResponseEntity.created(location).build();

        //} else {
            //return ResponseEntity.notFound().build();
        //}
    }*/

}
