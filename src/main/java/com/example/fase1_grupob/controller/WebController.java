package com.example.fase1_grupob.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.service.ImageService;


import org.hibernate.engine.jdbc.BlobProxy;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fase1_grupob.service.PostService;
import com.example.fase1_grupob.service.UserService;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Controller
public class WebController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;

    public WebController(PostService postService, UserService userService, ImageService imageService){
        this.postService = postService;
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }


    @GetMapping("/")
    public String showPosts(Model model) {

        model.addAttribute("posts", this.postService.findAll());
        model.addAttribute("errormsg", "No posts yet.");

        return "index";
    }


    @PostMapping("/upload_image")
    public String uploadPost(@RequestParam MultipartFile image, Model model,
                               @RequestParam String imageCategory, @RequestParam String imageDesc, @RequestParam String postTitle) throws IOException {

        Files.createDirectories(IMAGES_FOLDER);
        Post post = new Post();
        if(postTitle.isEmpty() || imageDesc.isEmpty() || imageCategory.isEmpty() || image.isEmpty())
        {
            return "redirect:/uploadImage.html";
        }

        this.postService.save(post, 1L,image, imageCategory, imageDesc, postTitle);

        if(this.userService.findById(1).isEmpty()){
            this.userService.findById(1).get().addPost(post);
        }

        return "redirect:/";
    }

    @GetMapping("/viewPost/{index}")
    public String showPost(@PathVariable int index, Model model) throws MalformedURLException {
        if(this.postService.findById(index).isEmpty()){
            return "/templates/error/404.html";
        }

        model.addAttribute("description", this.postService.findById(index).get().getDescription());


        model.addAttribute("title", this.postService.findById(index).get().getTitle());

        model.addAttribute("index", index);

        Optional<Post> post = this.postService.findById(index);
        if(post.isPresent()){
            model.addAttribute("comments", post.get().getComments(this.userService));

            model.addAttribute("likes", post.get().getLikes());

            model.addAttribute("additionalInformationFile", post.get().getAdditionalInformationFile());
        }

        return "viewPost_template";
    }
    

    @GetMapping("/download_image/{index}")
    public ResponseEntity<Object> downloadImage(Model model, @PathVariable int index) throws MalformedURLException, SQLException {
        if(this.postService.findById(index).isPresent()) {
            Resource file = new InputStreamResource(this.postService.findById(index).get().getImage().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(this.postService.findById(index).get().getImage().length()).body(file);

        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/viewPost/{index}")
    public String comment(Model model, @PathVariable int index, @RequestParam String comment){
        if(comment.isEmpty()){
            return "redirect:/viewPost/{index}";
        }
        if(this.userService.findById(1).isPresent()){
            Comment comment1 = new Comment((long) 1, comment, this.userService.findById(1).get().getUsername());

            Optional<Post> post = this.postService.findById(index);
            if(post.isPresent()){
                post.get().addComment(comment1);
                this.postService.save(post.get(), post.get().getId());
                model.addAttribute("comments", post.get().getComments(this.userService));

                model.addAttribute("description", post.get().getDescription());
                model.addAttribute("title", post.get().getTitle());
                model.addAttribute("index", index);

                model.addAttribute("likes", post.get().getLikes());

            }

        }

        return "viewPost_template";
    }

    @PostMapping("/viewPost/{index}/increaseLikes")
    public String likes(@PathVariable int index){

        Optional<Post> post = this.postService.findById(index);
        if(post.isPresent() && this.userService.findById(1).isPresent()){
            post.get().addLike(this.userService.findById(1).get());
            this.userService.findById(1).get().addLikedPost(post);
            this.postService.save(post.get(), post.get().getId());
        }

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/deletePost/{index}")
    public String deletePost(Model model, @PathVariable int index) throws MalformedURLException {
        if(this.postService.findById(index).isPresent()) {
            Optional<Post> post = this.postService.findById(index);
            if(post.isPresent() && this.userService.findById(1).isPresent()) {
                this.postService.deleteById(post.get().getId());
                

                model.addAttribute("posts", this.postService.findAll());

                this.userService.findById(1).get().deletePost(post.get());
            }
        }

        return "redirect:/";
    }

    @PostMapping("/updatePost/{index}")
    public String updatePost(@PathVariable int index, @RequestParam String imageDesc, @RequestParam String postTitle) {
        Optional<Post> post = this.postService.findById(index);
        if(post.isPresent()) {
            if (!imageDesc.isEmpty()) {
                post.get().setDescription(imageDesc);
            }

            if (!postTitle.isEmpty()) {
                post.get().setTitle(postTitle);
            }

            this.postService.save(post.get(), post.get().getId());
        }

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/editPost/{index}")
    public String updatePost(Model model, @PathVariable int index){
        model.addAttribute("index", index);
        return "edit_post";
    }

    @PostMapping("/search")
    public String searchByCategory(@RequestParam String category, Model model, @RequestParam String order, @RequestParam(required = false) String title) {
        /*if(((category.isEmpty() && (order == null || order.isEmpty())) || category.isEmpty() && order.equals("Default")) && (title == null || title.isEmpty())){
            return "redirect:/";
        }*/

        model.addAttribute("posts", this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order, title));
        model.addAttribute("errormsg", "No posts match that search criteria.");
        return "index";
    }

    @GetMapping("/contactus")
    public String contactus()
    {
        return "contact";
    }

    @GetMapping("/user")
    public String user(Model model)
    {
        if(this.userService.findById(1).isPresent()) {
            model.addAttribute("index", 1);
            model.addAttribute("username", this.userService.findById(1).get().getUsername());
            model.addAttribute("description", this.userService.findById(1).get().getDescription());
            model.addAttribute("posts", this.userService.findById(1).get().getUserPosts());
        }

        return "user_template";
    }

    @GetMapping("/edit_profile")
    public String editprofile()
    {

        return "edit_profile";
    }
    @PostMapping("/upload_info")
    public String uploadInfo(@RequestParam String username, @RequestParam String description, @RequestParam MultipartFile image, Model model) throws IOException
    {
        UserP user = this.userService.findById(1).get();

        this.userService.save(user, username, description, image);

        return "redirect:/user";
    }

    @GetMapping("/updated_profile/{index}")
    public ResponseEntity<Object> updateImageUSer(Model model, @PathVariable int index) throws MalformedURLException, SQLException {
        if(this.userService.findById(1).isPresent()) {
            Resource file = new InputStreamResource(this.userService.findById(1).get().getImage().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(this.userService.findById(1).get().getImage().length()).body(file);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/userposts")

    public String showUserPosts(Model model) {

        model.addAttribute("posts",  this.postService.findAll());

        return "user_post";

    }

    @GetMapping("/deleteComment/{indexPost}/{indexComment}")
    public String deleteComment(@PathVariable int indexComment, @PathVariable int indexPost){
        this.postService.deleteComment(indexPost, indexComment);
        
        return "redirect:/viewPost/{indexPost}";
    }

    @PostMapping("/viewPost/{index}/uploadFile")
    public String uploadFile(Model model, @RequestParam MultipartFile file, @PathVariable int index){
        if (file == null || !file.isEmpty()){
            this.postService.uploadFile(index, file);
        }
        return "redirect:/viewPost/{index}";
    }

    @GetMapping("/viewPost/{index}/downloadFile")
    public ResponseEntity<Object> downloadFile(@PathVariable int index) throws MalformedURLException {
        return this.postService.downloadFile(index);
    }
}