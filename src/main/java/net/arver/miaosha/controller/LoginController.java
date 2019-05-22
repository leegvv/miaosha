package net.arver.miaosha.controller;

import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.MiaoshaUserService;
import net.arver.miaosha.util.ValidatorUtil;
import net.arver.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @param loginVo 登录参数
     * @return 登录结果
     */
    @RequestMapping("do_login")
    @ResponseBody
    public Result<CodeMsg> doLogin(final LoginVo loginVo) {
        LOGGER.info(loginVo.toString());
        String mobile = loginVo.getMobile();
        final String password = loginVo.getPassword();
        if (StringUtils.isBlank(mobile)) {
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if (StringUtils.isBlank(password)) {
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (!ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMsg.MOBILE_ERROR);
        }

        final CodeMsg cm = miaoshaUserService.login(loginVo);
        if (cm.getCode() == 0) {
            return Result.success(CodeMsg.SUCCESS);
        }
        return Result.error(cm);
    }


}
