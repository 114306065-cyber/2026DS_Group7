package edu.nccu.moviefinder.cli;

import edu.nccu.moviefinder.model.Keyword;
import edu.nccu.moviefinder.model.KeywordList;
import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import edu.nccu.moviefinder.service.Ranker;
import edu.nccu.moviefinder.service.SubLinkCrawler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stage 1 / Stage 2 的命令列展示,不需要網站介面。
 * 用法:StageDemo page|site "movie:3,director:2" url1 url2 ...
 *   page = Stage 1,只評單一網頁
 *   site = Stage 2,連同子網頁一起評分並印出樹
 */
public final class StageDemo {

    private StageDemo() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3 || !(args[0].equals("page") || args[0].equals("site"))) {
            System.err.println("Usage: StageDemo page|site \"keyword:weight,...\" url1 [url2 ...]");
            System.exit(1);
        }
        boolean site = args[0].equals("site");
        KeywordList kws = parseKeywords(args[1]);
        List<String> urls = Arrays.asList(args).subList(2, args.length);

        SubLinkCrawler crawler = new SubLinkCrawler(5);
        List<WebTree> trees = new ArrayList<>();
        for (String url : urls) {
            WebNode root = new WebNode(new WebPage(url, url));
            if (site) {
                for (String link : crawler.getSubLinks(url)) {
                    root.addChild(new WebNode(new WebPage(link, link)));
                }
            }
            WebTree tree = new WebTree(root);
            tree.setPostOrderScore(kws);
            trees.add(tree);
        }

        new Ranker().sort(trees);

        int rank = 1;
        for (WebTree tree : trees) {
            WebPage page = tree.getRoot().getWebPage();
            System.out.printf("#%d  score=%.1f  %s  %s%n", rank++, tree.getTotalScore(), page.getUrl(), page.getCounts());
            if (site) {
                tree.eulerPrintTree();
            }
        }
    }

    private static KeywordList parseKeywords(String spec) {
        KeywordList kws = new KeywordList();
        for (String item : spec.split(",")) {
            String[] parts = item.split(":");
            kws.add(new Keyword(parts[0].trim(), Double.parseDouble(parts[1].trim())));
        }
        return kws;
    }
}
