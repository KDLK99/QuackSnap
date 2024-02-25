package com.example.fase1_grupob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ImageController {
    private static final Path IMAGES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");

    /*@PostMapping("/upload_image")
    public String uploadImage(@RequestParam String imageName, @RequestParam MultipartFile image, Model model)
            throws IOException {

        Files.createDirectories(IMAGES_FOLDER);

        Path imagePath = IMAGES_FOLDER.resolve(imageName);

        image.transferTo(imagePath);

        model.addAttribute("imageName", imageName);

        return "index1";
    }*/

    @PostMapping("/upload_image")
    public String uploadImages(@RequestParam String imageName, @RequestParam MultipartFile image, Model model)throws IOException{

        Files.createDirectories(IMAGES_FOLDER);

        Path imagePath = IMAGES_FOLDER.resolve(imageName);

        image.transferTo(imagePath);

        model.addAttribute("imageName", imageName);

        return "index1";
    }
}
