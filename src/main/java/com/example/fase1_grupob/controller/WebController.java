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


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.fase1_grupob.service.PostService;
import com.example.fase1_grupob.service.UserService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
public class WebController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return "login";
    }

    @GetMapping("/admin")
    public String admin(HttpServletRequest request, Model model) 
    {
        model.addAttribute("usersRegistered", this.userService.getAllUsersExceptAdmin());
        
        return "godmode";
    }

    @GetMapping("/deleteuser/{idUser}")
    public String deleteuser(@PathVariable int idUser, HttpServletRequest request) throws ServletException {

        if(!request.isUserInRole("ADMIN") && this.userService.findByName(request.getUserPrincipal().getName()).get().getId() != idUser){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<Post> lista = this.userService.findById(idUser).get().getUserPosts();
        List<Post> lista1 = new ArrayList<>(lista);
        for(Post post : lista1){
            this.postService.deleteById(post.getId());

        }
        this.userService.deleteById(idUser);
        if(request.isUserInRole("ADMIN")){
            return "redirect:/admin";
        }else{
            request.logout();
            return "redirect:/";
        }
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String description, @RequestParam MultipartFile image) throws IOException 
    {
        UserP userP = new UserP(username, passwordEncoder.encode(password), description, "USER");
        if (!image.isEmpty()) {
            userP = this.imageService.createImage(image, userP);
        }
        this.userService.save(userP);
        return "login";
    }
    


    @GetMapping("/")
    public String showPosts(Model model, HttpServletRequest request) {

        model.addAttribute("posts", this.postService.findAll());
        model.addAttribute("user", request.isUserInRole("USER"));
        model.addAttribute("admin", request.isUserInRole("ADMIN"));
        model.addAttribute("errormsg", "No posts yet.");

        return "index";
    }
    @GetMapping("/uploadImage")
    public String uploadPost() {

        return "uploadImage";
    }


    @PostMapping("/upload_image")
    public String uploadPost(@RequestParam MultipartFile image, Model model,
                               @RequestParam String imageCategory, @RequestParam String imageDesc, @RequestParam String postTitle, HttpServletRequest request) throws IOException {

        Files.createDirectories(IMAGES_FOLDER);
        Post post = new Post();
        if(postTitle.isEmpty() || imageDesc.isEmpty() || imageCategory.isEmpty() || image.isEmpty())
        {
            return "redirect:/uploadImage.html";
        }

        this.postService.save(post, this.userService.findByName(request.getUserPrincipal().getName()).get().getId(),image, imageCategory, imageDesc, postTitle);

        return "redirect:/";
    }

    @GetMapping("/viewPost/{index}")
    public String showPost(@PathVariable int index, Model model, HttpServletRequest request) throws MalformedURLException {
        if(this.postService.findById(index).isEmpty()){
            return "/templates/error/404.html";
        }
        Optional<Post> post1 = this.postService.findById(index);

        model.addAttribute("userPermission", (!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post1.get())) ? null : true);

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

    @PostMapping("/addComment/{index}")
    public String comment(Model model, @PathVariable int index, @RequestParam String comment, HttpServletRequest request){
        if(this.userService.findByName(request.getUserPrincipal().getName()).isPresent() && !comment.isEmpty()){
            Comment comment1 = new Comment((long) this.userService.findByName(request.getUserPrincipal().getName()).get().getId(), comment, this.userService.findByName(request.getUserPrincipal().getName()).get().getUsername());

            Optional<Post> post = this.postService.findById(index);
            if(post.isPresent()){
                post.get().addComment(comment1);
                this.postService.save(post.get());
            }
        }
        return "redirect:/viewPost/{index}";
    }

    @PostMapping("/{index}/increaseLikes")
    public String likes(@PathVariable int index, HttpServletRequest request){

        Optional<Post> post = this.postService.findById(index);
        if(post.isPresent() && this.userService.findByName(request.getUserPrincipal().getName()).isPresent()){
            this.postService.addLike(this.userService.findByName(request.getUserPrincipal().getName()).get(), post.get());
            this.userService.findByName(request.getUserPrincipal().getName()).get().addLikedPost(post.get());
            this.postService.saveLikedPost(post.get(), post.get().getId());
        }

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/deletePost/{index}")
    public String deletePost(Model model, @PathVariable int index, HttpServletRequest request) throws MalformedURLException {

        Optional<Post> post1 = this.postService.findById(index);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post1.get())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if(this.postService.findById(index).isPresent()) {
            Optional<Post> post = this.postService.findById(index);
            if(post.isPresent() && this.userService.findByName(request.getUserPrincipal().getName()).isPresent()) {
                this.postService.deleteById(post.get().getId());
                
                Collection<Post> lista = this.postService.findAll();
                model.addAttribute("posts", this.postService.findAll());

                this.userService.findByName(request.getUserPrincipal().getName()).get().deletePost(post.get());
            }
        }

        return "redirect:/";
    }


    @PostMapping("/updatePost/{index}")
    public String updatePost(@PathVariable int index, @RequestParam String imageDesc, @RequestParam String postTitle, HttpServletRequest request) {
        Optional<Post> post = this.postService.findById(index);



        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if(post.isPresent()) {
            if (!imageDesc.isEmpty()) {
                post.get().setDescription(imageDesc);
            }

            if (!postTitle.isEmpty()) {
                post.get().setTitle(postTitle);
            }

            this.postService.save(post.get());
        }

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/editPost/{index}")
    public String updatePost(Model model, @PathVariable int index, HttpServletRequest request){
        Optional<Post> post = this.postService.findById(index);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("index", index);
        return "edit_post";
    }

    @PostMapping("/search")
    public String search(@RequestParam String category, Model model, @RequestParam String order, @RequestParam(required = false) String title, HttpServletRequest request) {
        /*if(((category.isEmpty() && (order == null || order.isEmpty())) || category.isEmpty() && order.equals("Default")) && (title == null || title.isEmpty())){
            return "redirect:/";
        }*/

        model.addAttribute("posts", this.postService.filteredPosts(Arrays.stream(category.split(" ")).toList(), order, title));
        model.addAttribute("errormsg", "No posts match that search criteria.");

        model.addAttribute("user", request.isUserInRole("USER"));


        return "index";
    }

    @GetMapping("/contactus")
    public String contactus()
    {
        return "contact";
    }

    @GetMapping("/user")
    public String user(Model model, HttpServletRequest request)
    {
        
        if(this.userService.findByName(request.getUserPrincipal().getName()).isPresent()) {
            model.addAttribute("index", this.userService.findByName(request.getUserPrincipal().getName()).get().getId());
            model.addAttribute("username", this.userService.findByName(request.getUserPrincipal().getName()).get().getUsername());
            model.addAttribute("description", this.userService.findByName(request.getUserPrincipal().getName()).get().getDescription());
            model.addAttribute("posts", this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts());
        }

        return "user_template";
    }

    @GetMapping("/edit_profile")
    public String editprofile()
    {

        return "edit_profile";
    }
    @PostMapping("/upload_info")
    public String uploadInfo(@RequestParam String description, @RequestParam MultipartFile image, Model model, HttpServletRequest request) throws IOException
    {
        UserP user = this.userService.findByName(request.getUserPrincipal().getName()).get();

        this.userService.save(user, description, image);

        return "redirect:/user";
    }

    @GetMapping("/updated_profile")
    public ResponseEntity<Object> updateImageUSer(Model model, HttpServletRequest request) throws  SQLException {
        if(this.userService.findByName(request.getUserPrincipal().getName()).isPresent()) {
            Resource file = new InputStreamResource(this.userService.findByName(request.getUserPrincipal().getName()).get().getImage().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(this.userService.findByName(request.getUserPrincipal().getName()).get().getImage().length()).body(file);
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/userposts")

    public String showUserPosts(Model model, HttpServletRequest request) {

        Collection <Post> posts = this.postService.findByUsername(request.getUserPrincipal().getName());
        model.addAttribute("posts",  posts);

        return "user_post";

    }

    @PostMapping ("/deleteComment/{indexPost}/{indexComment}")
    public String deleteComment(@PathVariable int indexComment, @PathVariable int indexPost, HttpServletRequest request){
        Optional<Post> post = this.postService.findById(indexPost);

        if(!request.isUserInRole("ADMIN") && !request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        this.postService.deleteComment(indexPost, indexComment);
        
        return "redirect:/viewPost/{indexPost}";
    }

    @PostMapping("/{index}/uploadFile")
    public String uploadFile(Model model, @RequestParam MultipartFile file, @PathVariable int index, HttpServletRequest request){
        Optional<Post> post = this.postService.findById(index);

        if(!request.isUserInRole("ADMIN") && !this.userService.findByName(request.getUserPrincipal().getName()).get().getUserPosts().contains(post.get())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (file == null || !file.isEmpty()){
            this.postService.uploadFile(index, file);
        }
        return "redirect:/viewPost/{index}";
    }

    @GetMapping("/{index}/downloadFile")
    public ResponseEntity<Object> downloadFile(@PathVariable int index) throws MalformedURLException {
        return this.postService.downloadFile(index);
    }


}