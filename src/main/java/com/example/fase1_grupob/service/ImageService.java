package com.example.fase1_grupob.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.UUID;

import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.model.UserP;
import com.mysql.cj.util.SaslPrep;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageService {

    private boolean isValid(MultipartFile file){
        String originalName = file.getOriginalFilename();

        String type = file.getContentType();
        if(!file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png") && !file.getContentType().equals("image/gif") && !originalName.matches(".*\\.(jpg|jpeg|gif|png)")){
            return false;
        }
        return true;
    }

    public Post createImage(MultipartFile multiPartFile, Post post) throws IOException {

        String originalName = multiPartFile.getOriginalFilename();

        if(!this.isValid(multiPartFile)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not an image resource");
        }

        post.setImage(BlobProxy.generateProxy(multiPartFile.getInputStream(), multiPartFile.getSize()));

        return post;
    }

    public UserP createImage(MultipartFile multiPartFile, UserP user) throws IOException {

        String originalName = multiPartFile.getOriginalFilename();

        if(!this.isValid(multiPartFile)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not an image resource");
        }

        user.setImage(BlobProxy.generateProxy(multiPartFile.getInputStream(), multiPartFile.getSize()));

        return user;
    }

}
