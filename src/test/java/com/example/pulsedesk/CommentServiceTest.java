package com.example.pulsedesk;

import com.example.pulsedesk.models.Comment;
import com.example.pulsedesk.repositories.CommentRepository;
import com.example.pulsedesk.repositories.TicketRepository;
import com.example.pulsedesk.services.CommentService;
import com.example.pulsedesk.services.HuggingFaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockitoBean
    private HuggingFaceService huggingFaceService;

    private List<Map<String, Object>> mockResult(String label) {
        return List.of(Map.of("label", label, "score", 0.99));
    }

    @Test
    public void testCommentIsSaved() {
        when(huggingFaceService.classify(any(), any()))
                .thenReturn(mockResult("nonsense, gibberish, or unrelated to software"));

        Comment result = commentService.processComment("Great app!");

        assertNotNull(result);
        assertEquals("Great app!", result.getText());
    }

    @Test
    public void testComplimentDoesNotCreateTicket() {
        long ticketsBefore = ticketRepository.count();

        when(huggingFaceService.classify(any(), any()))
                .thenReturn(mockResult("real user feedback about a software product or service"))
                .thenReturn(mockResult("compliment or praise"));

        commentService.processComment("Love the app, great work!");

        assertEquals(ticketsBefore, ticketRepository.count());
    }

    @Test
    public void testBugCommentCreatesTicket() {
        long ticketsBefore = ticketRepository.count();

        when(huggingFaceService.classify(any(), any()))
                .thenReturn(mockResult("real user feedback about a software product or service"))
                .thenReturn(mockResult("problem or complaint"))
                .thenReturn(mockResult("software or button not working as expected"))
                .thenReturn(mockResult("critical issue causing data loss or complete system failure affecting all users"))
                .thenReturn(mockResult("something is not working or broken"));

        commentService.processComment("The app crashes every time I open it");

        assertEquals(ticketsBefore + 1, ticketRepository.count());
    }

    @Test
    public void testHuggingFaceUnavailableDoesNotCrash() {
        when(huggingFaceService.classify(any(), any())).thenReturn(null);

        assertDoesNotThrow(() -> commentService.processComment("The app is broken"));
    }
}