package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户controller.
 */
@RequestMapping("user")
@Controller
public class UserController {

    /**
     * 信息.
     * @param user 用户信息
     * @return 信息
     */
    @RequestMapping("info")
    @ResponseBody
    public Result<MiaoshaUser> info(final MiaoshaUser user) {
        return Result.success(user);
    }
}
