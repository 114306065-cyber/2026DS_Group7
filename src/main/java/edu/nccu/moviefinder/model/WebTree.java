package edu.nccu.moviefinder.model;

/** 一個網站:根節點是搜尋結果頁,子節點是爬到的子網頁。 */
public class WebTree {
    private final WebNode root;

    public WebTree(WebNode root) {
        this.root = root;
    }

    /**
     * 後序走訪:先算完所有子節點,再算自己。
     * 單一網頁抓取失敗(IOException)時,該頁以 0 分計,不可中斷整棵樹。
     */
    public void setPostOrderScore(KeywordList kws) {
        // TODO(Member A, Stage 2)
        throw new UnsupportedOperationException("TODO(Member A): WebTree.setPostOrderScore");
    }

    /**
     * Euler Tour 印出樹狀結構。每個節點一行,以 getDepth() × 2 個空白縮排,
     * 內容包含網址、節點分數,以及每個關鍵字的次數(Stage 2 規定要輸出)。
     */
    public void eulerPrintTree() {
        // TODO(Member A, Stage 2)
        throw new UnsupportedOperationException("TODO(Member A): WebTree.eulerPrintTree");
    }

    public double getTotalScore() {
        return root.getNodeScore();
    }

    public WebNode getRoot() {
        return root;
    }
}
