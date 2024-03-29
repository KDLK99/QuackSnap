package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {
    private PostRepository postRepository;
    private ImageService imageService;
    private UserService userService;


    public PostService(PostRepository postRepository, ImageService imageService, UserService userService){
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.userService = userService;
    }
    public Collection<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }


    public Post save(Post post, Long id, MultipartFile imageField, String imageCategory, String imageDesc, String postTitle) {

        if (imageField != null && !imageField.isEmpty()){
            String path = imageService.createImage(imageField);
            post.setImageName(path);
        }
        if(!imageCategory.isEmpty()){
            post.setCategories(imageCategory);
        }

        if(!imageDesc.isEmpty()){
            post.setDescription(imageDesc);
        }

        if(!postTitle.isEmpty()){
            post.setTitle(postTitle);
        }
        //post.setCreatorID(id);


        if(post.getImageName() == null || post.getImageName().isEmpty()) post.setImageName("no-image.png");

        return postRepository.save(post);
    }


    public Post save(Post post, Long id, MultipartFile imageField){
        if (imageField != null && !imageField.isEmpty()){
            String path = imageService.createImage(imageField);
            post.setImageName(path);
        }
        //post.setCreatorID(id);
        if(post.getImageName() == null || post.getImageName().isEmpty()) post.setImageName("no-image.png");
        return postRepository.save(post);
    }

    public Post save(Post post, Long id){
        //post.setCreatorID(id);
        return this.postRepository.save(post);
    }

    public void deleteById(long id) {
        this.postRepository.deleteById(id);
    }

    public List<Post> filteredPosts(List<String> categories){
        List<Category> categoryList = new ArrayList<>();
        for(String category: categories){
            category.toLowerCase();
            Category category1 = new Category(category);
            categoryList.add(category1);
        }



        return this.postRepository.findAllByCategories(categoryList);
    }


}
