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
        //Save the comment
        Comment comment = new Comment(text);
        commentRepository.save(comment);

        //Classify if it needs a ticket
        List<Map<String, Object>> relevanceCheck = huggingFaceService.classify(text,
                List.of("real user feedback about a software product or service",
                        "nonsense, gibberish, or unrelated to software"));
        if(relevanceCheck == null) return comment;
        String relevanceLabel = (String) relevanceCheck.get(0).get("label");
        double relevanceScore = (double) relevanceCheck.get(0).get("score");

        if (relevanceLabel.equals("nonsense, gibberish, or unrelated to software") && relevanceScore >= 0.75) {
            return comment;
        }
        else{
            relevanceLabel = "real user feedback about a software product or service";
        }
        List<Map<String, Object>> ticketCheck = huggingFaceService.classify(text, List.of("problem or complaint",
                "compliment or praise"));
        if (ticketCheck == null) return comment;

        String topLabel = (String) ticketCheck.get(0).get("label");
        topLabel = topLabel
                .replace("problem or complaint", "support ticket")
                .replace("compliment or praise", "compliment");

        if (topLabel.equals("support ticket")) {
            List<Map<String, Object>> categoryResult = huggingFaceService.classify(text,
                    List.of("software or button not working as expected",
                            "payment or invoice issue",
                            "new feature or improvement request",
                            "problem to my specific account, such as being locked, banned, or losing profile data",
                            "other"));
            if(categoryResult == null) return comment;
            String category = (String) categoryResult.get(0).get("label");
            category = category
                    .replace("software or button not working as expected", "bug")
                    .replace("payment or invoice issue", "billing")
                    .replace("new feature or improvement request", "feature")
                    .replace("problem to my specific account, such as being locked, banned, or losing profile data", "account")
                    .replace("other", "other");
            //is it critical or not?
            List<Map<String, Object>> criticalCheck = huggingFaceService.classify(text,
                    List.of("critical system failure or data loss", "minor or moderate issue"));
            if(criticalCheck == null) return comment;
            String criticalLabel = (String) criticalCheck.get(0).get("label");

            String priority;
            if (criticalLabel.equals("critical system failure or data loss")) {
                priority = "high";
            } else {
                //is it functional or purely cosmetic?
                List<Map<String, Object>> cosmeticCheck = huggingFaceService.classify(text,
                        List.of("something is not working or broken",
                                "visual or cosmetic issue like font color or spelling"));
                String cosmeticLabel = (String) cosmeticCheck.get(0).get("label");
                priority = cosmeticLabel.equals("visual or cosmetic issue like font color or spelling") ? "low" : "medium";
            }
            String titleText = text.length() > 50 ? text.substring(0, 50) + "..." : text;
            String title = "[" + category.toUpperCase() + "][" + priority.toUpperCase() + "] " + titleText;
            String summary = text.length() > 150 ? text.substring(0, 150) + "..." : text;

            Ticket ticket = new Ticket(title, category, priority, summary, comment);
            ticketRepository.save(ticket);
        }

        return comment;
    }
}