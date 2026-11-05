package edu.nccu.moviefinder.model;

import java.util.HashMap;

/** 傳給前端顯示的一筆結果。 */
public class SearchResult {
    private final int rank;
    private final String title;
    private final String url;
    private final double score;
    /** 每個關鍵字貢獻的分數(含子網頁折扣後),全部加總必須等於 score。 */
    private final HashMap<String, Double> breakdown;
    private final WebTree tree;

    public SearchResult(int rank, String title, String url, double score,
                        HashMap<String, Double> breakdown, WebTree tree) {
        this.rank = rank;
        this.title = title;
        this.url = url;
        this.score = score;
        this.breakdown = breakdown;
        this.tree = tree;
    }

    public int getRank() {
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public double getScore() {
        return score;
    }

    public HashMap<String, Double> getBreakdown() {
        return breakdown;
    }

    public WebTree getTree() {
        return tree;
    }
}
