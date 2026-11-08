package edu.nccu.moviefinder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.nccu.moviefinder.service.WordCounter;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Member A: remove this line once WebPage.setScore is implemented")
class WebPageTest {

    private static KeywordList keywords() {
        KeywordList kws = new KeywordList();
        kws.add(new Keyword("movie", 3));
        kws.add(new Keyword("director", 2));
        kws.add(new Keyword("ticket", -1));
        return kws;
    }

    private static WebPage page(String content) {
        return new WebPage("https://example.com", "t", new WordCounter("https://example.com", content));
    }

    @Test
    void scoreIsTheWeightedSumOfCounts() throws IOException {
        WebPage page = page("movie movie director ticket");

        page.setScore(keywords());

        assertEquals(2 * 3 + 1 * 2 - 1, page.getScore());
        assertEquals(2, page.getCounts().get("movie"));
        assertEquals(1, page.getCounts().get("ticket"));
    }

    @Test
    void absentKeywordsAreRecordedAsZero() throws IOException {
        WebPage page = page("nothing relevant");

        page.setScore(keywords());

        assertEquals(0, page.getScore());
        assertEquals(0, page.getCounts().get("movie"));
    }

    @Test
    void scoringTwiceDoesNotAccumulate() throws IOException {
        WebPage page = page("movie");

        page.setScore(keywords());
        page.setScore(keywords());

        assertEquals(3, page.getScore());
    }
}
