package ai.dragon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/ok")
    public String showUserList(Model model) {
        return "index";
    }
}
