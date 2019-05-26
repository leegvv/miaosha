package net.arver.miaosha.controller;

import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.MiaoshaUserService;
import net.arver.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 登录controller
 */
@Controller
@RequestMapping("login")
public class LoginController {

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    /**
     * 返回登录页面.
     * @return 登录页面
     */
    @RequestMapping("to_login")
    public String toLogin() {
        return "login";
    }

    /**
     * 登录操作.
     * @param response responese
     * @param loginVo 登录参数
     * @return 登录结果
     */
    @RequestMapping("do_login")
    @ResponseBody
    public Result<Boolean> doLogin(final HttpServletResponse response, @Valid final LoginVo loginVo) {
        LOGGER.info(loginVo.toString());

        miaoshaUserService.login(response, loginVo);
        return Result.success(Boolean.TRUE);
    }


}
