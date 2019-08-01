package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaOrder;
import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.service.MiaoshaService;
import net.arver.miaosha.service.OrderService;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * 秒杀 2087 2948.
     * @param user 用户
     * @param goodsId 商品id
     * @return 订单信息页面
     */
    @RequestMapping(value = "do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> doMiaosha(final MiaoshaUser user, @RequestParam("goodsId") final long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        final GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        final Integer stockCount = goodsVo.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        final MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 减库存 下订单 写入秒杀订单
        final OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        return Result.success(orderInfo);
    }
}
