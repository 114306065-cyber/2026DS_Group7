package edu.nccu.moviefinder.model;

import edu.nccu.moviefinder.service.WordCounter;
import java.io.IOException;
import java.util.HashMap;

/** 一個網頁:負責數關鍵字,並算出這一頁自己的分數。 */
public class WebPage {
    private final String url;
    private final String title;
    private final WordCounter counter;
    private HashMap<String, Integer> counts = new HashMap<>();
    private double score;

    public WebPage(String url, String title) {
        this(url, title, new WordCounter(url));
    }

    /** 測試用:傳入已有內容的 WordCounter,就不必連網。 */
    public WebPage(String url, String title, WordCounter counter) {
        this.url = url;
        this.title = title;
        this.counter = counter;
    }

    /** 假資料用:直接指定次數與分數,讓前端不必等後端完成。 */
    public static WebPage fixture(String url, String title, HashMap<String, Integer> counts, double score) {
        WebPage page = new WebPage(url, title, new WordCounter(url, ""));
        page.counts = counts;
        page.score = score;
        return page;
    }

    /**
     * WebPage.score = Σ count(k_i) × weight(k_i),並把每個關鍵字的次數記入 counts。
     * 重複呼叫時不可累加上一次的結果。
     */
    public void setScore(KeywordList kws) throws IOException {
        // TODO(Member A, Stage 1)
        throw new UnsupportedOperationException("TODO(Member A): WebPage.setScore");
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public double getScore() {
        return score;
    }

    public HashMap<String, Integer> getCounts() {
        return counts;
    }

    /** 網頁文字內容,供 KeywordExpander.deriveKeywords 使用。 */
    public String getContent() throws IOException {
        return counter.getContent();
    }
}
