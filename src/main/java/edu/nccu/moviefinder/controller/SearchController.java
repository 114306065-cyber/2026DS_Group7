package edu.nccu.moviefinder.controller;

import edu.nccu.moviefinder.model.SearchResult;
import edu.nccu.moviefinder.service.SearchService;
import edu.nccu.moviefinder.service.SearchUnavailableException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 只負責轉交請求,不處理商業邏輯。
 * TODO(Member E, Stage 5): 搜尋失敗或沒有結果時,顯示友善的錯誤畫面。
 */
@Controller
public class SearchController {
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q, Model model, HttpServletResponse response) {
        if (q.isBlank()) {
            return "redirect:/";
        }
        ArrayList<SearchResult> results = new ArrayList<>();
        try {
            results = searchService.search(q);
        } catch (SearchUnavailableException e) {
            model.addAttribute("searchError", true);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        model.addAttribute("q", q);
        model.addAttribute("results", results);
        return "result";
    }

    @GetMapping("/compare")
    public String compare(@RequestParam(defaultValue = "") String q, Model model, HttpServletResponse response) {
        if (q.isBlank()) {
            return "redirect:/";
        }
        LinkedHashMap<String, String> google = new LinkedHashMap<>();
        try {
            google = searchService.googleResults(q);
        } catch (IOException | IllegalStateException e) {
            log.warn("Tavily search failed: {}", e.getMessage());
            model.addAttribute("googleError", true);
        }
        ArrayList<SearchResult> results = new ArrayList<>();
        try {
            results = searchService.search(q);
        } catch (SearchUnavailableException e) {
            model.addAttribute("resultsError", true);
        }
        if (model.containsAttribute("googleError") && model.containsAttribute("resultsError")) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        model.addAttribute("q", q);
        model.addAttribute("google", google);
        model.addAttribute("results", results);
        return "compare";
    }
}
