package com.example.fase1_grupob.model;

import com.example.fase1_grupob.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Post {
    interface Basic{}
    @JsonView(Basic.class)
    private String imageName;
    @JsonView(Basic.class)
    private String description;
    @JsonView(Basic.class)
    private List<String> categories;
    @JsonView(Basic.class)
    
    private String postTitle;
    @JsonView(Basic.class)
    private List<Comment> comments;
    @JsonView(Basic.class)
    private int likes;
    @JsonView(Basic.class)
    private Long id;
    @JsonView(Basic.class)
    private List<User> likedUsers;


    public Post(String description, String postTitle){
        this.description = description;
        this.postTitle = postTitle;
        this.categories = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likedUsers = new ArrayList<>();

    }

    public void setCategories(String categories){
        this.categories = Arrays.stream(categories.split(" ")).toList();
    }

    public List<String> getCategories(){
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
            comment.setUsername(u.findById(comment.getUserId()).getUsername());
        }
        return this.comments;
    }

    public void setComments(Comment[] comments){
        this.comments = Arrays.stream(comments).toList();
    }


    public void addLike(User u){
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
}