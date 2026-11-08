package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.KeywordList;
import edu.nccu.moviefinder.model.SearchResult;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 串起整個搜尋流程:補字 → 搜尋 → 建樹 → 計分 → 排序 → 找新關鍵字 → 第二輪。
 * 這是 singleton,所以 KeywordList 必須是方法內的區域變數,不可放在欄位。
 */
@Service
public class SearchService {
    public static final int MAX_ROUND = 2;

    protected final KeywordExpander expander;
    protected final GoogleQuery googleQuery;
    protected final SubLinkCrawler crawler;
    protected final PagePrefetcher prefetcher;
    protected final Ranker ranker;

    public SearchService(KeywordExpander expander, GoogleQuery googleQuery,
                         SubLinkCrawler crawler, PagePrefetcher prefetcher, Ranker ranker) {
        this.expander = expander;
        this.googleQuery = googleQuery;
        this.crawler = crawler;
        this.prefetcher = prefetcher;
        this.ranker = ranker;
    }

    /**
     * 回傳依分數排序的結果,rank 從 1 開始,最多跑 MAX_ROUND 輪。
     * 單一網站失敗時略過該網站,不可讓整次搜尋失敗。
     * 搜尋引擎整個無法使用時(GoogleQuery 丟出 IOException 或 IllegalStateException),
     * 丟出 SearchUnavailableException,讓介面顯示錯誤訊息而不是「沒有找到結果」。
     */
    public ArrayList<SearchResult> search(String q) {
        // TODO(Member C, Stage 3 → 4)
        throw new UnsupportedOperationException("TODO(Member C): SearchService.search");
    }

    /** 跑一輪:對每個查詢字串搜尋,以網址去重後建樹,呼叫 prefetcher.prefetch,再計分、排序。 */
    protected List<WebTree> runRound(List<String> queries, KeywordList kws) {
        // TODO(Member C, Stage 3)
        throw new UnsupportedOperationException("TODO(Member C): SearchService.runRound");
    }

    /** 把一個網站建成 WebTree(根節點 + 最多 maxLinks 個子節點)。 */
    protected WebTree buildTree(String url, String title) {
        // TODO(Member C, Stage 3)
        throw new UnsupportedOperationException("TODO(Member C): SearchService.buildTree");
    }

    /** 所有 WebPage 都由此建立,測試時可覆寫成不連網的版本。 */
    protected WebPage newPage(String url, String title) {
        return new WebPage(url, title);
    }

    /** 未經重新排序的搜尋引擎結果(網址 → 標題),給 compare.html 使用。 */
    public LinkedHashMap<String, String> googleResults(String q) throws IOException {
        return googleQuery.query(q);
    }
}
