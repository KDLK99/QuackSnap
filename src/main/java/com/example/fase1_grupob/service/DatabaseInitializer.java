package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DatabaseInitializer {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() throws IOException{


        //Create Users
        UserP u1 = userService.findById(1).get();
        //Set profile photo
        Path imagePath = IMAGES_FOLDER.resolve("profphoto1.jpg");
        Resource image = new UrlResource(imagePath.toUri());
        u1.setImage(BlobProxy.generateProxy(image.getInputStream(), image.getFile().length()));

        
        //Create posts
        Post p1 = new Post();
        Post p2 = new Post();



        postService.save(p1, null, null, "testing", "This is the first post", "Hello!!");
        imagePath = IMAGES_FOLDER.resolve("image_88f7214b-fa78-4bae-8be5-ed43af8b9ad4_test1.jpg");
        image = new UrlResource(imagePath.toUri());
        p1.setImage(BlobProxy.generateProxy(image.getInputStream(), image.getFile().length()));
        postService.save(p2, null, null, "testing2", "This is a test post", "Example title");
        imagePath = IMAGES_FOLDER.resolve("image_0df5d1a8-360c-43a4-8670-8700cdd2f106_test2.jpg");
        image = new UrlResource(imagePath.toUri());
        p2.setImage(BlobProxy.generateProxy(image.getInputStream(), image.getFile().length()));
        

        //Create some comments
        p1.addComment(new Comment((long) 1, "This is the first message", "Pepe"));
        p2.addComment(new Comment((long) 1, "Hello!!", "Pepe"));
        p1.addLike(u1);

        //Add files
        p1.setAdditionalInformationFile("test.pdf");

        postService.save(p1, null);
        postService.save(p2, null);
    }
}
