package com.example.fase1_grupob;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WebController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    /*private List<Post> posts = new ArrayList<>();*/
    private int nImages = 0;

    private User user = new User();
    private List<User> userlist = new ArrayList<>();
    private int nProfilePhoto = 0;

    @Autowired
    private PostService postService;

    public WebController(){
        this.userlist.add(user);
    }


    @GetMapping("/")
    public String showPosts(Model model) {

        model.addAttribute("posts", this.postService.findAll());

        return "index";
    }


    @PostMapping("/upload_image")
    public String uploadImages(Post post, @RequestParam MultipartFile image, Model model,
            @RequestParam String imageCategory, @RequestParam String imageDesc, @RequestParam String postTitle) throws IOException {

        model.addAttribute("posts", this.postService.findAll());

        Files.createDirectories(IMAGES_FOLDER);

        this.nImages++;

        post.setTitle(postTitle);
        post.setDescription(imageDesc);
        post.setImageName("image" + this.nImages + ".jpg");
        post.setCategories(imageCategory);
        post.setId((long) this.nImages);

        Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());

        this.postService.save(post);

        image.transferTo(imagePath);

        model.addAttribute("imageName", post.getImageName());

        return "index";
    }

    @GetMapping("/viewPost/{index}")
    public String showPost(@PathVariable int index, Model model) throws MalformedURLException {
        model.addAttribute("description", this.postService.findById(index).getDescription());
        model.addAttribute("title", this.postService.findById(index).getTitle());
        model.addAttribute("index", index);

        Post post = this.postService.findById(index);
        model.addAttribute("comments", post.getComments());

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
    public String comment(Model model, @PathVariable int index, @RequestParam String userName, @RequestParam String comment){
        Comment comment1 = new Comment(userName, comment);
        Post post = this.postService.findById(index);
        post.addComment(comment1);
        model.addAttribute("comments", post.getComments());

        model.addAttribute("description", post.getDescription());
        model.addAttribute("title", post.getTitle());
        model.addAttribute("index", index);

        model.addAttribute("likes", post.getLikes());

        return "viewPost_template";
    }

    @PostMapping("/viewPost/{index}/increaseLikes")
    public String likes(@PathVariable int index){
        Post post = this.postService.findById(index);
        post.addLike();

        return "redirect:/viewPost/{index}";
    }


    @GetMapping("/deletePost/{index}")
    public String deletePost(Model model, @PathVariable int index) throws MalformedURLException {
        Path imgPath = IMAGES_FOLDER.resolve(this.postService.findById(index).getImageName());
        File img = imgPath.toFile();
        img.delete();
        this.postService.deleteById(this.postService.findById(index).getId());
        this.postService.deleteById(index);

        model.addAttribute("posts",  this.postService.findAll());

        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchByCategory(@RequestParam String category, Model model) {
        List<Post> postAux = new ArrayList<>();
        for (Post post :  this.postService.findAll()) {
            if (post.checkCategory(category)) {
                postAux.add(post);
            }
        }
        model.addAttribute("posts", postAux);
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
        model.addAttribute("username", this.user.getUsername());
        model.addAttribute("description", this.user.getDescription());

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

            this.user.setProfilePhotoName("profphoto" + 1 + ".jpg");

            Path imagePath = IMAGES_FOLDER.resolve(user.getProfilePhotoName());
            userlist.add(user);
            image.transferTo(imagePath);
            model.addAttribute("profile", user.getProfilePhotoName());
        }

        this.user.updateUsername(username);
        this.user.updateDescription(description);

        model.addAttribute("username", this.user.getUsername());
        model.addAttribute("description", this.user.getDescription());
        model.addAttribute("index", 1);
        return "user_template";
    }

    @GetMapping("/updated_profile/{index}")
    public ResponseEntity<Object> downloadImageUSer(Model model, @PathVariable int index) throws MalformedURLException {

        Path imagePath = IMAGES_FOLDER.resolve(userlist.get(index - 1).getProfilePhotoName());

        Resource image = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @GetMapping("/user/posts")
    public String userPosts(Model model, User user){
        model.addAttribute("posts", user.getUserPosts());
        return "userPosts_Template";
    }
}