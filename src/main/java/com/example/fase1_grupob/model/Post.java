package com.example.fase1_grupob.model;

import com.example.fase1_grupob.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Post {

    interface Basic{}
    @JsonView(Basic.class)
    private String imageName;
    @JsonView(Basic.class)
    private String description;
    @JsonView(Basic.class)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Category> categories;
    @JsonView(Basic.class)
    
    private String postTitle;
    @JsonView(Basic.class)
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;
    @JsonView(Basic.class)
    private int likes;
    @JsonView(Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonView(Basic.class)
    @ManyToMany
    private List<UserP> likedUsers;
    /*@ManyToOne*/
    /*private Long CreatorID;*/

    public Post() {

    }

    public Post(String description, String postTitle){
        this.description = description;
        this.postTitle = postTitle;
        this.categories = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likedUsers = new ArrayList<>();

    }

    public void setCategories(String categories){
        List<String> stringList = Arrays.stream(categories.split(" ")).toList();
        Set<Category> categoryList = new HashSet<>();
        for(String element: stringList){
            Category category = new Category(element);
            category.addPosts(this);
            categoryList.add(category);
        }
        this.categories = categoryList.stream().toList();
    }

    public void setCategories(List<Category> categories){
        this.categories = categories;
    }

    public List<Category> getCategories(){
        return this.categories;
    }


    public void setTitle(String title)
    {
        
        this.postTitle = title;
    }

    public String getTitle(){
        return this.postTitle;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getImageName(){
        return this.imageName;
    }

    public void setImageName(String name){
        this.imageName = name;
    }
    public String getDescription(){
        return this.description;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

    public void deleteComment(Comment comment){
        this.comments.remove(comment);
    }

    public List getComments(UserService u){
        for (Comment comment : this.comments) {
            if(u.findById(comment.getUserId()).isPresent()){
                comment.setUsername(u.findById(comment.getUserId()).get().getUsername());
            }
        }
        return this.comments;
    }

    public void setComments(Comment[] comments){
        this.comments = Arrays.stream(comments).toList();
    }


    public void addLike(UserP u){
        if (!this.likedUsers.contains(u)) {
            this.likedUsers.add(u);
            this.likes++;
        } else{
            this.likedUsers.remove(u);
            this.likes--;
        }
    }

    public int getLikes(){
        return this.likes;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }


    public boolean checkCategory(String category){
        for(int i = 0; i < this.categories.size();i++){
            for (String c : category.split(" ")) {
                if(this.categories.contains(c)){
                    return true;
                }
            }
        }
        return false;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Post [imageName=" + imageName + ", description=" + description + ", categories=" + categories + "]";
    }

    /*public Long getCreatorID(){
        return this.CreatorID;
    }

    public Long setCreatorID(Long id){
        this.CreatorID = id;
    }*/
}
