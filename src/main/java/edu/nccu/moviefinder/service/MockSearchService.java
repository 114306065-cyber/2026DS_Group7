package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.SearchResult;
import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 假資料版本,只在 mock profile 下生效,不連網、不用 API key。
 * 查詢含 movie 或 film 時,模擬「搜尋某一部電影」的結果;否則模擬廣泛主題的結果。
 */
@Service
@Primary
@Profile("mock")
public class MockSearchService extends SearchService {

    private static final double[] SCORES = {96, 94, 92, 90, 85, 80};

    public MockSearchService(KeywordExpander expander, GoogleQuery googleQuery,
                             SubLinkCrawler crawler, PagePrefetcher prefetcher, Ranker ranker) {
        super(expander, googleQuery, crawler, prefetcher, ranker);
    }

    @Override
    public ArrayList<SearchResult> search(String q) {
        String core = core(q);
        String[][] rows = isTitleSearch(q) ? titleRows(core) : themeRows(core);
        ArrayList<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            results.add(result(i + 1, core, rows[i][0], rows[i][1], rows[i][2], SCORES[i]));
        }
        return results;
    }

    @Override
    public LinkedHashMap<String, String> googleResults(String q) {
        List<SearchResult> ours = search(q);
        LinkedHashMap<String, String> tavily = new LinkedHashMap<>();
        for (int i : new int[] {2, 0, 4, 1}) {
            tavily.put(ours.get(i).getUrl(), ours.get(i).getTitle());
        }
        tavily.put("https://news.example.com/" + core(q), "Example News about " + core(q));
        tavily.put(ours.get(5).getUrl(), ours.get(5).getTitle());
        return tavily;
    }

    @Override
    public List<String> relatedKeywords(String q) {
        String core = core(q);
        return List.of(core + " films", "best " + core + " movies", core + " documentary", "family " + core, "classic " + core);
    }

    private static boolean isTitleSearch(String q) {
        String lower = q.toLowerCase(Locale.ROOT);
        return lower.contains("movie") || lower.contains("film");
    }

    private static String core(String q) {
        String core = q.toLowerCase(Locale.ROOT).replaceAll("\\b(movies?|films?)\\b", "").trim();
        return core.isEmpty() ? q.trim() : core;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** 標題、網址、摘要。 */
    private static String[][] titleRows(String core) {
        String title = capitalize(core);
        String slug = core.replace(' ', '_');
        return new String[][] {
            {title + " (2024 film) - Wikipedia", "https://en.wikipedia.org/wiki/" + capitalize(slug) + "_(2024_film)",
                title + " is a 2024 comedy-drama film about an unlikely bond and the people it changes."},
            {title + " | Official Website", "https://" + slug.replace("_", "") + "movie.example.com/",
                "Watch the trailer, meet the cast, and find out where to stream " + title + "."},
            {title + " (2024) | Rotten Tomatoes", "https://www.rottentomatoes.com/m/" + slug + "_2024",
                "Critics consensus, audience score, and reviews for " + title + "."},
            {title + " (2024) - IMDb", "https://www.imdb.com/title/tt0000001/",
                "Cast, crew, ratings, and trivia for " + title + "."},
            {"Review: " + title + " is warm, strange, and very funny", "https://reviews.example.org/" + slug,
                "A review of " + title + ", a comedy about connection and the awkward moments along the way."},
            {"What did you think of " + title + "? : r/movies", "https://www.reddit.com/r/movies/comments/example/",
                "Fans discuss the ending, the performances, and their favourite scenes."},
        };
    }

    private static String[][] themeRows(String core) {
        String title = capitalize(core);
        String slug = core.replace(' ', '-');
        return new String[][] {
            {"The 20 Best " + title + " Movies of All Time - Rotten Tomatoes", "https://www.rottentomatoes.com/guides/best-" + slug + "-movies",
                "A guide to the best movies about " + core + ", featuring classics and modern favourites."},
            {title + " Movies: Our Top Picks - IMDb", "https://www.imdb.com/list/ls000000001/",
                "A curated list of must-watch movies about " + core + ", from heartwarming stories to epic adventures."},
            {"40 Great " + title + " Movies for the Whole Family", "https://collider.example.com/" + slug + "-movies/",
                "From animated classics to true stories, here are the best " + core + " movies for all ages."},
            {"The Best " + title + " Movies - Common Sense Media", "https://www.commonsensemedia.org/lists/" + slug + "-movies",
                "Kid-friendly " + core + " movies with age ratings and parent reviews."},
            {"List of films about " + core + " - Wikipedia", "https://en.wikipedia.org/wiki/List_of_films_about_" + slug,
                "An overview of films about " + core + ", their history, and notable examples across different eras."},
            {"Movies about " + core + "? : r/movies", "https://www.reddit.com/r/movies/comments/example2/",
                "Users share their favourite movies about " + core + " and a few hidden gems."},
        };
    }

    private SearchResult result(int rank, String core, String title, String url, String snippet, double score) {
        HashMap<String, Double> breakdown = new HashMap<>();
        breakdown.put(core, score * 0.50);
        breakdown.put("review", score * 0.25);
        breakdown.put("director", score * 0.15);
        breakdown.put("movie", score * 0.12);
        breakdown.put("ticket", score * -0.02);

        HashMap<String, Integer> counts = new HashMap<>();
        counts.put(core, 8);
        counts.put("review", 4);
        WebNode root = WebNode.fixture(WebPage.fixture(url, title, counts, score * 0.8), score);
        for (String sub : new String[] {"Cast and crew", "Reviews"}) {
            HashMap<String, Integer> childCounts = new HashMap<>();
            childCounts.put(core, 2);
            childCounts.put("review", 1);
            root.addChild(WebNode.fixture(
                    WebPage.fixture(url + "#" + sub.toLowerCase(Locale.ROOT).replace(' ', '-'), sub, childCounts, score * 0.2),
                    score * 0.2));
        }
        return new SearchResult(rank, title, url, score, breakdown, new WebTree(root), snippet);
    }
}
