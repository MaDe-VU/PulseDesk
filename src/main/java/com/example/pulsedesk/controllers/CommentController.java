package com.example.pulsedesk.controllers;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.repositories.CommentRepository;
import com.example.pulsedesk.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comment createComment(@RequestBody Map<String, String> body){
        return commentService.processComment(body.get("text"));
    }

    @GetMapping
    public List<Comment> getComments(){
        return commentRepository.findAll();
    }
}
