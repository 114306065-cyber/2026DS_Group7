package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.SearchResult;
import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 假資料版本,只在 mock profile 下生效,不連網、不用 API key。
 * 讓前端不必等後端完成就能開發。
 */
@Service
@Primary
@Profile("mock")
public class MockSearchService extends SearchService {

    public MockSearchService(KeywordExpander expander, GoogleQuery googleQuery,
                             SubLinkCrawler crawler, PagePrefetcher prefetcher, Ranker ranker) {
        super(expander, googleQuery, crawler, prefetcher, ranker);
    }

    @Override
    public ArrayList<SearchResult> search(String q) {
        ArrayList<SearchResult> results = new ArrayList<>();
        results.add(result(1, q, "Example Movie Reviews", "https://reviews.example.com/", 12, 6));
        results.add(result(2, q, "Example Film Database", "https://films.example.org/", 7, 4));
        results.add(result(3, q, "Example Cinema Blog", "https://blog.example.net/", 3, 1));
        return results;
    }

    @Override
    public LinkedHashMap<String, String> googleResults(String q) {
        LinkedHashMap<String, String> google = new LinkedHashMap<>();
        google.put("https://films.example.org/", "Example Film Database");
        google.put("https://news.example.com/" + q, "Example News about " + q);
        google.put("https://reviews.example.com/", "Example Movie Reviews");
        return google;
    }

    private SearchResult result(int rank, String q, String title, String url, int queryHits, int reviewHits) {
        HashMap<String, Integer> rootCounts = new HashMap<>();
        rootCounts.put(q, queryHits);
        rootCounts.put("review", reviewHits);
        double rootScore = queryHits * 5.0 + reviewHits * 3.0;

        HashMap<String, Integer> childCounts = new HashMap<>();
        childCounts.put(q, 2);
        childCounts.put("review", 1);
        double childScore = 2 * 5.0 + 1 * 3.0;

        double total = rootScore + 0.5 * childScore;
        WebNode root = WebNode.fixture(WebPage.fixture(url, title, rootCounts, rootScore), total);
        root.addChild(WebNode.fixture(
                WebPage.fixture(url + "sub", title + " / Subpage", childCounts, childScore), childScore));

        HashMap<String, Double> breakdown = new HashMap<>();
        breakdown.put(q, (queryHits + 0.5 * 2) * 5.0);
        breakdown.put("review", (reviewHits + 0.5 * 1) * 3.0);
        return new SearchResult(rank, title, url, total, breakdown, new WebTree(root));
    }
}
