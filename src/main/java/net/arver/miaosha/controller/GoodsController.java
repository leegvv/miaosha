package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 登录controller
 */
@Controller
@RequestMapping("goods")
public class GoodsController {

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    /**
     * 商品服务.
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 返回登录页面.
     * @param model 页面模型
     * @param user 用户信息
     * @return 登录页面
     */
    @RequestMapping("to_list")
    public String toList(final Model model, final MiaoshaUser user) {
        model.addAttribute("user", user);
        final List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

}
