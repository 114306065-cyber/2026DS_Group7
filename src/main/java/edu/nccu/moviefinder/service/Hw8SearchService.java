package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.SearchResult;
import java.util.ArrayList;
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

    public Hw8SearchService(KeywordExpander expander, GoogleQuery googleQuery,
                            SubLinkCrawler crawler, PagePrefetcher prefetcher, Ranker ranker) {
        super(expander, googleQuery, crawler, prefetcher, ranker);
    }

    /**
     * 依 Tavily 的原順序回傳結果,rank 從 1 開始。
     * 提示:每筆結果用 WebPage.fixture(url, title, new HashMap<>(), 0) 建立網頁,
     * 樹只有根節點(WebNode.fixture(page, 0)),breakdown 給空的 HashMap,score 給 0。
     * 查詢失敗時回傳空的清單,讓頁面顯示「沒有找到結果」。
     */
    @Override
    public ArrayList<SearchResult> search(String q) {
        // TODO(Member E, HW8)
        throw new UnsupportedOperationException("TODO(Member E): Hw8SearchService.search");
    }
}
