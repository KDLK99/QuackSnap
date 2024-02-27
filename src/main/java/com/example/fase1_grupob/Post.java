package com.example.fase1_grupob;

import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String imageName;
    private String description;
    private String[] categories;


    public Post(String imageName, String description){
        this.imageName = imageName;
        this.description = description;
        this.categories = null;
    }

    public void setCategories(String categories){
        this.categories = categories.split(" ");
    }

    public String getImageName(){
        return this.imageName;
    }

    public void setImageName(String name){
        this.imageName = name;
    }

    @Override
    public String toString() {
        return "Post [imageName=" + imageName + ", description=" + description + ", categories=" + categories + "]";
    }
}
