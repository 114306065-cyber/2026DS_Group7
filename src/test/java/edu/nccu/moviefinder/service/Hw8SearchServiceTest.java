package edu.nccu.moviefinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.nccu.moviefinder.model.SearchResult;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class Hw8SearchServiceTest {

    private static Hw8SearchService service(GoogleQuery google) {
        return new Hw8SearchService(new KeywordExpander(), google, new SubLinkCrawler(5), new PagePrefetcher(), new Ranker());
    }

    @Test
    void keepsTavilyOrderAndNumbersFromOne() {
        GoogleQuery google = new GoogleQuery("key", 10) {
            @Override
            public LinkedHashMap<String, String> query(String q) {
                LinkedHashMap<String, String> hits = new LinkedHashMap<>();
                hits.put("https://b.example", "B");
                hits.put("https://a.example", "A");
                return hits;
            }
        };

        List<SearchResult> results = service(google).search("cat");

        assertEquals(2, results.size());
        assertEquals("https://b.example", results.get(0).getUrl());
        assertEquals(1, results.get(0).getRank());
        assertEquals("A", results.get(1).getTitle());
        assertEquals(2, results.get(1).getRank());
        assertEquals("https://a.example", results.get(1).getTree().getRoot().getWebPage().getUrl());
    }

    @Test
    void returnsNoResultsWhenTavilyFails() {
        GoogleQuery google = new GoogleQuery("key", 10) {
            @Override
            public LinkedHashMap<String, String> query(String q) throws IOException {
                throw new IOException("HTTP 500");
            }
        };

        assertTrue(service(google).search("cat").isEmpty());
    }

    @Test
    void returnsNoResultsWhenTheKeyIsMissing() {
        assertTrue(service(new GoogleQuery("", 10)).search("cat").isEmpty());
    }
}
