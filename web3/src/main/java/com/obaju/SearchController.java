package com.obaju;

import com.obaju.model.Product;
import com.obaju.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam(value = "keyword", required = false) String q,
                         @RequestParam(value = "reverse", required = false,
                                 defaultValue = "false") boolean reverse,
                         Model model){
        List<Product> searchResult = null;
        searchResult = searchService.search(q.toLowerCase(), reverse);
        model.addAttribute("results", searchResult);
        model.addAttribute("keyword", q);
        return "search";
    }
}