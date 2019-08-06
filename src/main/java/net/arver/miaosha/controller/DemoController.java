package net.arver.miaosha.controller;

import net.arver.miaosha.domain.User;
import net.arver.miaosha.rabbitmq.MQSender;
import net.arver.miaosha.redis.RedisService;
import net.arver.miaosha.redis.UserKey;
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
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    /*@RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
        mqSender.sendHeader("hello header");
        return Result.success("Hello Header");
    }

    @RequestMapping("mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
        mqSender.sendFanout("hello fanout");
        return Result.success("Hello fanout");
    }

    @RequestMapping("mq/topic")
    @ResponseBody
    public Result<String> topic() {
        mqSender.sendTopic("hello world");
        return Result.success("Hello world");
    }

    @RequestMapping("mq")
    @ResponseBody
    public Result<String> mq() {
        mqSender.send("Hello Arver");
        return Result.success("Hello arver");
    }*/

    @RequestMapping("/thymeleaf")
    public String thymeleaf(final Model model) {
        model.addAttribute("name", "arver");
        return "thymeleaf";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Object dbGet() {
        final User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/tx")
    @ResponseBody
    public void tx() {
        userService.tx();
    }

    @RequestMapping("redisSet")
    @ResponseBody
    public Object redisSet() {
        final User user = new User();
        user.setId(1);
        user.setName("张三");
        redisService.set(UserKey.BY_ID, user.getId().toString(), 1);
        return Result.success(true);
    }

    @RequestMapping("redisGet")
    @ResponseBody
    public Object redisGet() {
        final User user = new User();
        user.setId(2);
        user.setName("李四");
        redisService.set(UserKey.BY_ID, user.getId().toString(), user);
        final User value = redisService.get(UserKey.BY_ID, user.getId().toString(), User.class);
        return Result.success(value);
    }
}
