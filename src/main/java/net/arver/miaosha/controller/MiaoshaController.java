package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.service.MiaoshaService;
import net.arver.miaosha.service.OrderService;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 秒杀controller.
 */
@RequestMapping("miaosha")
@Controller
public class MiaoshaController {

    /**
     * 商品服务.
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 订单服务.
     */
    @Autowired
    OrderService orderService;

    /**
     * 秒杀服务.
     */
    @Autowired
    MiaoshaService miaoshaService;

    /**
     * 秒杀.
     * @param model 页面模型
     * @param user 用户
     * @param goodsId 商品id
     * @return 订单信息页面
     */
    @RequestMapping("do_miaosha")
    public String doMiaosha(final Model model, final MiaoshaUser user, @RequestParam("goodsId") final long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }

        final GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        final Integer stockCount = goodsVo.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errMsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        // 减库存 下订单 写入秒杀订单
        final OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVo);
        return "order_detail";
    }
}
