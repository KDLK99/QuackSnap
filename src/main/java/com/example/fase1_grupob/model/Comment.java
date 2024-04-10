package com.example.fase1_grupob.model;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    @Column(columnDefinition = "LONGBLOB")
    private String username;
    @Column(columnDefinition = "LONGBLOB")
    private String text;
    private int position;

    public Comment() {

    }

    public Comment(Long userId, String text, String username) {
        super();
        this.userId = userId;
        this.text = text;
        this.username = username;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition(){
        return this.position;
    }

    public String toString(){
        return "<strong>" + this.userId + ": "  + "</strong>"+ this.text;
    }

}
