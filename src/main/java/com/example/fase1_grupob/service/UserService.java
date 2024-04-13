package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.UserP;
import com.example.fase1_grupob.repository.UserRepository;

//import org.h2.engine.User;
import org.hibernate.engine.jdbc.BlobProxy;
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

    List<String> permitedHTMLtags = new ArrayList<>();


    public UserService(UserRepository userRepository, ImageService imageService){
        this.userRepository = userRepository;
        this.imageService = imageService;
        UserP user = new UserP();
        this.addUser(user);
        this.permitedHTMLtags.add("<strong");
        this.permitedHTMLtags.add("<em");
        this.permitedHTMLtags.add("<u");
        this.permitedHTMLtags.add("<h1");
        this.permitedHTMLtags.add("<h2");
        this.permitedHTMLtags.add("<h3");
        this.permitedHTMLtags.add("<a");
        this.permitedHTMLtags.add("<ol");
        this.permitedHTMLtags.add("<li");
        this.permitedHTMLtags.add("<b");
        this.permitedHTMLtags.add("</strong");
        this.permitedHTMLtags.add("</em");
        this.permitedHTMLtags.add("</u");
        this.permitedHTMLtags.add("</h1");
        this.permitedHTMLtags.add("</h2");
        this.permitedHTMLtags.add("</h3");
        this.permitedHTMLtags.add("</a");
        this.permitedHTMLtags.add("</ol");
        this.permitedHTMLtags.add("</li");
        this.permitedHTMLtags.add("</b");
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

        if(description.contains("<") || description.contains(">")){
            List<String> phrases = Arrays.stream(description.split("<")).toList();

            for(String part: phrases){
                String part1 = "<" + part;

                if(part1.contains(">") && !stringContainsItemFromList(part1, this.permitedHTMLtags)){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insecure HTML tags");
                }

            }
        }
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


    public static boolean stringContainsItemFromList(String inputStr, List<String> items)
    {
        for (String item : items) {
            if (inputStr.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
