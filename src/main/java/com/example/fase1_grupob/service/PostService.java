package com.example.fase1_grupob.service;

import com.example.fase1_grupob.model.Category;
import com.example.fase1_grupob.model.Comment;
import com.example.fase1_grupob.model.Post;
import com.example.fase1_grupob.repository.CategoryRepository;
import com.example.fase1_grupob.repository.CommentRepository;
import com.example.fase1_grupob.repository.PostRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class PostService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "files");
    private PostRepository postRepository;
    private ImageService imageService;
    private UserService userService;
    private CategoryRepository categoryRepository;
    private CommentRepository commentRepository;


    public PostService(PostRepository postRepository, ImageService imageService, UserService userService, CategoryRepository categoryRepository, CommentRepository commentRepository){
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
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
            post.setCategories(imageCategory,this.categoryRepository.findAll());
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



    public Post save(Post post, Long id){
        //post.setCreatorID(id);
        return this.postRepository.save(post);
    }

    public void deleteById(long id) {
        Optional<Post> post = this.postRepository.findById(id);
        List<Category>categories = post.get().getCategories();

        for(Category category: categories)
        {
            if(this.categoryRepository.findAll().contains(category))
            {
                category.deletePost(post.get());
                this.categoryRepository.save(category);
            }
        }

        this.postRepository.deleteById(id);
    }

    public List<Post> filteredPosts(List<String> categories){
        List<Category> categoryList = new ArrayList<>();
        for(String category: categories){
            category.toLowerCase();
            Category category1 = new Category(category);
            categoryList.add(category1);
        }

        List<Post> posts = new ArrayList<>();
        for(Category category: categoryList){
            if(this.categoryRepository.findAll().contains(category)){
                category.setId(this.categoryRepository.findAll().get(this.categoryRepository.findAll().indexOf(category)).getId());

            }
            else if(category.getId() == null){
                category.setId(0L);
            }
            posts.addAll(this.postRepository.findPostsByCategoryID(Math.toIntExact(category.getId())));
        }
        return posts;
    }

    public void deleteComment(int postId, int commentPos){
        if(this.findById(postId).isPresent()) {
            Comment comment = this.findById(postId).get().getComments().get(commentPos);
            this.findById(postId).get().deleteComment(commentPos);

            this.commentRepository.delete(comment);

        }
    }

    public String uploadFile(int index, MultipartFile file){
        Optional<Post> post = this.postRepository.findById((long) index);
        post.get().setAdditionalInformationFile(file.getOriginalFilename());


        String fileName = file.getOriginalFilename();

        if(!fileName.matches(".*\\.(pdf)")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not a file resource");
        }

        this.save(post.get(), post.get().getId());

        Path filePath = FILES_FOLDER.resolve(fileName);
        try {
            file.transferTo(filePath);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't save file locally", ex);
        }

        return fileName;

    }

    public ResponseEntity<Object> downloadFile(int index) throws MalformedURLException {
        if(this.postRepository.findById((long)index).isPresent()) {

            Path filePath = FILES_FOLDER.resolve(this.postRepository.findById((long) index).get().getAdditionalInformationFile());

            if(!Files.exists(filePath)){
                return ResponseEntity.notFound().build();
            }

            Resource file = new UrlResource(filePath.toUri());


            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(file);

        }else{
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get local file");
        }
    }



}
