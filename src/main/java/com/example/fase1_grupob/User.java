package com.example.fase1_grupob;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.dynalink.linker.LinkerServices;

import java.util.ArrayList;
import java.util.List;

public class User
{
    private String profilePhoto;
    private String username;
    private String description;
    @JsonIgnore

    private List<Post> userPosts = new ArrayList<>();

    public User() 
    {
        this.profilePhoto= "profphoto1.jpg";
        this.username = "Default User";
        this.description = "Default Description";
    }

    public User(String description) 
    {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePhotoName()
    {
        return this.profilePhoto;
    }

    public void setProfilePhotoName(String name)
    {
        if(!name.isEmpty())
        {
          this.profilePhoto = name;  
        }
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

    public List getUserPosts(){
        return this.userPosts;
    }

    public void addPost(Post post){
        this.userPosts.add(post);
    }

    public void deletePost(Post post){
        this.userPosts.remove(post);
    }
    
}