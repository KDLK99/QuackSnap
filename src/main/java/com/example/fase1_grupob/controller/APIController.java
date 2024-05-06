package com.example.fase1_grupob.controller;


import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.service.ImageService;
import com.sun.jdi.request.ExceptionRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fase1_grupob.service.PostService;
import com.example.fase1_grupob.service.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class APIController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");


    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public ResponseEntity<Post> createPost(String description, MultipartFile image, String title, String categories, HttpServletRequest request)throws IOException {
        if(image== null || image.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Post post = new Post();

        this.postService.save(post, this.userService.findByName(request.getUserPrincipal().getName()).get().getId(), image, categories, description, title);


        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();


        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> replacePost(@PathVariable long id, String description, String title, HttpServletRequest request) {

        Optional<Post> post1 = this.postService.findById(id);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post1.get())){
            return ResponseEntity.status(403).build();
        }

        if (this.postService.findById(id).isPresent()) {
            Optional<Post> post = this.postService.findById(id);
            if(post.isPresent()) {
                if (!description.isEmpty()) {
                    post.get().setDescription(description);
                }

                if (!title.isEmpty()) {
                    post.get().setTitle(title);
                }

                this.postService.save(post.get());
            }

            return ResponseEntity.ok(post.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable long id, HttpServletRequest request) throws IOException {
        Optional<Post> post1 = this.postService.findById(id);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post1.get())){
            return ResponseEntity.status(403).build();
        }
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();

            this.postService.deleteById(id);


            return ResponseEntity.ok(post);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/posts/{id}/like")
    public ResponseEntity<Post> giveLike( @PathVariable long id, HttpServletRequest request)throws IOException {
        if(this.postService.findById(id).isPresent()) {
            Post post = this.postService.findById(id).get();
            this.postService.addLike(this.userService.findByName(request.getUserPrincipal().getName()).get(), post);
            this.userService.findByName(request.getUserPrincipal().getName()).get().addLikedPost(post);
            this.postService.saveLikedPost(post, post.getId());
            return ResponseEntity.ok(post);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping( "/posts/{id}/comment")
    public ResponseEntity<Post> writeComment( @PathVariable long id, Comment comment, HttpServletRequest request)throws IOException {
        if(this.postService.findById(id).isPresent()) {

            Comment comment1 = new Comment(this.userService.findByName(request.getUserPrincipal().getName()).get().getId(), comment.getText(), this.userService.findByName(request.getUserPrincipal().getName()).get().getUsername());

            Optional<Post> post = this.postService.findById(id);

            post.get().addComment(comment1);
            this.postService.save(post.get());
            return ResponseEntity.ok(post.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping( "/user")
    public ResponseEntity<UserP> updateUserData (String description, MultipartFile image, HttpServletRequest request)throws IOException {
        if (this.userService.findByName(request.getUserPrincipal().getName()).isPresent()) {
            UserP user = this.userService.findByName(request.getUserPrincipal().getName()).get();
            try {
                this.userService.save(user, description, image);
            }catch (ResponseStatusException e){
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(this.userService.findByName(request.getUserPrincipal().getName()).get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/searchBar")
    public ResponseEntity<Collection<Post>> searchAPI (String category, String order, String title){

        if(this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order, title).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order, title));
    }

    @DeleteMapping("/posts/{index}/comment/{position}")
    public ResponseEntity<Comment> deleteComment(@PathVariable int index, @PathVariable int position, HttpServletRequest request){
        Optional<Post> post = this.postService.findById(index);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            return ResponseEntity.status(403).build();
        }

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
    public ResponseEntity<MultipartFile> uploadFile(@PathVariable int index, MultipartFile file, HttpServletRequest request){

        Optional<Post> post = this.postService.findById(index);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            return ResponseEntity.status(403).build();
        }

        if(this.postService.findById(index).isPresent()) {
            if(file == null || file.isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            try {
                this.postService.uploadFile(index, file);
            }catch (ResponseStatusException e){
                if(e.getStatusCode().value() == 422) {
                    return ResponseEntity.unprocessableEntity().build();
                }else if(e.getStatusCode().value() == 400){
                    return ResponseEntity.badRequest().build();
                }
            }
            return ResponseEntity.ok().build();
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

    @DeleteMapping("/user/{idUser}")
    public ResponseEntity<Object> deleteUser(@PathVariable int idUser, HttpServletRequest request){

        if(!request.isUserInRole("ADMIN") && this.userService.findByName(request.getUserPrincipal().getName()).get().getId() != idUser){
            return ResponseEntity.status(403).build();
        }
        if(!this.userService.findById(idUser).isEmpty()) {
            List<Post> lista = this.userService.findById(idUser).get().getUserPosts();
            List<Post> lista1 = new ArrayList<>(lista);
            for (Post post : lista1) {
                this.postService.deleteById(post.getId());

            }
            this.userService.deleteById(idUser);

            return ResponseEntity.status(202).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password, @RequestParam String description, @RequestParam MultipartFile image) throws IOException
    {
        UserP userP = new UserP(username, passwordEncoder.encode(password), description, "USER");

        if (!image.isEmpty() && !username.isEmpty() && !description.isEmpty() && !password.isEmpty()) {
            userP = this.imageService.createImage(image, userP);
            this.userService.save(userP);
            return ResponseEntity.status(201).build();
        }else{
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
