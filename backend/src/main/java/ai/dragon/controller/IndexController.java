package ai.dragon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ai.dragon.service.DatabaseService;

@Controller
public class IndexController {
    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/awesome")
    public String showUserList() {
        databaseService.openDatabase();
        return "index";
    }
}
