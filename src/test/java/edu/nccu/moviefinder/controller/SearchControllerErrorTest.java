package edu.nccu.moviefinder.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.nccu.moviefinder.service.SearchService;
import edu.nccu.moviefinder.service.SearchUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** 搜尋服務無法使用和真的沒有結果,畫面要顯示不同的訊息。 */
@WebMvcTest(SearchController.class)
class SearchControllerErrorTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    void unavailableSearchShowsAnErrorInsteadOfNoResults() throws Exception {
        when(searchService.search(anyString()))
                .thenThrow(new SearchUnavailableException("down", new IOException("HTTP 401")));

        mvc.perform(get("/search").param("q", "dog"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(containsString("temporarily unavailable")))
                .andExpect(content().string(not(containsString("No results found"))));
    }

    @Test
    void anEmptySearchStillSaysNoResults() throws Exception {
        when(searchService.search(anyString())).thenReturn(new ArrayList<>());

        mvc.perform(get("/search").param("q", "zzzz"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No results found")))
                .andExpect(content().string(not(containsString("temporarily unavailable"))));
    }

    @Test
    void errorMessageIsTranslated() throws Exception {
        when(searchService.search(anyString()))
                .thenThrow(new SearchUnavailableException("down", new IOException("HTTP 401")));

        mvc.perform(get("/search").param("q", "dog").param("lang", "zh"))
                .andExpect(content().string(containsString("搜尋服務暫時無法使用")));
    }

    @Test
    void compareMarksOnlyTheColumnThatFailed() throws Exception {
        when(searchService.googleResults(anyString())).thenThrow(new IOException("HTTP 500"));
        when(searchService.search(anyString())).thenReturn(new ArrayList<>());

        mvc.perform(get("/compare").param("q", "dog"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("temporarily unavailable")))
                .andExpect(content().string(containsString("No results.")));
    }

    @Test
    void compareWorksWhenBothColumnsAreEmpty() throws Exception {
        when(searchService.googleResults(anyString())).thenReturn(new LinkedHashMap<>());
        when(searchService.search(anyString())).thenReturn(new ArrayList<>());

        mvc.perform(get("/compare").param("q", "dog"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("temporarily unavailable"))));
    }
}
