package com.example.fase1_grupob.model;

import com.example.fase1_grupob.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Post {

    interface Basic{}
    @JsonView(Basic.class)
    private String imageName;
    @JsonView(Basic.class)
    private String description;
    @JsonIgnore

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.PERSIST})

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
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
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

    public Post(String description, String postTitle, List<Category> categories, List<Comment> comments, List<UserP> userPS){
        this.description = description;
        this.postTitle = postTitle;
        this.categories = categories;
        this.comments = comments;
        this.likedUsers = userPS;

    }

    public void setCategories(String categories, List<Category> allCategories){
        List<String> stringList = Arrays.stream(categories.split(" ")).toList();
        Set<Category> categoryList = new HashSet<>();
        if(!(this.categories == null)){
            categoryList = new HashSet<>(this.categories);
        }

        for(String element: stringList){
            Category category = new Category(element);
            if(allCategories.contains(category)){
                allCategories.get(allCategories.indexOf(category)).addPosts(this);
                categoryList.add(allCategories.get(allCategories.indexOf(category)));
            }else {
                category.addPosts(this);
                categoryList.add(category);
            }
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

    public void addLikeUser(UserP userP){
        this.likedUsers.add(userP);
    }

    public void deleteLikeUser(UserP userP){
        this.likedUsers.remove(userP);
    }

    public void deleteComment(int id){
        this.comments.remove(this.comments.get(--id));
    }

    /*public Long getCreatorID(){
        return this.CreatorID;
    }

    public Long setCreatorID(Long id){
        this.CreatorID = id;
    }*/
}
