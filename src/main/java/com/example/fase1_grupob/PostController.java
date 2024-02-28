package com.example.fase1_grupob;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");
    private List<Post> posts = new ArrayList<>();
    private int nImages = 0;

    /*
     * @PostMapping("/upload_image")
     * public String uploadImage(@RequestParam String imageName, @RequestParam
     * MultipartFile image, Model model)
     * throws IOException {
     * 
     * Files.createDirectories(IMAGES_FOLDER);
     * 
     * Path imagePath = IMAGES_FOLDER.resolve(imageName);
     * 
     * image.transferTo(imagePath);
     * 
     * model.addAttribute("imageName", imageName);
     * 
     * return "index1";
     * }
     */

    @GetMapping("/")
    public String showPosts(Model model) {

        model.addAttribute("posts", posts);

        return "index";
    }

    @PostMapping("/upload_image")
    public String uploadImages(Post post, @RequestParam MultipartFile image, Model model,
            @RequestParam String imageCategory, @RequestParam String imageDesc, @RequestParam String postTitle) throws IOException {

        model.addAttribute("posts", posts);

        Files.createDirectories(IMAGES_FOLDER);

        this.nImages++;

        post.setTitle(postTitle);
        post.setDescription(imageDesc);
        post.setImageName("image" + this.nImages + ".jpg");
        post.setCategories(imageCategory);

        Path imagePath = IMAGES_FOLDER.resolve(post.getImageName());

        posts.add(post);

        image.transferTo(imagePath);

        model.addAttribute("imageName", post.getImageName());

        return "index";
    }

    @GetMapping("/viewPost/{index}")
    public String showPost(@PathVariable int index, Model model) throws MalformedURLException {
        model.addAttribute("description", posts.get(index - 1).getDescription());
        model.addAttribute("title", posts.get(index - 1).getTitle());
        model.addAttribute("index", index);

        Post post = posts.get(index - 1);
        model.addAttribute("comments", post.getComments());

        return "viewPost_template";
    }
    

    @GetMapping("/download_image/{index}")
    public ResponseEntity<Object> downloadImage(Model model, @PathVariable int index) throws MalformedURLException {

        Path imagePath = IMAGES_FOLDER.resolve(posts.get(index - 1).getImageName());

        Resource image = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @PostMapping("/viewPost/{index}")
    public String comment(Model model, @PathVariable int index, @RequestParam String userName, @RequestParam String comment){
        Comment comment1 = new Comment(userName, comment);
        Post post = posts.get(index - 1);
        post.addComment(comment1);
        model.addAttribute("comments", post.getComments());

        model.addAttribute("description", posts.get(index - 1).getDescription());
        model.addAttribute("title", posts.get(index - 1).getTitle());
        model.addAttribute("index", index);

        return "viewPost_template";
    }
}