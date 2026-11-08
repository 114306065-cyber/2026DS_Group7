package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.SearchResult;
import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * HW8 用的暫時版本,只在 hw8 profile 下生效。
 * 直接把 Tavily 的結果包成 SearchResult,不計分、不爬子網頁,所以不會用到 C 的 SearchService 流程。
 * C 的完整流程可以使用後,就刪除這個檔案。
 */
@Service
@Primary
@Profile("hw8")
public class Hw8SearchService extends SearchService {
    private static final Logger log = LoggerFactory.getLogger(Hw8SearchService.class);

    public Hw8SearchService(KeywordExpander expander, GoogleQuery googleQuery,
                            SubLinkCrawler crawler, PagePrefetcher prefetcher, Ranker ranker) {
        super(expander, googleQuery, crawler, prefetcher, ranker);
    }

    /**
     * 依 Tavily 的原順序回傳結果,rank 從 1 開始。分數為 0,breakdown 為空,樹只有根節點。
     * 查詢失敗時回傳空的清單,讓頁面顯示「沒有找到結果」,原因寫在 log。
     */
    @Override
    public ArrayList<SearchResult> search(String q) {
        ArrayList<SearchResult> results = new ArrayList<>();
        LinkedHashMap<String, String> hits;
        try {
            hits = googleQuery.query(q);
        } catch (IOException | IllegalStateException e) {
            log.warn("Tavily search failed: {}", e.getMessage());
            return results;
        }

        int rank = 1;
        for (Map.Entry<String, String> hit : hits.entrySet()) {
            String url = hit.getKey();
            String title = hit.getValue();
            WebPage page = WebPage.fixture(url, title, new HashMap<>(), 0);
            WebTree tree = new WebTree(WebNode.fixture(page, 0));
            results.add(new SearchResult(rank++, title, url, 0, new HashMap<>(), tree));
        }
        return results;
    }
}
