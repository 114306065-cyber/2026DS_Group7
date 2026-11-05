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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Member C: remove this line once GoogleQuery is implemented")
class GoogleQueryTest {

    private static String sample() throws IOException {
        try (InputStream in = GoogleQueryTest.class.getResourceAsStream("/serpapi-sample.json")) {
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
    void parseReturnsEmptyWhenThereAreNoOrganicResults() {
        assertTrue(new GoogleQuery("key", 20).parse("{\"search_metadata\":{}}").isEmpty());
    }

    @Test
    void aMissingApiKeyFailsWithAHelpfulMessage() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new GoogleQuery("", 20).query("cat"));

        assertTrue(e.getMessage().contains("SERPAPI_KEY"));
    }
}
