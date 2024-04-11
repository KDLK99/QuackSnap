package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.repository.UserRepository;

//import org.h2.engine.User;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private ImageService imageService;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, ImageService imageService){
        this.userRepository = userRepository;
        this.imageService = imageService;
        UserP user = new UserP();
        this.addUser(user);
    }

    public Collection<UserP> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserP> findById(long id) {
        return userRepository.findById(id);
    }

    public void addUser(UserP user) {
        userRepository.save(user);
    }

    public void save (UserP user)
    {
        this.userRepository.save(user);
    }

    public  void save(UserP user, String username, String description, MultipartFile image) throws IOException {
        if(!image.isEmpty() && this.findById(user.getId()).isPresent())
        {
            user = this.imageService.createImage(image, user);
        }

        user.updateUsername(username);
        user.updateDescription(description);

        this.save(user);
    }

    public void deleteById(long id) {
        this.userRepository.deleteById(id);
    }

    public List<UserP> findByIds(List<Long> ids){
        return userRepository.findAllById(ids);
    }
}
