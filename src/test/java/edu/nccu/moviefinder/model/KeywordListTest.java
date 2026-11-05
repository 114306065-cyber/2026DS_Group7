package edu.nccu.moviefinder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KeywordListTest {

    @Test
    void userTermsComeFirstWithTheHighestWeight() {
        KeywordList kws = KeywordList.defaultsFor("cat food");

        assertEquals("cat", kws.getList().get(0).getName());
        assertEquals("food", kws.getList().get(1).getName());
        assertEquals(KeywordList.USER_WEIGHT, kws.getList().get(0).getWeight());
        for (Keyword k : kws.getList().subList(2, kws.getList().size())) {
            assertTrue(k.getWeight() < KeywordList.USER_WEIGHT, k + " must stay below the user weight");
        }
    }

    @Test
    void loadsNegativeNoiseKeywords() {
        assertTrue(KeywordList.defaultsFor("cat").getList().stream().anyMatch(k -> k.getWeight() < 0));
    }
}
