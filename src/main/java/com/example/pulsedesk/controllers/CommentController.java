package com.example.pulsedesk.controllers;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public Comment createComment(@RequestBody Comment comment){
        return commentRepository.save(comment);
    }

    @GetMapping
    public List<Comment> getComments(){
        return commentRepository.findAll();
    }
}
