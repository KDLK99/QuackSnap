package controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import model.Comment;
import model.Post;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.PostService;
import service.UserService;

@Controller
public class WebController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    private final PostService postService;
    private final UserService userService;

    public WebController(PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
    }


    @GetMapping("/")
    public String showPosts(Model model) {

        model.addAttribute("posts", this.postService.findAll());
        model.addAttribute("errormsg", "No posts yet.");

        return "index";
    }


    @PostMapping("/upload_image")
    public String uploadImages(Post post, @RequestParam MultipartFile image, Model model,
                               @RequestParam String imageCategory, @RequestParam String imageDesc, @RequestParam String postTitle) throws IOException {

        Files.createDirectories(IMAGES_FOLDER);

        if(postTitle.isEmpty() || imageDesc.isEmpty() || imageCategory.isEmpty() || image.isEmpty())
        {
            return "redirect:/uploadImage.html";
        }
        
        post.setTitle(postTitle);
        post.setDescription(imageDesc);
        post.setImageName("image" + this.postService.getNextId() + ".jpg");
        post.setCategories(imageCategory.toLowerCase());
        post.setId(this.postService.getNextId().get());

        Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());

        this.postService.save(post);

        image.transferTo(imagePath);

        model.addAttribute("imageName", post.getImageName());
        this.userService.findById(1).addPost(post);

        return "redirect:/";
    }

    @GetMapping("/viewPost/{index}")
    public String showPost(@PathVariable int index, Model model) throws MalformedURLException {
        if(this.postService.findById(index) == null){
            return "/templates/error/404.html";
        }
        
        model.addAttribute("description", this.postService.findById(index).getDescription());
        model.addAttribute("title", this.postService.findById(index).getTitle());
        model.addAttribute("index", index);

        Post post = this.postService.findById(index);
        model.addAttribute("comments", post.getComments(this.userService));

        model.addAttribute("likes", post.getLikes());

        return "viewPost_template";
    }
    

    @GetMapping("/download_image/{index}")
    public ResponseEntity<Object> downloadImage(Model model, @PathVariable int index) throws MalformedURLException {

        Path imagePath = IMAGES_FOLDER.resolve(this.postService.findById(index).getImageName());

        Resource image = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @PostMapping("/viewPost/{index}")
    public String comment(Model model, @PathVariable int index, @RequestParam String comment){
        Comment comment1 = new Comment((long) 1, comment, this.userService.findById(1).getUsername());
        Post post = this.postService.findById(index);
        post.addComment(comment1);
        model.addAttribute("comments", post.getComments(this.userService));

        model.addAttribute("description", post.getDescription());
        model.addAttribute("title", post.getTitle());
        model.addAttribute("index", index);

        model.addAttribute("likes", post.getLikes());

        return "viewPost_template";
    }

    @PostMapping("/viewPost/{index}/increaseLikes")
    public String likes(@PathVariable int index){
        Post post = this.postService.findById(index);
        post.addLike(this.userService.findById(1));

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/deletePost/{index}")
    public String deletePost(Model model, @PathVariable int index) throws MalformedURLException {
        Path imgPath = IMAGES_FOLDER.resolve(this.postService.findById(index).getImageName());
        File img = imgPath.toFile();
        img.delete();
        Post post = this.postService.findById(index);
        this.postService.deleteById(post.getId());
        this.postService.deleteById(index);

        model.addAttribute("posts",  this.postService.findAll());

        this.userService.findById(1).deletePost(post);

        return "redirect:/";
    }

    @PostMapping("/updatePost/{index}")
    public String updatePost(Model model, @PathVariable int index, @RequestParam String imageDesc, @RequestParam String postTitle)
            throws IOException {
        Post post = this.postService.findById(index);
        if(!imageDesc.isEmpty()){
            post.setDescription(imageDesc);
        }
        if(!postTitle.isEmpty()){
            post.setTitle(postTitle);
        }

        return "redirect:/viewPost/{index}";
    }

    @GetMapping("/editPost/{index}")
    public String updatePost(Model model, @PathVariable int index){
        model.addAttribute("index", index);
        return "edit_post";
    }

    @PostMapping("/search")
    public String searchByCategory(@RequestParam String category, Model model) {
        if(category.isEmpty()){
            return "redirect:/";
        }
        model.addAttribute("posts", this.postService.filteredPosts(category.toLowerCase()));
        model.addAttribute("errormsg", "Ningún post coincide con ese criterio de búsqueda.");
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
        model.addAttribute("index", 1);
        model.addAttribute("username", this.userService.findById(1).getUsername());
        model.addAttribute("description", this.userService.findById(1).getDescription());
        model.addAttribute("posts", this.userService.findById(1).getUserPosts());

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
        if(!image.isEmpty())
        {
            Files.createDirectories(IMAGES_FOLDER);

            this.userService.findById(1).setProfilePhotoName("profphoto" + 1 + ".jpg");

            Path imagePath = IMAGES_FOLDER.resolve(this.userService.findById(1).getProfilePhotoName());
            image.transferTo(imagePath);
            model.addAttribute("profile", this.userService.findById(1).getProfilePhotoName());
        }

        this.userService.findById(1).updateUsername(username);
        this.userService.findById(1).updateDescription(description);

        return "redirect:/user";
    }

    @GetMapping("/updated_profile/{index}")
    public ResponseEntity<Object> updateImageUSer(Model model, @PathVariable int index) throws MalformedURLException {

        Path imagePath = IMAGES_FOLDER.resolve(this.userService.findById(1).getProfilePhotoName());

        Resource image = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @GetMapping("/userposts")

    public String showUserPosts(Model model) {

        model.addAttribute("posts",  this.postService.findAll());

        return "user_post";

    }
}