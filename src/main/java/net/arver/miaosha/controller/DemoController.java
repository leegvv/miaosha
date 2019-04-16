package net.arver.miaosha.controller;

import net.arver.miaosha.domain.User;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private UserService userService;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(final Model model) {
        model.addAttribute("name", "arver");
        return "thymeleaf";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Object dbGet () {
        final User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/tx")
    @ResponseBody
    public void tx() {
        userService.tx();
    }
}
