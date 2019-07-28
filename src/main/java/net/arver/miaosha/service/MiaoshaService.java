package net.arver.miaosha.service;

import net.arver.miaosha.domain.MiaoshaUser;
import net.arver.miaosha.domain.OrderInfo;
import net.arver.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 秒杀服务.
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * 秒杀.
     * @param user 用户信息
     * @param goods 商品vo
     * @return 订单信息
     */
    public OrderInfo miaosha(final MiaoshaUser user, final GoodsVo goods) {
        //减库存、下订单、写入秒杀订单
        goodsService.reduceStock(goods);
        return orderService.createOrder(user, goods);

    }
}
