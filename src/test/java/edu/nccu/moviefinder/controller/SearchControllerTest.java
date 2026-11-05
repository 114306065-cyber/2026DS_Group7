package edu.nccu.moviefinder.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** 在 mock profile 下確認三個頁面都能正常渲染。 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
class SearchControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void indexShowsTheSearchBar() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"q\"")));
    }

    @Test
    void searchRendersRanksBreakdownAndSubpageTree() throws Exception {
        mvc.perform(get("/search").param("q", "cat"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Example Movie Reviews")))
                .andExpect(content().string(containsString("review")))
                .andExpect(content().string(containsString("Subpage")));
    }

    @Test
    void compareRendersGoogleAndOurResults() throws Exception {
        mvc.perform(get("/compare").param("q", "cat"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Example News about cat")))
                .andExpect(content().string(containsString("Example Cinema Blog")));
    }

    @Test
    void blankQueryReturnsToTheHomePage() throws Exception {
        mvc.perform(get("/search").param("q", "  "))
                .andExpect(redirectedUrl("/"));
    }
}
