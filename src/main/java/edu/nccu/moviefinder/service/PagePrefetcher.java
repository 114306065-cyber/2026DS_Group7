package edu.nccu.moviefinder.service;

import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebTree;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 在計分前先把所有網頁抓好。WordCounter 會保存抓到的內容,所以之後計分不會再連網。
 *
 * 目前是依序抓取,可以正常使用但很慢。
 * TODO(Member B, 第二輪): 改成用執行緒池平行抓取,並設定整次搜尋的時間上限。
 * 第二輪搜尋不要重抓已經抓過的網址,也不要重試已經失敗的網址。
 */
@Service
public class PagePrefetcher {

    /** 單一網頁失敗時略過該網頁,不可中斷其他網頁。 */
    public void prefetch(List<WebTree> trees) {
        for (WebTree tree : trees) {
            fetch(tree.getRoot());
        }
    }

    private void fetch(WebNode node) {
        try {
            node.getWebPage().getContent();
        } catch (IOException e) {
            // 失敗的網頁在計分時會以 0 分計。
        }
        for (WebNode child : node.getChildren()) {
            fetch(child);
        }
    }
}
