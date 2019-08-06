package net.arver.miaosha.controller;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.result.CodeMsg;
import net.arver.miaosha.result.Result;
import net.arver.miaosha.service.GoodsService;
import net.arver.miaosha.service.OrderService;
import net.arver.miaosha.vo.GoodsVo;
import net.arver.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订单controller.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    /**
     * 详情.
     * @param user 用户
     * @param orderId 订单id
     * @return dingdan 详情
     */
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(final MiaoshaUser user, @RequestParam("orderId")final long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        final OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        final Long goodsId = order.getGoodsId();
        final GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        final OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
