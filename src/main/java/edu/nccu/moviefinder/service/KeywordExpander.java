package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.Keyword;
import edu.nccu.moviefinder.model.WebPage;
import java.util.List;
import org.springframework.stereotype.Service;

/** 補上電影相關字詞,並從排名靠前的網頁找出新的關鍵字。 */
@Service
public class KeywordExpander {

    /** "cat" → ["cat movie", "cat film", "cat movie review"]。 */
    public List<String> expand(String q) {
        // TODO(Member D, Stage 3)
        throw new UnsupportedOperationException("TODO(Member D): KeywordExpander.expand");
    }

    /**
     * 從 topPages 的內文找出相關關鍵字(TF-IDF 或共現分析)。
     * 規則:排除 stopwords.txt 內的字和 originalQuery 本身;權重必須介於
     * 0 與 KeywordList.USER_WEIGHT 之間(不含),避免主題偏離使用者的原始意圖。
     * 抓不到內文的網頁直接略過。
     */
    public List<Keyword> deriveKeywords(List<WebPage> topPages, String originalQuery) {
        // TODO(Member D, Stage 4)
        throw new UnsupportedOperationException("TODO(Member D): KeywordExpander.deriveKeywords");
    }
}
