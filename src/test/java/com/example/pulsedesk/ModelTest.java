package com.example.pulsedesk;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.models.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ModelTest {

    @Test
    public void testCommentGettersAndSetters() {
        Comment comment = new Comment("Test text");
        comment.setCreatedAt(LocalDateTime.now());

        assertEquals("Test text", comment.getText());
        assertNotNull(comment.getCreatedAt());

        comment.setText("Updated text");
        assertEquals("Updated text", comment.getText());
    }

    @Test
    public void testTicketGettersAndSetters() {
        Comment comment = new Comment("Test");
        Ticket ticket = new Ticket("Title", "bug", "high", "Summary", comment);

        assertEquals("Title", ticket.getTitle());
        assertEquals("bug", ticket.getCategory());
        assertEquals("high", ticket.getPriority());
        assertEquals("Summary", ticket.getSummary());
        assertEquals(comment, ticket.getComment());

        ticket.setTitle("New Title");
        ticket.setCategory("feature");
        ticket.setPriority("low");
        ticket.setSummary("New Summary");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setComment(new Comment("New comment"));

        assertEquals("New Title", ticket.getTitle());
        assertEquals("feature", ticket.getCategory());
        assertEquals("low", ticket.getPriority());
        assertEquals("New Summary", ticket.getSummary());
        assertNotNull(ticket.getCreatedAt());
    }
}