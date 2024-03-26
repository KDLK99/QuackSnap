package model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class User
{
    private String profilePhoto;
    private String username;
    private String description;
    private long id;
    @JsonIgnore

    private List<Post> userPosts = new ArrayList<>();

    public User() 
    {
        this.profilePhoto= "profphoto1.jpg";
        this.username = "Pepe";
        this.description = "Pepe the Duck: Nature lover and conservationist. Shares educational content about waterfowl and wildlife. Adventurous and creative, he shares his own illustrations and photographs of ducks and other wildlife.";
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
    
}