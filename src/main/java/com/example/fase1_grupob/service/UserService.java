package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.repository.UserRepository;

//import org.h2.engine.User;
import org.hibernate.engine.jdbc.BlobProxy;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@Service
public class UserService {

    private ImageService imageService;
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public Collection<UserP> getAllUsers() {
        return userRepository.findAll();
    }

    public Collection<UserP> getAllUsersExceptAdmin()
    {
        Collection<UserP> noAdmin = this.getAllUsers();
        noAdmin.remove(this.findById(1).get());
        return noAdmin;
    }

    public Optional<UserP> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<UserP> findByName(String name){return userRepository.findByUsername(name);}


    public void addUser(UserP user) {
        userRepository.save(user);
    }

    public void save(UserP user) {
        this.userRepository.save(user);
    }

    public void save(UserP user, String username, String description, MultipartFile image) throws IOException {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);
        String safeHTML = policy.sanitize(description);

        if (!image.isEmpty() && this.findById(user.getId()).isPresent()) {
            user = this.imageService.createImage(image, user);
        }

        user.updateUsername(username);
        user.updateDescription(safeHTML);

        this.save(user);
    }

    public void deleteById(long id) 
    {
        
        if(!this.userRepository.findById(id).get().getUserPosts().isEmpty())
        {
            Optional<UserP> user = this.userRepository.findById(id);
            user.get().deleteAllPosts();
            this.userRepository.save(user.get());
            
        }
        Optional<UserP> user1 = this.userRepository.findById(id);
        this.userRepository.deleteById(id);
    }



    public List<UserP> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public void save(UserP user, String description, MultipartFile image) throws IOException{
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);
        String safeHTML = policy.sanitize(description);

        if (!image.isEmpty() && this.findById(user.getId()).isPresent()) {
            user = this.imageService.createImage(image, user);
        }

        user.updateDescription(safeHTML);

        this.save(user);
    }
}

