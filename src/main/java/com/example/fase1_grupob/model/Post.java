package com.example.fase1_grupob.model;

import com.example.fase1_grupob.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Post {

    interface Basic{}
    @JsonView(Basic.class)
    @Lob
    private Blob image;
    @JsonView(Basic.class)
    @Column(columnDefinition = "LONGBLOB")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.PERSIST, CascadeType.MERGE})
    private List<Category> categories;
    @JsonView(Basic.class)
    @Column(columnDefinition = "LONGBLOB")
    private String postTitle;
    @JsonView(Basic.class)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;
    @JsonView(Basic.class)
    private int likes;

    @JsonView(Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST})
    private List<UserP> likedUsers;
    @JsonView(Basic.class)
    private int counter = 0;
    @JsonView(Basic.class)
    private String additionalInformationFile;

    @ManyToOne
    private UserP creator;


    public Post() {
        this.description = null;
        this.postTitle = null;
        this.categories = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likedUsers = new ArrayList<>();
    }

    public Post(String description, String postTitle){
        this.description = description;
        this.postTitle = postTitle;
        this.categories = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likedUsers = new ArrayList<>();

    }

    public Post(String description, String postTitle, String categories){
        this.description = description;
        this.postTitle = postTitle;
        List<String> stringCategories = Arrays.stream(categories.split(" ")).toList();
        List<Category> categoryList = new ArrayList<>();
        for(String category: stringCategories){
            categoryList.add(new Category(category));
        }
        this.categories = categoryList;


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
        if(!title.isEmpty()){
            this.postTitle = title;
        }
    }

    public String getTitle(){
        return this.postTitle;
    }

    public void setDescription(String description){
        if(!description.isEmpty()){
            this.description = description;
        }
    }

    public Blob getImage(){
        return this.image;
    }

    public void setImage(Blob image){
        this.image = image;
    }
    public String getDescription(){
        return this.description;
    }

    public void addComment(Comment comment){
        comment.setPosition(this.counter);
        this.counter++;
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

    public List<Comment> getComments(){
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
        return "Post [ description=" + description + ", categories=" + categories + "]";
    }

    public void addLikeUser(UserP userP){
        this.likedUsers.add(userP);
    }

    public void deleteLikeUser(UserP userP){
        this.likedUsers.remove(userP);
    }

    public void deleteComment(int pos){
        this.comments.remove(this.comments.get(pos));

        if(!this.comments.isEmpty()) {
            for (int i = pos; i < this.comments.size(); i++) {
                this.comments.get(i).setPosition(this.comments.get(i).getPosition() - 1);
            }
        }
        this.counter--;
    }

    public List<UserP> getLikedUsers(){
        return this.likedUsers;
    }

    public int getCounter(){
        return this.counter;
    }

    public void setAdditionalInformationFile(String name){
        this.additionalInformationFile = name;
    }

    public String getAdditionalInformationFile(){
        return this.additionalInformationFile;
    }


    public void deleteAllUsers()
    {
        for(UserP userp : this.likedUsers)
        {
            this.likedUsers.remove(userp);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addCreator(UserP userP){
        this.creator = userP;
    }

    public UserP getCreator(){
        return this.creator;
    }

}
