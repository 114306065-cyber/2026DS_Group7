package edu.nccu.moviefinder.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 找出一個網頁的子網頁,用來建立兩層的網站樹。 */
@Service
public class SubLinkCrawler {
    private final int maxLinks;
    private final int maxDepth = 1;

    public SubLinkCrawler(@Value("${crawler.max-links:5}") int maxLinks) {
        this.maxLinks = maxLinks;
    }

    /** 抓取 url 的 HTML 後,交給 extractLinks 取出子網頁。 */
    public List<String> getSubLinks(String url) throws IOException {
        // TODO(Member B, Stage 2): 用 Jsoup 抓 HTML,並設定逾時與 User-Agent
        throw new UnsupportedOperationException("TODO(Member B): SubLinkCrawler.getSubLinks");
    }

    /**
     * 從 HTML 取出最多 maxLinks 個子網頁的絕對網址,順序依頁面出現順序。
     * 規則:相對網址要轉成絕對網址、去掉 #fragment、只留同網域的 http(s) 連結、
     * 用 HashSet 去除重複,且不可包含 baseUrl 自己。
     */
    public List<String> extractLinks(String html, String baseUrl) {
        // TODO(Member B, Stage 2)
        throw new UnsupportedOperationException("TODO(Member B): SubLinkCrawler.extractLinks");
    }

    public int getMaxLinks() {
        return maxLinks;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
