package edu.nccu.moviefinder.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 一次搜尋使用的關鍵字集合:使用者關鍵字(權重最高)+ 電影領域關鍵字 + 雜訊關鍵字(負權重)。
 * 每次搜尋都要建立新的實例,不可存成 Spring bean 的欄位,否則同時搜尋的使用者會互相污染。
 */
public class KeywordList {
    /** 使用者輸入的關鍵字權重,必須高於 keywords.csv 內的所有權重。 */
    public static final double USER_WEIGHT = 5.0;

    private final ArrayList<Keyword> list = new ArrayList<>();

    public void add(Keyword k) {
        list.add(k);
    }

    public ArrayList<Keyword> getList() {
        return list;
    }

    /** 建立一次搜尋的初始關鍵字:查詢字串的每個詞,加上 keywords.csv 的內容。 */
    public static KeywordList defaultsFor(String query) {
        KeywordList kws = new KeywordList();
        for (String term : query.trim().split("\\s+")) {
            if (!term.isEmpty()) {
                kws.add(new Keyword(term, USER_WEIGHT));
            }
        }
        try (InputStream in = KeywordList.class.getResourceAsStream("/keywords.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                kws.add(new Keyword(parts[0].trim(), Double.parseDouble(parts[1].trim())));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read keywords.csv", e);
        }
        return kws;
    }
}
