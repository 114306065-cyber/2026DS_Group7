package edu.nccu.moviefinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.nccu.moviefinder.model.WebNode;
import edu.nccu.moviefinder.model.WebPage;
import edu.nccu.moviefinder.model.WebTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


class RankerTest {
    private final Ranker ranker = new Ranker();

    private static WebTree tree(double score) {
        return new WebTree(WebNode.fixture(WebPage.fixture("https://e.example", "t", new HashMap<>(), score), score));
    }

    private static List<WebTree> trees(double... scores) {
        List<WebTree> trees = new ArrayList<>();
        for (double s : scores) {
            trees.add(tree(s));
        }
        return trees;
    }

    private static void assertDescending(List<WebTree> trees) {
        for (int i = 1; i < trees.size(); i++) {
            assertTrue(trees.get(i - 1).getTotalScore() >= trees.get(i).getTotalScore(), "out of order at " + i);
        }
    }

    @Test
    void sortsDescendingIncludingNegativeScoresAndTies() {
        List<WebTree> trees = trees(3, -2, 9, 3, 0);

        ranker.sort(trees);

        assertEquals(9, trees.get(0).getTotalScore());
        assertEquals(-2, trees.get(4).getTotalScore());
        assertDescending(trees);
    }

    @Test
    void handlesEmptyAndSingleElementLists() {
        ranker.sort(new ArrayList<>());
        List<WebTree> one = trees(1);

        ranker.sort(one);

        assertEquals(1, one.size());
    }

    @Test
    void sortsLargeRandomInput() {
        Random random = new Random(7);
        List<WebTree> trees = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            trees.add(tree(random.nextInt(100)));
        }

        ranker.sort(trees);

        assertEquals(2000, trees.size());
        assertDescending(trees);
    }

    @Test
    void handlesAlreadySortedInput() {
        List<WebTree> trees = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            trees.add(tree(i));
        }

        ranker.sort(trees);

        assertDescending(trees);
    }
}
