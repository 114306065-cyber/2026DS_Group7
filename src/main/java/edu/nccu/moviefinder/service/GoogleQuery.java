package edu.nccu.moviefinder.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 透過 SerpApi 取得 Google 搜尋結果。 */
@Service
public class GoogleQuery {
    private final String apiKey;
    private final int num;

    public GoogleQuery(@Value("${serpapi.key:}") String apiKey, @Value("${serpapi.num:20}") int num) {
        this.apiKey = apiKey;
        this.num = num;
    }

    /**
     * 回傳「網址 → 標題」,順序就是 Google 的排名。
     * apiKey 為空時丟出 IllegalStateException,並在訊息中提示設定 SERPAPI_KEY。
     */
    public LinkedHashMap<String, String> query(String q) throws IOException {
        // TODO(Member C, Stage 3): 呼叫 SerpApi,處理失敗與額度用盡,再交給 parse
        throw new UnsupportedOperationException("TODO(Member C): GoogleQuery.query");
    }

    /** 解析 SerpApi 回傳的 JSON。沒有 organic_results 時回傳空的 map。 */
    public LinkedHashMap<String, String> parse(String json) {
        // TODO(Member C, Stage 3)
        throw new UnsupportedOperationException("TODO(Member C): GoogleQuery.parse");
    }

    protected String getApiKey() {
        return apiKey;
    }

    protected int getNum() {
        return num;
    }
}
