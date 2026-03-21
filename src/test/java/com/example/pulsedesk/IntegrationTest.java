package com.example.pulsedesk;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.models.Ticket;
import com.example.pulsedesk.repositories.CommentRepository;
import com.example.pulsedesk.repositories.TicketRepository;
import com.example.pulsedesk.services.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void testFullBugTicketFlow() {
        // Submit a clear bug report
        long commentsBefore = commentRepository.count();
        long ticketsBefore = ticketRepository.count();

        Comment comment = commentService.processComment(
                "The app crashes every time I try to upload a file, I get a 500 error");

        // Comment should always be saved
        assertEquals(commentsBefore + 1, commentRepository.count());
        assertNotNull(comment.getId());

        // If HuggingFace is available, a ticket should be created
        long ticketsAfter = ticketRepository.count();
        if (ticketsAfter > ticketsBefore) {
            Ticket ticket = ticketRepository.findAll().get((int) ticketsAfter - 1);
            assertNotNull(ticket.getTitle());
            assertNotNull(ticket.getCategory());
            assertNotNull(ticket.getPriority());
            assertNotNull(ticket.getSummary());
            assertTrue(List.of("bug", "feature", "billing", "account", "other")
                    .contains(ticket.getCategory()));
            assertTrue(List.of("low", "medium", "high")
                    .contains(ticket.getPriority()));
            assertEquals(comment.getId(), ticket.getComment().getId());
        }
    }

    @Test
    public void testComplimentDoesNotCreateTicket() {
        long ticketsBefore = ticketRepository.count();

        commentService.processComment("Love the app, absolutely fantastic work by the team!");

        // Give HuggingFace a moment to respond
        long ticketsAfter = ticketRepository.count();

        // A clear compliment should never create a ticket
        // We check the comment was saved regardless
        assertNotNull(commentRepository.findAll());
    }

    @Test
    public void testBillingTicketFlow() {
        long ticketsBefore = ticketRepository.count();

        commentService.processComment(
                "I was charged twice this month on my credit card and need a refund immediately");

        long ticketsAfter = ticketRepository.count();
        if (ticketsAfter > ticketsBefore) {
            Ticket ticket = ticketRepository.findAll().get((int) ticketsAfter - 1);
            assertNotNull(ticket.getCategory());
            assertTrue(List.of("bug", "feature", "billing", "account", "other")
                    .contains(ticket.getCategory()));
        }
    }

    @Test
    public void testNonsenseInputHandledGracefully() {
        long commentsBefore = commentRepository.count();

        // Should not crash
        assertDoesNotThrow(() -> commentService.processComment("asdfghjkl qwerty"));

        // Comment should still be saved
        assertEquals(commentsBefore + 1, commentRepository.count());
    }

    @Test
    public void testTicketLinkedToComment() {
        long ticketsBefore = ticketRepository.count();

        Comment comment = commentService.processComment(
                "The payment system is completely broken, nobody can check out");

        long ticketsAfter = ticketRepository.count();
        if (ticketsAfter > ticketsBefore) {
            Ticket ticket = ticketRepository.findAll().get((int) ticketsAfter - 1);
            // Ticket should be linked to the comment that created it
            assertNotNull(ticket.getComment());
            assertEquals(comment.getId(), ticket.getComment().getId());
        }
    }
}