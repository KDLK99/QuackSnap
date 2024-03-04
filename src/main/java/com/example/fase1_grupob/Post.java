package com.example.fase1_grupob;

import com.fasterxml.jackson.annotation.JsonView;

import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Post {
    interface Basic{}
    @JsonView(Basic.class)
    private String imageName;
    @JsonView(Basic.class)
    private String description;
    @JsonView(Basic.class)
    private String[] categories;
    @JsonView(Basic.class)
    private String postTitle;
    private List<Comment> comments = new ArrayList<>();
    @JsonView(Basic.class)
    private int likes;
    @JsonView(Basic.class)
    private Long id;


    public Post(String imageName, String description, String title){
        this.imageName = imageName;
        this.description = description;
        this.postTitle = title;
        this.categories = null;
    }

    public void setCategories(String categories){
        this.categories = categories.split(" ");
    }

    public void setTitle(String title){
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

    public List getComments(){
        return this.comments;
    }

    public void addLike(){
        this.likes++;
    }

    public int getLikes(){
        return this.likes;
    }

    public boolean checkCategory(String category){
        for(int i = 0; i < this.categories.length;i++){
            if(this.categories[i].equals(category)){
                return true;
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
