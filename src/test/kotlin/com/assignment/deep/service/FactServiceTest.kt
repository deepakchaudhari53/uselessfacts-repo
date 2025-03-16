package com.assignment.deep.service

import com.assignment.deep.exception.FactNotFoundException
import com.assignment.deep.model.CachedFact
import com.assignment.deep.model.FactStatistics
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FactServiceTest {

    FactService factService;
    CachedFact testFact;

    @BeforeEach
    void setup() {
        factService = new FactService();
        testFact = new CachedFact(
                "test-id", "123", "Test fact",
        "test", "http://source.com", "en", "http://permalink.com"
        );
    }

    @Test
    void saveAndRetrieveFact_Success() {
        factService.saveFact(testFact);
        CachedFact retrieved = factService.getFact("test-id");

        Assertions.assertEquals("Test fact", retrieved.getText());
        assertEquals(0, retrieved.getAccessCount());
    }

    @Test
    void getFact_NotFound() {
        assertThrows(FactNotFoundException.class, () -> {
            factService.getFact("non-existent-id");
        });
    }

    @Test
    void incrementAccessCount() {
        factService.saveFact(testFact);
        factService.getFact("test-id"); // First access
        CachedFact accessed = factService.getFact("test-id"); // Second access

        assertEquals(2, accessed.getAccessCount());
    }

    @Test
    void getStatistics_MultipleFacts() {
        factService.saveFact(testFact);
        factService.saveFact(new CachedFact("id2", "456", "Another fact",
            "test", "http://source.com", "en", "http://permalink.com"));

        List<FactStatistics> stats = factService.getStatistics();
        assertEquals(2, stats.size());
        assertTrue(stats.stream().anyMatch(s -> s.getShortenedUrl().equals("test-id")));
    }
}
