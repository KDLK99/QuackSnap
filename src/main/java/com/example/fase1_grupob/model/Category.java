package com.example.fase1_grupob.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Entity
public class Category {
    private String category;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Post> posts;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Category(){

        this.posts = new ArrayList<>();
    }

    public Category(String category) {
        this.category = category;
        this.posts = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }

    public void addPosts(Post post){
        this.posts.add(post);
    }

    public void deletePost(Post post){
        this.posts.remove(post);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category1 = (Category) o;
        return Objects.equals(category, category1.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }
}
