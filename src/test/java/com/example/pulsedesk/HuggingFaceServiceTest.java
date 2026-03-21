package com.example.pulsedesk;

import com.example.pulsedesk.services.HuggingFaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HuggingFaceServiceTest {

    @Autowired
    private HuggingFaceService huggingFaceService;

    @Test
    public void testClassifyReturnsResult() {
        List<Map<String, Object>> result = huggingFaceService.classify(
                "The app crashes every time I open it",
                List.of("software problem", "compliment")
        );

        // Either returns a valid result or null if HuggingFace is unavailable
        if (result != null) {
            assertFalse(result.isEmpty());
            assertNotNull(result.get(0).get("label"));
            assertNotNull(result.get(0).get("score"));
        }
    }

    @Test
    public void testClassifyHandlesShortInput() {
        // Should not crash on short input
        assertDoesNotThrow(() -> huggingFaceService.classify(
                "bad",
                List.of("problem", "compliment")
        ));
    }
}