package com.example.pulsedesk.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private LocalDateTime createdAt;

    public Comment(){}

    public Comment(String text){
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public long getId(){
        return id;
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text = text;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}
