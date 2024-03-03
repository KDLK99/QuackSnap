package com.example.fase1_grupob;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;




@Controller
public class UserController 
{
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    private User user = new User();
    private List<User> userlist = new ArrayList<>();
    private int nProfilePhoto = 0;

    public UserController()
    {
        userlist.add(user);
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
    public ResponseEntity<Object> downloadImage(Model model, @PathVariable int index) throws MalformedURLException {

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