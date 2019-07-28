package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    /**
     * 返回商品详情页.
     * @param model 页面模型
     * @param user 登录用户
     * @param goodsId 商品id
     * @return 详情页
     */
    @RequestMapping("/to_detail/{goodsId}")
    public String detail(final Model model, final MiaoshaUser user, @PathVariable("goodsId") final long goodsId) {
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        final long startAt = goods.getStartDate().getTime();
        final long endAt = goods.getEndDate().getTime();
        final long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        long remainSeconds = 0;
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = TimeUnit.MILLISECONDS.toSeconds(startAt - now);
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }

}
