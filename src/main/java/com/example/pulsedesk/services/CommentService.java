package com.example.pulsedesk.services;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.models.Ticket;
import com.example.pulsedesk.repositories.CommentRepository;
import com.example.pulsedesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HuggingFaceService huggingFaceService;

    public Comment processComment(String text) {
        // 1. Save the comment
        Comment comment = new Comment(text);
        commentRepository.save(comment);

        // 2. Classify if it needs a ticket
        List<Map<String, Object>> ticketCheck = huggingFaceService.classify(text,
                List.of("support ticket", "compliment"));

        String topLabel = (String) ticketCheck.get(0).get("label");

        if (topLabel.equals("support ticket")) {

            List<Map<String, Object>> categoryResult = huggingFaceService.classify(text,
                    List.of("bug", "feature", "billing", "account", "other"));
            String category = (String) categoryResult.get(0).get("label");

            List<Map<String, Object>> priorityResult = huggingFaceService.classify(text,
                    List.of("high", "medium", "low"));
            String priority = (String) priorityResult.get(0).get("label");

            String title = text.length() > 60 ? text.substring(0, 60) + "..." : text;
            String summary = text.length() > 150 ? text.substring(0, 150) + "..." : text;

            Ticket ticket = new Ticket(title, category, priority, summary, comment);
            ticketRepository.save(ticket);
        }

        return comment;
    }
}