package com.example.fase1_grupob.controller;


import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.service.ImageService;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fase1_grupob.service.PostService;
import com.example.fase1_grupob.service.UserService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
    public ResponseEntity<Post> createPost(String description,  MultipartFile image, String title, String categories)throws IOException {
        Post post = new Post();
        this.postService.save(post, 1L, image, categories, description, title);


        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();


        return ResponseEntity.created(location).body(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> replacePost(@PathVariable long id, String description, String title) {
        if (this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();
            Post newPost = new Post();

            newPost.setImage(post.getImage());
            newPost.setCategories(post.getCategories());
            newPost.setId(id);
            newPost.setDescription(description);
            newPost.setTitle(title);
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
            this.postService.save(post, id);
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
                this.postService.save(post, id);
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

                this.userService.findById(1).get().setImage(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));


                /*Path imagePath = IMAGES_FOLDER.resolve(this.userService.findById(1).get().getImage());
                image.transferTo(imagePath);*/
            }
            this.userService.findById(1).get().updateUsername(username);
            this.userService.findById(1).get().updateDescription(description);
            return ResponseEntity.ok(this.userService.findById(1).get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/searchBar")
    public ResponseEntity<Collection<Post>> searchAPI (String category, String order){

        if(this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else if(category.isEmpty()){
            return ResponseEntity.ok(this.postService.findAll());
        }

        return ResponseEntity.ok(this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order));
    }

    @DeleteMapping("/posts/{index}/comment/{position}")
    public ResponseEntity<Comment> deleteComment(@PathVariable int index, @PathVariable int position){

        if(this.postService.findById(index).isEmpty() || this.postService.findById(index).get().getCounter() == 0){
            return ResponseEntity.notFound().build();
        }else if(position < 0 || position > this.postService.findById(index).get().getCounter()){
            return ResponseEntity.notFound().build();
        }else {
            Comment comment = this.postService.findById(index).get().getComments().get(position);
            this.postService.deleteComment(index, position);
            return ResponseEntity.ok(comment);
        }

    }

    @PostMapping("/posts/{index}/file")
    public ResponseEntity<MultipartFile> uploadFile(@PathVariable int index, MultipartFile file){
        if(this.postService.findById(index).isPresent()) {
            this.postService.uploadFile(index, file);
            return ResponseEntity.ok(file);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/posts/{index}/file")
    public ResponseEntity<Object> downloadFile(@PathVariable int index) throws MalformedURLException {

        if(this.postService.findById(index).isPresent() && this.postService.findById(index).get().getAdditionalInformationFile() != null) {
            return this.postService.downloadFile(index);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
