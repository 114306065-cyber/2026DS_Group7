package edu.nccu.moviefinder.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * 抓取一個網頁並計算關鍵字次數(由 Stage 0 / HW3 移植,Boyer-Moore 邏輯未變)。
 * 每個網頁一個實例,因此不是 Spring bean。
 *
 * TODO(Member B):
 *  1. 改用 Jsoup 抓取並只取內文,目前連 HTML 標籤和 script 都會計入。
 *  2. 英文關鍵字要比對完整單字,目前 "cat" 會命中 "category"、"AI" 會命中 "email"。
 *  3. 設定連線逾時與 User-Agent,目前沒有逾時,一個卡住的網站會拖住整次搜尋。
 *  4. last() 每次呼叫 BoyerMoore 都重建 65536 格陣列,應該每個關鍵字只建一次。
 *  5. fetchContent 用字串 + 串接是 O(n²),改用 StringBuilder 或 Jsoup。
 */
public class WordCounter {
    private final String urlStr;
    private String content;
    private String upperContent;

    public WordCounter(String urlStr) {
        this.urlStr = urlStr;
    }

    /** 測試用:直接給內容,不連網。 */
    public WordCounter(String urlStr, String content) {
        this.urlStr = urlStr;
        this.content = content;
    }

    protected String fetchContent() throws IOException {
        URL url = new URL(this.urlStr);
        URLConnection conn = url.openConnection();
        InputStream in = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String retVal = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            retVal = retVal + line + "\n";
        }
        return retVal;
    }

    /** 網頁內容,只抓一次。 */
    public String getContent() throws IOException {
        if (content == null) {
            content = fetchContent();
        }
        return content;
    }

    public int BoyerMoore(String T, String P, int start) {
        int n = T.length();
        int m = P.length();

        if (m == 0) {
            return -1;
        }

        int[] L = last(P);

        int i = start + m - 1;
        int j = m - 1;

        while (i <= n - 1) {
            if (T.charAt(i) == P.charAt(j)) {
                if (j == 0) {
                    return i;
                }
                i--;
                j--;
            } else {
                int l = L[T.charAt(i)];
                i += m - Math.min(j, l + 1);
                j = m - 1;
            }
        }

        return -1;
    }

    public int[] last(String P) {
        int[] L = new int[Character.MAX_VALUE + 1];

        Arrays.fill(L, -1);

        for (int i = 0; i < P.length(); i++) {
            L[P.charAt(i)] = i;
        }

        return L;
    }

    /** 不分大小寫計算關鍵字次數。 */
    public int countKeyword(String keyword) throws IOException {
        if (upperContent == null) {
            upperContent = getContent().toUpperCase();
        }

        keyword = keyword.toUpperCase();

        int retVal = 0;
        int i = 0;
        while (i < upperContent.length()) {
            int j = BoyerMoore(upperContent, keyword, i);
            if (j == -1) {
                break;
            }
            retVal++;
            i = j + 1;
        }
        return retVal;
    }
}
