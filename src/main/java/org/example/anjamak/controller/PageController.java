package org.example.anjamak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping
    public String index() {
        return "index";
    }
    @GetMapping("/start")
    public String start() {
        return "start";
    }
}
