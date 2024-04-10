package com.example.fase1_grupob.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class UserP
{
    @Lob
    private Blob imageFile;
    @Column(columnDefinition = "LONGBLOB")
    private String username;
    @Column(columnDefinition = "LONGBLOB")
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @JsonIgnore
    @OneToMany
    private List<Post> userPosts = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Post> likedPosts = new ArrayList<>();


    public UserP()
    {
        super();
        this.imageFile= null;
        this.username = "Pepe";
        this.description = "Pepe the Duck: Nature lover and conservationist. Shares educational content about waterfowl and wildlife. Adventurous and creative, he shares his own illustrations and photographs of ducks and other wildlife.";
    }

    public UserP(String description)
    {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public void updateDescription(String newDescription) 
    {
        //If the description is empty, we don't want to save it.
        if(!newDescription.isEmpty())
        {
            this.description = newDescription;
        }
        
    }
    
    public void updateUsername(String newUsername)
    {
        //If the username is empty, we don't want to save it
        if(!newUsername.isEmpty())  
        {
            this.username = newUsername;
        }
        
    }

    public Blob getImage(){
        return this.imageFile;
    }

    public void setImage(Blob image){
        this.imageFile = image;
    }

    public List<Post> getUserPosts(){
        return this.userPosts;
    }

    public void addPost(Post post){
        this.userPosts.add(post);
    }

    public void deletePost(Post post){
        this.userPosts.remove(post);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addLikedPost(Optional<Post> post){
        post.ifPresent(value -> this.likedPosts.add(value));
    }

    public void deleteLikedPost(Optional<Post> post){
        post.ifPresent(value -> this.likedPosts.remove(value));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserP other = (UserP) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
}