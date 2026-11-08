package edu.nccu.moviefinder.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 取得搜尋引擎的結果。類別名稱沿用 proposal,實際使用 Tavily Search API,
 * 因為 Google 自 2026 年起不再開放新用戶申請搜尋 API(見 HW8)。
 */
@Service
public class GoogleQuery {
    private final String apiKey;
    private final int maxResults;

    public GoogleQuery(@Value("${tavily.api-key:}") String apiKey,
                       @Value("${tavily.max-results:10}") int maxResults) {
        this.apiKey = apiKey;
        this.maxResults = maxResults;
    }

    /**
     * 回傳「網址 → 標題」,順序就是 Tavily 回傳的順序。
     * apiKey 為空時丟出 IllegalStateException,並在訊息中提示設定 TAVILY_API_KEY。
     */
    public LinkedHashMap<String, String> query(String q) throws IOException {
        // TODO(Member E, HW8): POST https://api.tavily.com/search,Header 帶 Bearer 金鑰,
        // body 帶 query 和 max_results。處理連線失敗與額度用盡,再把回應交給 parse。
        throw new UnsupportedOperationException("TODO(Member E): GoogleQuery.query");
    }

    /** 解析 Tavily 回傳的 JSON,取 results 陣列中的 url 和 title。沒有 results 時回傳空的 map。 */
    public LinkedHashMap<String, String> parse(String json) {
        // TODO(Member E, HW8)
        throw new UnsupportedOperationException("TODO(Member E): GoogleQuery.parse");
    }

    protected String getApiKey() {
        return apiKey;
    }

    protected int getMaxResults() {
        return maxResults;
    }
}
