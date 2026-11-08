package edu.nccu.moviefinder.controller;

import edu.nccu.moviefinder.service.SearchService;
import java.io.IOException;
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
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q, Model model) {
        if (q.isBlank()) {
            return "redirect:/";
        }
        model.addAttribute("q", q);
        model.addAttribute("results", searchService.search(q));
        return "result";
    }

    @GetMapping("/compare")
    public String compare(@RequestParam(defaultValue = "") String q, Model model) throws IOException {
        if (q.isBlank()) {
            return "redirect:/";
        }
        model.addAttribute("q", q);
        model.addAttribute("google", searchService.googleResults(q));
        model.addAttribute("results", searchService.search(q));
        return "compare";
    }
}
