package edu.nccu.moviefinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoogleQueryTest {

    private static String sample() throws IOException {
        try (InputStream in = GoogleQueryTest.class.getResourceAsStream("/tavily-sample.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void parseKeepsGoogleOrderAndMapsUrlToTitle() throws IOException {
        LinkedHashMap<String, String> results = new GoogleQuery("key", 20).parse(sample());

        assertEquals(List.of("https://a.example.com/cats", "https://b.example.org/film", "https://c.example.net/reviews"),
                new ArrayList<>(results.keySet()));
        assertEquals("Best Cat Movies", results.get("https://a.example.com/cats"));
    }

    @Test
    void parseReturnsEmptyWhenThereAreNoResults() {
        assertTrue(new GoogleQuery("key", 20).parse("{\"query\":\"cat\"}").isEmpty());
    }

    @Test
    void parseSkipsResultsWithoutAUrl() {
        String json = "{\"results\":[{\"title\":\"No link\"},{\"title\":\"Ok\",\"url\":\"https://ok.example\"}]}";

        assertEquals(List.of("https://ok.example"), new ArrayList<>(new GoogleQuery("key", 20).parse(json).keySet()));
    }

    @Test
    void aMissingApiKeyFailsWithAHelpfulMessage() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new GoogleQuery("", 20).query("cat"));

        assertTrue(e.getMessage().contains("TAVILY_API_KEY"));
    }

    @Test
    void parseKeepsTheFirstOfDuplicateUrlsAndFallsBackToTheUrlAsTitle() {
        String json = "{\"results\":[{\"title\":\"First\",\"url\":\"https://a.example\"},"
                + "{\"title\":\"Again\",\"url\":\"https://a.example\"},{\"url\":\"https://b.example\"}]}";

        LinkedHashMap<String, String> results = new GoogleQuery("key", 20).parse(json);

        assertEquals("First", results.get("https://a.example"));
        assertEquals("https://b.example", results.get("https://b.example"));
    }

    @Test
    void parseRejectsInvalidJson() {
        assertThrows(IllegalArgumentException.class, () -> new GoogleQuery("key", 20).parse("<html>"));
    }

    @Test
    void errorMessagesExplainCommonTavilyFailures() {
        assertTrue(GoogleQuery.describeError(401).contains("TAVILY_API_KEY"));
        assertTrue(GoogleQuery.describeError(432).contains("limit"));
        assertTrue(GoogleQuery.describeError(500).contains("500"));
    }
}
