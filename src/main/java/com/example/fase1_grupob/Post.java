package com.example.fase1_grupob;

import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Post {
    private String imageName;
    private String description;
    private String[] categories;
    private String postTitle;
    private List<Comment> comments = new ArrayList<>();


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

    @Override
    public String toString() {
        return "Post [imageName=" + imageName + ", description=" + description + ", categories=" + categories + "]";
    }
}
