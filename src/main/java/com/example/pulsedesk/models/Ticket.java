package com.example.pulsedesk.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String priority;
    private String summary;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Ticket(){}

    public Ticket(String title, String category, String priority, String summary, Comment comment){
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.summary = summary;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId(){
        return id;
    }
    public String getCategory(){
        return category;
    }
    public String getTitle(){
        return title;
    }
    public String getPriority(){
        return priority;
    }
    public String getSummary(){
        return summary;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public Comment getComment(){
        return comment;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public void setPriority(String priority){
        this.priority = priority;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    public void setComment(Comment comment){
        this.comment = comment;
    }
}
